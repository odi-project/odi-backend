package com.odi.apiserver.infrastructure.external.mapper;

import com.odi.apiserver.domain.ai.ChatMessage;
import com.odi.apiserver.infrastructure.external.ai.dto.ExternalChatRequest;
import com.odi.apiserver.infrastructure.external.ai.dto.ExternalChatResponse;
import org.springframework.stereotype.Component;

@Component
public class ChatMessageMapper {
    public ExternalChatRequest toExternalRequest(ChatMessage chatMessage) {
        return ExternalChatRequest.builder()
                .message(chatMessage.getMessage())
                .maxTokens(chatMessage.getMaxTokens())
                .requestId(chatMessage.getRequestId())
                .build();
    }

    public ChatMessage toDomainWithResponse(ChatMessage originalMessage, ExternalChatResponse response) {
        return originalMessage.withResponse(
                response.getResponse()
        );
    }
}
