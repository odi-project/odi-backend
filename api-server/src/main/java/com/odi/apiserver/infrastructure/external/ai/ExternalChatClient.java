package com.odi.apiserver.infrastructure.external.ai;

import com.odi.apiserver.infrastructure.external.ai.dto.ExternalChatRequest;
import com.odi.apiserver.infrastructure.external.ai.dto.ExternalChatResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.time.Duration;

@Component
@RequiredArgsConstructor
@Slf4j
public class ExternalChatClient {
    private final WebClient webClient;

    @Value("${external.chat.base-url:http://localhost:8000}")
    private String baseUrl;

    public Mono<ExternalChatResponse> sendMessage(ExternalChatRequest request) {
        log.info("Sending message to external chat server: {}", request.getMessage());

        String fullUrl = baseUrl + "/api/v1/chat/simple";

        log.info("🚀 Sending request to: {}", fullUrl);
        log.info("📤 Request body: {}", request);

        return webClient
                .post()
                .uri(baseUrl + "/api/v1/chat/simple")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .retrieve()
                // ✨ 핵심: createException()으로 WebClientResponseException 던지기
                .onStatus(HttpStatusCode::is4xxClientError, resp -> resp.createException())
                .onStatus(HttpStatusCode::is5xxServerError, resp -> resp.createException())
                .bodyToMono(ExternalChatResponse.class);
    }

    public Mono<Boolean> healthCheck() {
        return webClient
                .get()
                .uri(baseUrl + "/health")
                .retrieve()
                .toBodilessEntity()
                .map(response -> response.getStatusCode().is2xxSuccessful())
                .onErrorReturn(false)
                .timeout(Duration.ofSeconds(5));
    }
}
