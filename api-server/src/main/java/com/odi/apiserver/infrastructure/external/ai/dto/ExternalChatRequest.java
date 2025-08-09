package com.odi.apiserver.infrastructure.external.ai.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ExternalChatRequest {
    @JsonProperty("message")
    private final String message;

    @JsonProperty("max_tokens")
    private final Integer maxTokens;

    // 내부 식별용 (외부 API에 전송하지 않음)
    private final String requestId;
}
