package com.side.chubotamanju.global.common;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Base64;

/**
 * 무한 스크롤 커서 유틸
 * 문자열 형태의 커서를 실제 타입으로 매핑
 */
public class CursorUtil {
    private static final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Base64 문자열을 자바 커서 객체로 변환
     */
    public static <T> T getCursorAs(String cursor, Class<T> targetType) {
        if (cursor == null ||cursor.isBlank()) {
            return null;
        }
        try {
            // Base64 디코딩 후 JSON을 객체로 변환 (보안 및 URL 안정성)
            byte[] decodedBytes = Base64.getDecoder().decode(cursor);
            return objectMapper.readValue(decodedBytes, targetType);
        } catch (Exception e) {
            // 혹은 그냥 생 JSON 문자열 구조라면 아래 한 줄로 처리 가능
            // return objectMapper.readValue(this.cursor, targetType);
            throw new IllegalArgumentException("잘못된 커서 형식입니다.", e);
        }
    }

    /**
     * 자바 커서 객체를 클라이언트 전송용 Base64 문자열로 변환
     */
    public static String encodeCursor(Object cursorObject) {
        if (cursorObject == null) {
            return null;
        }
        try {
            // 1. 객체를 JSON 문자열로 변환 (예: {"lastBountyMissionId":10})
            String json = objectMapper.writeValueAsString(cursorObject);

            // 2. URL에 특수문자(+, /)가 깨지지 않도록 UrlSafe Base64로 인코딩
            return Base64.getUrlEncoder().encodeToString(json.getBytes());
        } catch (Exception e) {
            throw new IllegalStateException("커서 인코딩 중 오류가 발생했습니다.", e);
        }
    }

}
