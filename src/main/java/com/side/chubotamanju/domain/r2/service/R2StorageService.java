package com.side.chubotamanju.domain.r2.service;

import com.side.chubotamanju.domain.r2.service.dto.MissionPresignedURLServiceDto;
import com.side.chubotamanju.global.config.cloudflare.R2Properties;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.CopyObjectRequest;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.HexFormat;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class R2StorageService {

    private final S3Client r2S3Client;
    private final S3Presigner r2S3Presigner;

    private final R2Properties properties;

    /**
     * 길드 아이디를 이용해서 R2 Object 저장 경로 생성
     * - 웹에서는 임시 폴더로 업로드할 때, 이미 UUID를 포함하므로 제거
     * Todo - 디스코드 봇과 buildObjectKey가 현재 다르므로 통일시켜야함
     * @param guildId
     * @param originalFileName
     * @return
     */
    private String buildObjectKey(String guildId, String originalFileName) {
//        String safeName = originalFileName == null ? "proof" : originalFileName.replaceAll("[^a-zA-Z0-9._-]", "_");
        String datePath = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
        return "bounty/%s/%s/%s".formatted(guildId, datePath, originalFileName);
    }

    /**
     * URL 목록을 받아 지정된 새 폴더 경로로 일괄 이동시킨다.
     * - 임시폴더에 저장한 파일을 실사용폴더로 이동
     * - 버킷의 객체는 바로 이동이 불가능하다
     * - 기존 객체 복사 -> 실사용 폴더 경로에 붙여넣기 -> 기존 객체 삭제 순으로 진행
     * @param guildId 미션이 속하는 길드 이름
     * @param fileUrlList 이동할 객체들의 R2 URL 목록
     */
    public Map<String, String> resolveTempFiles(String guildId, List<String> fileUrlList) {

        Map<String, String> newFileUrlMap = new HashMap<>();

        for (String oldFileUrl : fileUrlList) {
            String fileName = oldFileUrl.substring("tmp/".length());
            String newFileUrl = buildObjectKey(guildId, fileName);

            //임시폴더의 파일을 실사용폴더 경로로 복사
            CopyObjectRequest copyReq = CopyObjectRequest.builder()
                    .sourceBucket(properties.getBucket())
                    .sourceKey(oldFileUrl)
                    .destinationBucket(properties.getBucket())
                    .destinationKey(newFileUrl)
                    .build();
            r2S3Client.copyObject(copyReq);

            //임시폴더의 파일 제거
            DeleteObjectRequest deleteReq = DeleteObjectRequest.builder()
                    .bucket(properties.getBucket())
                    .key(oldFileUrl)
                    .build();
            r2S3Client.deleteObject(deleteReq);

            newFileUrlMap.put(oldFileUrl, newFileUrl);
        }

        return newFileUrlMap;

    }

    // 단일 파일 업로드 Presigned URL 생성
    public MissionPresignedURLServiceDto generatePresignedUploadUrl(String objectKey) {
        Duration signatureDuration = Duration.ofSeconds(properties.getSignedUrlTtlSeconds());

        PutObjectPresignRequest presignRequest = PutObjectPresignRequest.builder()
                .signatureDuration(signatureDuration)
                .putObjectRequest(builder -> builder
                        .bucket(properties.getBucket())
                        .key(objectKey)
                        .build())
                .build();

        PresignedPutObjectRequest presignedRequest = r2S3Presigner.presignPutObject(presignRequest);

        return MissionPresignedURLServiceDto.builder()
                .uploadUrl(presignedRequest.url().toString())
                .method(presignedRequest.httpRequest().method().name())
                .build();
    }

    /**
     * R2 Object 접근 Url 생성(유효시간 설정)
     * @param fileUrl
     * @return
     */
    public String generateObjectAccessUrl(String fileUrl) {
        if (fileUrl == null || fileUrl.isBlank()) {
            return "";
        }

        String baseUrl = properties.getPublicBaseUrl();
        String secret = properties.getWorkerSignSecret();

        //접근 만료시간
        long exp = Instant.now().getEpochSecond() + Math.max(30, properties.getSignedUrlTtlSeconds());
        String payload = fileUrl + ":" + exp;
        String sig = hmacSha256Hex(secret, payload);

        String objectAccessUrl = "%s/object?key=%s&exp=%d&sig=%s".formatted(
                baseUrl,
                URLEncoder.encode(fileUrl, StandardCharsets.UTF_8),
                exp,
                sig);

        return objectAccessUrl;
    }

    private String hmacSha256Hex(String secret, String payload) {
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
            return HexFormat.of().formatHex(mac.doFinal(payload.getBytes(StandardCharsets.UTF_8)));
        } catch (Exception e) {
            throw new RuntimeException("서명 URL 생성 중 오류가 발생했어.", e);
        }
    }
}
