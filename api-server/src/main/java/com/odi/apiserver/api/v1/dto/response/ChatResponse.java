package com.odi.apiserver.api.v1.dto.response;

import com.odi.apiserver.domain.ai.ChatMessage;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class ChatResponse {
    private final String response;
    private final LocalDateTime processedAt;

    public static ChatResponse from(ChatMessage chatMessage) {
        return ChatResponse.builder()
                .response(chatMessage.getResponse())
                .processedAt(chatMessage.getProcessedAt())
                .build();
    }
}
