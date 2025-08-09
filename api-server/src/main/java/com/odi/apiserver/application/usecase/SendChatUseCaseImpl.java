package com.odi.apiserver.application.usecase;

import com.odi.apiserver.application.port.in.SendChatCommand;
import com.odi.apiserver.application.port.in.SendChatUseCase;
import com.odi.apiserver.application.port.out.ExternalChatPort;
import com.odi.apiserver.domain.ai.ChatMessage;
import com.odi.apiserver.domain.ai.service.ChatService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
@Slf4j
public class SendChatUseCaseImpl implements SendChatUseCase {
    private final ChatService chatService;
    private final ExternalChatPort externalChatPort;

    @Override
    public Mono<ChatMessage> execute(SendChatCommand command) {
        log.info("Processing chat request message: {}", command.getMessage());

        return createChatRequest(command)
                .flatMap(this::sendToExternalServer);
    }

    private Mono<ChatMessage> createChatRequest(SendChatCommand command) {
        return Mono.fromSupplier(() -> chatService.createChatRequest(command));
    }

    private Mono<ChatMessage> sendToExternalServer(ChatMessage chatMessage) {
        return externalChatPort.sendMessage(chatMessage);
    }
}
