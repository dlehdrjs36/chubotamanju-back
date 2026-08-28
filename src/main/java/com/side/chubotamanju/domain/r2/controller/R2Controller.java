package com.side.chubotamanju.domain.r2.controller;

import com.side.chubotamanju.domain.r2.dto.request.MissionPresignedURLCreateRequest;
import com.side.chubotamanju.domain.r2.dto.response.MissionPresignedURLCreateResponse;
import com.side.chubotamanju.domain.r2.service.R2StorageService;
import com.side.chubotamanju.domain.r2.service.dto.MissionPresignedURLServiceDto;
import com.side.chubotamanju.global.common.ApiResult;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class R2Controller {

    private final R2StorageService r2StorageService;

    /**
     * 미션 보고용도의 presigned-url 발급
     * - 임시 경로로 첨부파일 업로드 이후에 실제 접수보고 API에서 임시 경로의 첨부파일을 실제 폴더로 옮긴다.
     * @param missionPresignedURLCreateRequest
     * @return
     */
    @PostMapping("/missions/presigned-url")
    public ResponseEntity<ApiResult<MissionPresignedURLCreateResponse>> generatePresignUrl(@RequestBody MissionPresignedURLCreateRequest missionPresignedURLCreateRequest) {
        String missionId = missionPresignedURLCreateRequest.getMissionId();
        String fileName = missionPresignedURLCreateRequest.getFileName();
        String objectKey = "tmp/" + missionId + "_" + UUID.randomUUID() + "_" + fileName;

        MissionPresignedURLServiceDto missionPresignedURLServiceDto = r2StorageService.generatePresignedUploadUrl(objectKey);

        MissionPresignedURLCreateResponse missionPresignedURLCreateResponse = MissionPresignedURLCreateResponse.builder()
                .uploadUrl(missionPresignedURLServiceDto.getUploadUrl())
                .method(missionPresignedURLServiceDto.getMethod())
                .fileKey(objectKey)
                .build();

        return ResponseEntity.ok(ApiResult.success(missionPresignedURLCreateResponse));
    }
}
