package com.odi.apiserver.common.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;

/**
 * 통일된 API 응답 포맷
 * - 모든 API는 이 포맷을 사용
 * - 성공/실패 상관없이 일관된 구조
 */
@Getter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {
    private final boolean success;
    private final int code;
    private final String message;
    private final T data;
    private final ErrorInfo error;
    private final String timestamp;
    // 요청 추적 ID
    private final String traceId;

    /**
     * 성공 응답 생성
     */
    public static <T> ApiResponse<T> success(T data) {
        return ApiResponse.<T>builder()
                .success(true)
                .code(200)
                .message("요청이 성공적으로 처리되었습니다.")
                .data(data)
                .timestamp(java.time.LocalDateTime.now().toString())
                .traceId(generateTraceId())
                .build();
    }

    public static <T> ApiResponse<T> success(T data, String message) {
        return ApiResponse.<T>builder()
                .success(true)
                .code(200)
                .message(message)
                .data(data)
                .timestamp(java.time.LocalDateTime.now().toString())
                .traceId(generateTraceId())
                .build();
    }

    /**
     * 실패 응답 생성
     */
    public static <T> ApiResponse<T> failure(int code, String message) {
        return ApiResponse.<T>builder()
                .success(false)
                .code(code)
                .message(message)
                .data(null)
                .timestamp(java.time.LocalDateTime.now().toString())
                .traceId(generateTraceId())
                .build();
    }

    public static <T> ApiResponse<T> failure(int code, String message, ErrorInfo error) {
        return ApiResponse.<T>builder()
                .success(false)
                .code(code)
                .message(message)
                .error(error)
                .timestamp(java.time.LocalDateTime.now().toString())
                .traceId(generateTraceId())
                .build();
    }

    private static String generateTraceId() {
        return "req_" + System.currentTimeMillis();
    }
}
