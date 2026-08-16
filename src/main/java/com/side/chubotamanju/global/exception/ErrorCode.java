package com.side.chubotamanju.global.exception;

import org.springframework.http.HttpStatus;

/**
 * 에러 코드 동작 정의
 */
public interface ErrorCode {
    String getCode();
    HttpStatus getHttpStatus();
    String getMessageKey();
}
