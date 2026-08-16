package com.side.chubotamanju.global.exception;

import org.springframework.validation.BindingResult;

/**
 * 객체 검증 에러 발생 타입
 */
public class ValidationException extends RuntimeException {
    private final BindingResult bindingResult;

    public ValidationException(BindingResult bindingResult) {
        super("Validation failed");
        this.bindingResult = bindingResult;
    }

    public BindingResult getBindingResult() {
        return bindingResult;
    }
}
