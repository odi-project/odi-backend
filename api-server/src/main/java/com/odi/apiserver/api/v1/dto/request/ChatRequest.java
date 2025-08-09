package com.odi.apiserver.api.v1.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ChatRequest {
    @NotBlank(message = "메시지는 필수입니다")
    private final String message;

    @Positive(message = "최대 토큰 수는 양수여야 합니다")
    private final Integer maxTokens;
}
