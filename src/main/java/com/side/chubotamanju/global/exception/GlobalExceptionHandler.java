package com.side.chubotamanju.global.exception;


import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.side.chubotamanju.global.common.ApiResult;
import com.side.chubotamanju.global.exception.enums.GlobalErrorCode;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Rest Api 전역 예외처리
 * - 도메인 예외 처리기에서 처리하지 않는 공통의 예외들을 처리한다.
 */
@Slf4j
@RestControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    private final MessageSource messageSource;

    /**
     * BusinessException 예외 처리
     * @param ex
     * @param request
     * @return
     */
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiResult<Void>> handleBusinessException(
            BusinessException ex,
            HttpServletRequest request) {

        //실패 응답 생성
        ErrorCode errorCode = ex.getErrorCode();
        String errorMessage = getErrorMessage(errorCode.getMessageKey(), ex.getArgs());
        ApiResult<Void> response = ApiResult.failure(errorCode, errorMessage);

        return ResponseEntity
                .status(errorCode.getHttpStatus())
                .body(response);
    }

    /**
     * 나머지 예외 처리
     * @param ex
     * @param request
     * @return
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResult<Void>> handleGeneralException(Exception ex, HttpServletRequest request) {
        GlobalErrorCode internalServerError = GlobalErrorCode.INTERNAL_SERVER_ERROR;
        String errorMessage = getErrorMessage(internalServerError.getMessageKey());
        ApiResult<Void> response = ApiResult.failure(internalServerError, errorMessage);

        return ResponseEntity
                .status(internalServerError.getHttpStatus())
                .body(response);
    }

    /**
     * Validator 요청 객체 검증 실패
     * @param ex
     * @param request
     * @return
     */
    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ApiResult<Void>> handleValidation(ValidationException ex, HttpServletRequest request) {
        List<ApiResult.ApiSubError> apiSubErrorList = new ArrayList<>();
        ex.getBindingResult().getFieldErrors()
                .forEach(fieldError -> {
                    String errorMessage;
                    try {
                        errorMessage = getErrorMessage(fieldError.getCode(), fieldError.getArguments());
                    } catch (NoSuchMessageException e) {
                        errorMessage = Objects.toString(fieldError.getDefaultMessage(), "메시지 키를 찾지 못했습니다.");
                    }

                    ApiResult.ApiValidationError apiValidationError = new ApiResult.ApiValidationError(
                            fieldError.getObjectName(),
                            fieldError.getField(),
                            fieldError.getRejectedValue(),
                            errorMessage
                    );
                    apiSubErrorList.add(apiValidationError);
                });

        ex.getBindingResult().getGlobalErrors()
                .forEach(globalError -> {
                    String errorMessage;
                    try {
                        errorMessage = getErrorMessage(globalError.getCode(), globalError.getArguments());
                    } catch (NoSuchMessageException e) {
                        errorMessage = Objects.toString(globalError.getDefaultMessage(), "메시지 키를 찾지 못했습니다.");
                    }

                    ApiResult.ApiValidationError apiValidationError = new ApiResult.ApiValidationError(
                            globalError.getObjectName(),
                            errorMessage
                    );
                    apiSubErrorList.add(apiValidationError);
                });

        GlobalErrorCode methodArgumentNotValid = GlobalErrorCode.METHOD_ARGUMENT_NOT_VALID;
        String errorMessage = getErrorMessage(methodArgumentNotValid.getMessageKey());
        ApiResult<Void> response = ApiResult.failure(methodArgumentNotValid, errorMessage, apiSubErrorList);

        return ResponseEntity
                .status(methodArgumentNotValid.getHttpStatus())
                .body(response);
    }

    /**
     * 검증 실패(bean validation 필드)
     * @param ex
     * @param headers
     * @param status
     * @param request
     * @return
     */
    @Override
    public ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        List<ApiResult.ApiSubError> apiSubErrorList = ex.getBindingResult().getFieldErrors().stream()
                .map(fieldError -> new ApiResult.ApiValidationError(
                        fieldError.getObjectName(),
                        fieldError.getField(),
                        fieldError.getRejectedValue(),
                        fieldError.getDefaultMessage()
                ))
                .collect(Collectors.toList());

        GlobalErrorCode methodArgumentNotValid = GlobalErrorCode.METHOD_ARGUMENT_NOT_VALID;
        String errorMessage = getErrorMessage(methodArgumentNotValid.getMessageKey());
        ApiResult<Void> response = ApiResult.failure(methodArgumentNotValid, errorMessage, apiSubErrorList);

        return ResponseEntity
                .status(methodArgumentNotValid.getHttpStatus())
                .body(response);
    }

    @Override
    public ResponseEntity<Object> handleHttpMessageNotReadable(HttpMessageNotReadableException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
//        List<ApiResponse.ApiValidationError> collect = ex.getBindingResult().getFieldErrors().stream()
//                .map(fieldError -> new ApiResponse.ApiValidationError(
//                        fieldError.getObjectName(),
//                        fieldError.getField(),
//                        fieldError.getRejectedValue(),
//                        fieldError.getDefaultMessage()
//                ))
//                .collect(Collectors.toList());
        Throwable cause = ex.getCause();
        if (cause instanceof InvalidFormatException ife) {
            return invalidFormatHandle(ife);
        }else if (cause instanceof JsonParseException jpe) {
            return jsonParseHandle(jpe);
        }

        GlobalErrorCode httpMessageNotReadable = GlobalErrorCode.HTTP_MESSAGE_NOT_READABLE;
        String errorMessage = getErrorMessage(httpMessageNotReadable.getMessageKey());
        ApiResult<Void> response = ApiResult.failure(httpMessageNotReadable, errorMessage);

        return ResponseEntity
                .status(httpMessageNotReadable.getHttpStatus())
                .body(response);
    }

    /**
     * JSON 타입 미일치 오류 처리
     * @param ife
     * @return
     */
    private ResponseEntity<Object> invalidFormatHandle(InvalidFormatException ife) {
        List<ApiResult.ApiSubError> apiValidationErrors = ife.getPath().stream()
                .map(ref -> new ApiResult.ApiValidationError(
                        ref.getFrom().getClass().getSimpleName(),
                        ref.getFieldName(),
                        String.valueOf(ife.getValue()),
                        ife.getOriginalMessage()
                ))
                .collect(Collectors.toList());
        GlobalErrorCode httpMessageNotReadable = GlobalErrorCode.INVALID_FORMAT;
        String errorMessage = getErrorMessage(httpMessageNotReadable.getMessageKey());
        ApiResult<Void> response = ApiResult.failure(httpMessageNotReadable, errorMessage, apiValidationErrors);

        return ResponseEntity
                .status(httpMessageNotReadable.getHttpStatus())
                .body(response);
    }

    /**
     * JSON 타입 미일치 오류 처리
     * @param ex
     * @return
     */
    private ResponseEntity<Object> jsonParseHandle(JsonParseException ex) {
        ApiResult.ApiSubError apiValidationError = new ApiResult.ApiValidationError(
                "requestBody",
                null,
                null,
                ex.getOriginalMessage()
        );
        List<ApiResult.ApiSubError> apiSubErrorList = List.of(apiValidationError);
        GlobalErrorCode httpMessageNotReadable = GlobalErrorCode.JSON_PARSE;
        String errorMessage = getErrorMessage(httpMessageNotReadable.getMessageKey());
        ApiResult<Void> response = ApiResult.failure(httpMessageNotReadable, errorMessage, apiSubErrorList);

        return ResponseEntity
                .status(httpMessageNotReadable.getHttpStatus())
                .body(response);
    }

    /**
     * 예외에서 메시지키, 메시지 파라미터 추출하여 메시지 얻기
     * @param messageKey
     * @param args
     * @return
     */
    private String getErrorMessage(String messageKey, Object... args) {
        return messageSource.getMessage(
                messageKey,
                args,
                LocaleContextHolder.getLocale()
        );
    }
}
