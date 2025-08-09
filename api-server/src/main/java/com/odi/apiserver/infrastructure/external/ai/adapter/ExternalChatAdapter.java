package com.odi.apiserver.infrastructure.external.ai.adapter;

import com.odi.apiserver.application.port.out.ExternalChatPort;
import com.odi.apiserver.domain.ai.ChatMessage;
import com.odi.apiserver.infrastructure.external.ai.ExternalChatClient;
import com.odi.apiserver.infrastructure.external.ai.dto.ExternalChatRequest;
import com.odi.apiserver.infrastructure.external.mapper.ChatMessageMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
@Slf4j
public class ExternalChatAdapter implements ExternalChatPort {
    private final ExternalChatClient externalChatClient;
    private final ChatMessageMapper chatMessageMapper;

    @Override
    public Mono<ChatMessage> sendMessage(ChatMessage chatMessage) {
        ExternalChatRequest request = chatMessageMapper.toExternalRequest(chatMessage);

        return externalChatClient.sendMessage(request)
                .map(response -> chatMessageMapper.toDomainWithResponse(chatMessage, response))
                .doOnNext(result -> log.info("External chat response mapped to domain: requestId={}",
                        result.getRequestId()));
    }

    @Override
    public Mono<Boolean> healthCheck() {
        return externalChatClient.healthCheck();
    }
}
