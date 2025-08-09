package com.odi.apiserver.application.port.out;

import com.odi.apiserver.domain.ai.ChatMessage;
import reactor.core.publisher.Mono;

public interface ExternalChatPort {
    Mono<ChatMessage> sendMessage(ChatMessage chatMessage);
    Mono<Boolean> healthCheck();
}
