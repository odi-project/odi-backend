package com.odi.apiserver.api.v1.controller;


import com.odi.apiserver.api.v1.dto.request.ChatRequest;
import com.odi.apiserver.api.v1.dto.response.ChatResponse;
import com.odi.apiserver.application.port.in.SendChatCommand;
import com.odi.apiserver.application.port.in.SendChatUseCase;
import com.odi.apiserver.common.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/v1/ai")
@RequiredArgsConstructor
@Slf4j
public class AiController {
    private final SendChatUseCase sendChatUseCase;

    @PostMapping("/greeting")
    public Mono<ResponseEntity<ApiResponse<ChatResponse>>> sendMessage(@Valid @RequestBody ChatRequest request) {
        log.info("Chat request received: message={}, maxTokens={}", request.getMessage(), request.getMaxTokens());

        SendChatCommand command = SendChatCommand.builder()
                .message(request.getMessage())
                .maxTokens(request.getMaxTokens())
                .build();

        return sendChatUseCase.execute(command)
                .map(chatMessage -> {
                    ChatResponse response = ChatResponse.from(chatMessage);
                    ApiResponse<ChatResponse> apiResponse = ApiResponse.success(response, "채팅 메시지가 성공적으로 처리되었습니다.");
                    return ResponseEntity.ok(apiResponse);
                })
                .doOnSuccess(response -> log.info("Chat response sent successfully"))
                .doOnError(error -> log.error("Chat request failed", error));
    }
}
