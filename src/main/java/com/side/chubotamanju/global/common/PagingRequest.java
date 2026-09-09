package com.side.chubotamanju.global.common;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class PagingRequest {

    // 1. 페이징
    private int page = 1;       // 클라이언트는 직관적으로 1, 2, 3... 보냄
    private int pageSize;      // 한 페이지당 가져올 데이터 개수

    // 2. 무한 스크롤(Cursor)
    // jackson에서 제네릭 타입 형변환 문제로 base64 기반으로 처리
    private String cursor;

    /**
     * 현재 요청이 일반 페이징인지 판별하는 메서드
     * 클라이언트가 커서(cursor)를 보내지 않았다면 일반 페이징(Offset)
     */
    public boolean isOffsetMode() {
        return this.cursor == null || this.cursor.isBlank();
    }
}
