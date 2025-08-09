package com.odi.apiserver.common.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {
    // 공통 에러
    INVALID_REQUEST(400, "잘못된 요청입니다."),
    UNAUTHORIZED(401, "인증이 필요합니다."),
    FORBIDDEN(403, "권한이 없습니다."),
    NOT_FOUND(404, "요청한 리소스를 찾을 수 없습니다."),
    INTERNAL_SERVER_ERROR(500, "서버 내부 오류가 발생했습니다."),

    // 사용자 관련 에러
    USER_NOT_FOUND(404, "사용자를 찾을 수 없습니다."),
    DUPLICATE_EMAIL(409, "이미 존재하는 이메일입니다."),
    INVALID_PASSWORD(400, "비밀번호가 올바르지 않습니다."),

    // 채팅 관련 에러
    MESSAGE_TOO_LONG(400, "메시지가 너무 깁니다."),
    INVALID_TOKEN_COUNT(400, "토큰 수가 올바르지 않습니다."),
    EXTERNAL_API_TIMEOUT(504, "외부 API 응답 시간이 초과되었습니다."),
    EXTERNAL_API_ERROR(502, "외부 API 오류가 발생했습니다.");

    private final int httpStatus;
    private final String message;
}
