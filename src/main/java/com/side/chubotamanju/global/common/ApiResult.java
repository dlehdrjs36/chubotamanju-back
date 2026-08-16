package com.side.chubotamanju.global.common;


import com.fasterxml.jackson.annotation.JsonFormat;

import com.side.chubotamanju.global.exception.ErrorCode;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

/**
 * Api 응답 구조 통일을 위한 커스텀 응답객체
 * @param <T>
 */
@Getter
@Schema(description = "API 성공 또는 실패 시 사용되는 표준 응답입니다.")
public class ApiResult<T> {
    private static final String SUCCESS_CODE = "SUCCESS";

    @Schema(description = "API 요청 성공여부. (true : 성공, false : 실패)", example = "true")
    private final boolean success;

    @Schema(description = "http 상태코드 이름", example = "OK")
    private final String status;

    @Schema(description = "내부 구분 코드", example = "SUCCESS 또는 X01, X02 ...")
    private final String code;

    @Schema(description = "성공, 오류 관련 메시지", example = "성공")
    private final String message;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
    @Schema(description = "응답 발생시간", example = "2025-09-04 11:04:26")
    private final LocalDateTime timestamp;

    @Schema(description = "응답 데이터")
    private final T data;

    @Schema(description = "세부 에러 목록")
    private final List<ApiSubError> subErrors; //상세 에러 목록(필드별)

    private ApiResult(
            boolean success
            , HttpStatus status
            , String code
            , String message
            , T data
            , List<ApiSubError> subErrors
    ) {
        this.success = success;
        this.status = status.name();
        this.code = code;
        this.message = message;
        this.timestamp = LocalDateTime.now();
        this.data = data;
        this.subErrors = (subErrors == null) ? Collections.emptyList() : subErrors;
    }

    public static <T> ApiResult<T> success(T data) {
        return success(data, "성공");
    }

    public static <T> ApiResult<T> success(T data, String message) {
        return new ApiResult<>(
                true
                , HttpStatus.OK
                , SUCCESS_CODE
                , message
                , data
                , null
        );
    }

    public static ApiResult<Void> failure(ErrorCode errorCode, String message) {
        return failure(errorCode, message, null, null);
    }

    public static <T> ApiResult<T> failure(ErrorCode errorCode, String message, List<ApiSubError> subErrors) {
        return failure(errorCode, message, null, subErrors);
    }

    private static <T> ApiResult<T> failure(ErrorCode errorCode, String message, T data, List<ApiSubError> subErrors) {
        return new ApiResult<>(false, errorCode.getHttpStatus(), errorCode.getCode(), message, data, subErrors);
    }

    @Schema(
            description = "공통 에러 서브 타입",
            oneOf = { ApiValidationError.class }
    )
    public static abstract class ApiSubError {}

    @Getter
    @Schema(description = "API 예외 발생 시 사용되는 응답입니다.")
    public static class ApiValidationError extends ApiSubError {
        @Schema(description = "객체명")
        private final String object;

        @Schema(description = "필드명")
        private final String field;

        @Schema(description = "거부된 값")
        private final Object rejectedValue;

        @Schema(description = "메시지")
        private final String message;

        public ApiValidationError(String object, String message) {
            this(object, null, null, message);
        }

        public ApiValidationError(String object, String field, Object rejectedValue, String message) {
            this.object = object;
            this.field = field;
            this.rejectedValue = rejectedValue;
            this.message = message;
        }
    }
}
