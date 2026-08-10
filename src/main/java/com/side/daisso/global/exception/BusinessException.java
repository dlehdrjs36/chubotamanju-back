package com.side.daisso.global.exception;

/**
 * 비즈니스 에러 정의
 */
public class BusinessException extends RuntimeException {
    private final ErrorCode errorCode;
    private final Object[] args; // 메시지 파라미터

    public BusinessException(ErrorCode errorCode) {
        this(errorCode, null);
    }

    public BusinessException(ErrorCode errorCode, Object[] args) {
        super(errorCode.getMessageKey());
        this.errorCode = errorCode;
        this.args = args;
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }

    public Object[] getArgs() {
        return args;
    }
}
