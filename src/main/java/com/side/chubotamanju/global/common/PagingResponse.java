package com.side.chubotamanju.global.common;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Slice;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class PagingResponse<T, C> {

    // 1. 페이징
    private int totalPage;
    private int page;       // 클라이언트는 직관적으로 1, 2, 3... 보냄
    private int pageSize;      // 한 페이지당 가져올 데이터 개수

    // 2. 무한 스크롤(Cursor)
//    private Long lastId;        // 마지막으로 조회된 데이터의 ID
//    private String lastValue;   // 정렬 기준이 되는 마지막 데이터의 값 (예: 생성일, 조회수 등)
    private C cursor;

    // 3. 공통
    private boolean hasNext;

    private List<T> data;

    // 무한 스크롤
    public PagingResponse(List<T> data, boolean hasNext) {
//        this.cursor = cursor;
        this.hasNext = hasNext;
        this.data = data;
    }

    // 일반 페이징
    public PagingResponse(List<T> data, boolean hasNext, int page, int pageSize, int totalPage) {
        this.page = page;
        this.pageSize = pageSize;
        this.hasNext = hasNext;
        this.data = data;
        this.totalPage = totalPage;
    }
}
