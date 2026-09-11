package com.side.chubotamanju.global.common;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;

import java.util.Collections;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class PagingResponse<T> {

    // 1. 페이징
    private int totalPage;
    private int page;       // 클라이언트는 직관적으로 1, 2, 3... 보냄
    private int pageSize;      // 한 페이지당 가져올 데이터 개수

    // 2. 무한 스크롤(Cursor)
    // jackson에서 제네릭 타입 형변환 문제로 base64 기반으로 처리
    private String cursor;

    // 3. 공통
    private boolean hasNext;

    private List<T> data;

    // 무한 스크롤
    public PagingResponse(List<T> data, boolean hasNext) {
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

    //내부 변환 생성자
    private PagingResponse(String cursor, List<T> data, boolean hasNext, int page, int pageSize, int totalPage) {
        this.cursor = cursor;
        this.page = page;
        this.pageSize = pageSize;
        this.hasNext = hasNext;
        this.data = data;
        this.totalPage = totalPage;
    }
    /**
     * 전달받은 리스트의 타입으로 반환
     * @param data
     * @return
     * @param <C>
     */
    public <C> PagingResponse<C> changeData(List<C> data) {
        return new PagingResponse<>(this.cursor, data, this.hasNext, this.page, this.pageSize, this.totalPage);
    }

    public Page<T> toBotPage() {
        int page = Math.max(0, this.page - 1); //서버의 페이지 1이 첫페이지로 동작하지만 봇은 0이 첫페이지로 동작한다. (봇의 페이지 처리 체계로 맞춰준다.)
        PageRequest pageRequest = PageRequest.of(page, this.pageSize);

        // 데이터가 없는 경우, 익명 클래스 오버라이딩을 통하여 내부 계산 로직 변경
        if (this.data == null || this.data.isEmpty()) {
            return new PageImpl<>(Collections.emptyList(), pageRequest, (long) this.totalPage * this.pageSize) {
                @Override
                public int getTotalPages() {
                    return totalPage; // 총 페이지 수를 백엔드 계산 그대로 리턴
                }
                @Override
                public long getTotalElements() {
                    return (long) totalPage * pageSize; // 전체 데이터 백엔드 데이터를 이용하여 계산
                }
            };
        }

        return new PageImpl<>(this.data, pageRequest, (long) this.totalPage * this.pageSize);
    }
}
