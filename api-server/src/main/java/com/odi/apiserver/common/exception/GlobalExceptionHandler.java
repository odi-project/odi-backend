package com.odi.apiserver.common.exception;

import com.odi.apiserver.common.response.ApiResponse;
import com.odi.apiserver.common.response.ErrorInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.support.WebExchangeBindException;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.stream.Collectors;

@RestControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE) // ⭐ 최우선 순위로 처리
@Slf4j
public class GlobalExceptionHandler {

    /**
     * Validation 에러 처리
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Object>>  handleValidationException(MethodArgumentNotValidException ex) {
        log.warn("Validation error occurred", ex);

        List<ErrorInfo.FieldError> fieldErrors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> ErrorInfo.FieldError.builder()
                        .field(error.getField())
                        .rejectedValue(error.getRejectedValue())
                        .message(error.getDefaultMessage())
                        .build())
                .collect(Collectors.toList());

        ErrorInfo errorInfo = ErrorInfo.builder()
                .errorCode("VALIDATION_FAILED")
                .errorMessage("입력값 검증에 실패했습니다.")
                .fieldErrors(fieldErrors)
                .timestamp(java.time.LocalDateTime.now().toString())
                .build();

        ApiResponse<Object> response = ApiResponse.failure(400, "잘못된 요청입니다.", errorInfo);
//        return Mono.just(ResponseEntity.badRequest().body(response)); MVC인데 예외 핸들러 반환을 Mono<>로 작성
        return ResponseEntity.badRequest().body(response);
    }


    /**
     * 🎯 Reactive에서는 WebExchangeBindException을 처리해야 함!
     */
    @ExceptionHandler(WebExchangeBindException.class)
    public ResponseEntity<ApiResponse<Object>> handleWebExchangeBindException(WebExchangeBindException ex) {
        log.error("🚨 WebExchangeBindException occurred (Reactive Validation Error)", ex);

        List<ErrorInfo.FieldError> fieldErrors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> {
                    log.error("❌ Field error: field='{}', rejectedValue='{}', message='{}'",
                            error.getField(), error.getRejectedValue(), error.getDefaultMessage());

                    return ErrorInfo.FieldError.builder()
                            .field(error.getField())
                            .rejectedValue(error.getRejectedValue())
                            .message(error.getDefaultMessage())
                            .build();
                })
                .collect(Collectors.toList());

        ErrorInfo errorInfo = ErrorInfo.builder()
                .errorCode("VALIDATION_FAILED")
                .errorMessage("입력값 검증에 실패했습니다.")
                .fieldErrors(fieldErrors)
                .timestamp(java.time.LocalDateTime.now().toString())
                .build();

        ApiResponse<Object> response = ApiResponse.failure(400, "잘못된 요청입니다.", errorInfo);

        log.info("📤 Returning validation error response: {}", response);

        return ResponseEntity.badRequest().body(response);
    }

    /**
     * 비즈니스 예외 처리
     */
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiResponse<Object>> handleBusinessException(BusinessException ex) {
        log.warn("Business error occurred: {}", ex.getMessage());

        ErrorInfo errorInfo = ErrorInfo.builder()
                .errorCode(ex.getErrorCode().name())
                .errorMessage(ex.getMessage())
                .timestamp(java.time.LocalDateTime.now().toString())
                .build();

        ApiResponse<Object> response = ApiResponse.failure(
                ex.getErrorCode().getHttpStatus(),
                ex.getMessage(),
                errorInfo
        );

        return ResponseEntity.status(ex.getErrorCode().getHttpStatus()).body(response);
    }

    /**
     * 외부 API 에러 처리
     */
    @ExceptionHandler(WebClientResponseException.class)
    public ResponseEntity<ApiResponse<Object>> handleWebClientException(WebClientResponseException ex) {
        log.error("External API error occurred", ex);

        ErrorInfo errorInfo = ErrorInfo.builder()
                .errorCode("EXTERNAL_API_ERROR")
                .errorMessage("외부 서비스 연동 중 오류가 발생했습니다.")
                .timestamp(java.time.LocalDateTime.now().toString())
                .build();

        ApiResponse<Object> response = ApiResponse.failure(502, "외부 서비스 오류", errorInfo);
        return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body(response);
    }

    /**
     * 일반 예외 처리
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Object>> handleGeneralException(Exception ex) {
        log.error("Unexpected error occurred", ex);

        ErrorInfo errorInfo = ErrorInfo.builder()
                .errorCode("INTERNAL_SERVER_ERROR")
                .errorMessage("서버 내부 오류가 발생했습니다.")
                .timestamp(java.time.LocalDateTime.now().toString())
                .build();

        ApiResponse<Object> response = ApiResponse.failure(500, "서버 오류", errorInfo);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
}
