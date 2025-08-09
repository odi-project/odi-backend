package com.odi.apiserver.application.port.in;

import com.odi.apiserver.domain.ai.ChatMessage;
import reactor.core.publisher.Mono;

public interface SendChatUseCase {
    /**
     * 채팅 메시지를 외부 서버로 전송
     * @param command 전송할 메시지 정보
     * @return 처리된 채팅 메시지
     */
    Mono<ChatMessage> execute(SendChatCommand command);
}
