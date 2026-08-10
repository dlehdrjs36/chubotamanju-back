package com.side.daisso.global.exception.enums;

import com.side.daisso.global.exception.ErrorCode;
import org.springframework.http.HttpStatus;

/**
 * 공통 에러코드 관리
 * - HTTP 상태코드, 내부 에러코드, 스프링 메시지키를 갖는 enum 객체 생성
 * 도메인별로 발생하는 에러에 대해서는 별도의 에러코드를 정의하고 해당 도메인 패키지에서 별도로 생성해서 관리한다.
 * ex) code = C001
 * C: Common (공통)
 * U: User (사용자)
 * P: Product (상품)
 * A: Auth (인증/인가)
 * B: Business (비즈니스 로직)
 * E: External (외부 서비스)
 * D: Data (데이터 관련)
 * V: Validation (검증)
 * N: Notification (알림)
 * 등등
 */
public enum GlobalErrorCode implements ErrorCode {
    // 공통
    INVALID_REQUEST(HttpStatus.BAD_REQUEST, "C001", "C001.ERROR.COMMON.BAD_REQUEST"),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "C002", "C002.ERROR.COMMON.INTERNAL_SERVER_ERROR"),

    // 인증/인가
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "C003", "C003.ERROR.COMMON.UNAUTHORIZED"),
    FORBIDDEN(HttpStatus.FORBIDDEN, "C004", "C004.ERROR.COMMON.FORBIDDEN"),

    // API
    HTTP_MESSAGE_NOT_READABLE(HttpStatus.BAD_REQUEST, "C005", "C005.ERROR.COMMON.HTTP_MESSAGE_NOT_READABLE"),
    INVALID_FORMAT(HttpStatus.BAD_REQUEST, "C006", "C006.ERROR.COMMON.INVALID_FORMAT"),
    JSON_PARSE(HttpStatus.BAD_REQUEST, "C007", "C007.ERROR.COMMON.JSON_PARSE"),
    METHOD_ARGUMENT_NOT_VALID(HttpStatus.BAD_REQUEST, "C008", "C008.ERROR.COMMON.METHOD_ARGUMENT_NOT_VALID"),

    // 사용자
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "U001", "U001.ERROR.USER.NOT_FOUND"),
    USER_DUPLICATED(HttpStatus.CONFLICT, "U002", "U002.ERROR.USER.CONFLICT");


    private final HttpStatus httpStatus;
    private final String code;     // API 응답에서 클라이언트가 해석하는 식별자
    private final String messageKey;

    GlobalErrorCode(HttpStatus httpStatus, String code, String messageKey) {
        this.httpStatus = httpStatus;
        this.code = code;
        this.messageKey = messageKey;
    }

    @Override
    public HttpStatus getHttpStatus() {
        return httpStatus;
    }

    @Override
    public String getCode() {
        return code;
    }

    @Override
    public String getMessageKey() {
        return messageKey;
    }

}
