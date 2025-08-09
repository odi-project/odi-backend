package com.odi.apiserver.domain.ai.service;

import com.odi.apiserver.application.port.in.SendChatCommand;
import com.odi.apiserver.domain.ai.ChatMessage;
import org.springframework.stereotype.Service;

@Service
public class ChatService {
    public ChatMessage createChatRequest(SendChatCommand command) {
        // 비즈니스 규칙 검증
        validateMessage(command.getMessage());
        validateTokens(command.getMaxTokens());

        return ChatMessage.createRequest(
                command.getMessage(),
                command.getMaxTokens()
        );
    }

    private void validateMessage(String message) {
        if (message == null || message.trim().isEmpty()) {
            throw new IllegalArgumentException("메시지가 비어있습니다");
        }
        if (message.length() > 4000) {
            throw new IllegalArgumentException("메시지가 너무 깁니다");
        }
    }

    private void validateTokens(Integer maxTokens) {
        if (maxTokens == null || maxTokens <= 0) {
            throw new IllegalArgumentException("최대 토큰 수는 양수여야 합니다");
        }
        if (maxTokens > 4000) {
            throw new IllegalArgumentException("최대 토큰 수가 제한을 초과했습니다");
        }
    }
}
