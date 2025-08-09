package com.odi.apiserver.domain.ai;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Builder
public class ChatMessage {
    // 식별자
    private final String requestId;
    private final String userId;

    // 메시지 내용
    private final String message;
    private final String response;

    // 토큰 정보
    private final Integer maxTokens;
    private final Integer tokensUsed;

    // 시간 정보
    private final LocalDateTime createdAt;
    private final LocalDateTime processedAt;

    // 상태
    private final ChatStatus status;

    public static ChatMessage createRequest(String message, Integer maxTokens) {
        // 도메인 규칙 검증
        validateMessage(message);
        validateMaxTokens(maxTokens);

        return ChatMessage.builder()
                .requestId(generateRequestId())
                .message(message)
                .maxTokens(maxTokens)
                .createdAt(LocalDateTime.now())
                .status(ChatStatus.PENDING)
                .build();
    }

    public ChatMessage withResponse(String response) {
//        validateResponse(response);
//        validateTokensUsed(tokensUsed);

        return ChatMessage.builder()
                .response(response)
                .build();
    }

    public ChatMessage withFailure(String errorMessage) {
        return ChatMessage.builder()
                .requestId(this.requestId)
                .userId(this.userId)
                .message(this.message)
                .response(errorMessage)
                .maxTokens(this.maxTokens)
                .createdAt(this.createdAt)
                .processedAt(LocalDateTime.now())
                .status(ChatStatus.FAILED)
                .build();
    }


    public ChatMessage markAsProcessing() {
        return ChatMessage.builder()
                .requestId(this.requestId)
                .userId(this.userId)
                .message(this.message)
                .response(this.response)
                .maxTokens(this.maxTokens)
                .tokensUsed(this.tokensUsed)
                .createdAt(this.createdAt)
                .processedAt(this.processedAt)
                .status(ChatStatus.PROCESSING)
                .build();
    }

    private static void validateUserId(String userId) {
        if (userId == null || userId.trim().isEmpty()) {
            throw new IllegalArgumentException("사용자 ID는 필수입니다");
        }
        if (userId.length() > 50) {
            throw new IllegalArgumentException("사용자 ID가 너무 깁니다");
        }
    }

    private static void validateMessage(String message) {
        if (message == null || message.trim().isEmpty()) {
            throw new IllegalArgumentException("메시지는 필수입니다");
        }
        if (message.length() > 4000) {
            throw new IllegalArgumentException("메시지가 너무 깁니다 (최대 4000자)");
        }
    }

    private static void validateMaxTokens(Integer maxTokens) {
        if (maxTokens == null || maxTokens <= 0) {
            throw new IllegalArgumentException("최대 토큰 수는 양수여야 합니다");
        }
        if (maxTokens > 4000) {
            throw new IllegalArgumentException("최대 토큰 수가 제한을 초과했습니다 (최대 4000)");
        }
    }

    private static void validateResponse(String response) {
        if (response == null || response.trim().isEmpty()) {
            throw new IllegalArgumentException("응답 메시지는 필수입니다");
        }
    }

    private static void validateTokensUsed(Integer tokensUsed) {
        if (tokensUsed == null || tokensUsed < 0) {
            throw new IllegalArgumentException("사용된 토큰 수는 0 이상이어야 합니다");
        }
    }

    private static String generateRequestId() {
        return "chat_" + UUID.randomUUID().toString().replace("-", "").substring(0, 8);
    }

    /**
     * 메시지가 완료되었는지 확인
     */
    public boolean isCompleted() {
        return status == ChatStatus.COMPLETED;
    }

    /**
     * 메시지가 실패했는지 확인
     */
    public boolean isFailed() {
        return status == ChatStatus.FAILED;
    }

    /**
     * 메시지가 처리 중인지 확인
     */
    public boolean isProcessing() {
        return status == ChatStatus.PROCESSING;
    }

    /**
     * 메시지가 대기 중인지 확인
     */
    public boolean isPending() {
        return status == ChatStatus.PENDING;
    }

    /**
     * 토큰 효율성 계산 (사용률)
     */
    public double getTokenEfficiency() {
        if (tokensUsed == null || maxTokens == null || maxTokens == 0) {
            return 0.0;
        }
        return (double) tokensUsed / maxTokens * 100.0;
    }

    /**
     * 처리 시간 계산 (밀리초)
     */
    public long getProcessingTimeMillis() {
        if (createdAt == null || processedAt == null) {
            return 0L;
        }
        return java.time.Duration.between(createdAt, processedAt).toMillis();
    }

    /**
     * 메시지가 특정 사용자의 것인지 확인
     */
    public boolean belongsToUser(String userId) {
        return this.userId != null && this.userId.equals(userId);
    }

    /**
     * 응답이 있는지 확인
     */
    public boolean hasResponse() {
        return response != null && !response.trim().isEmpty();
    }

    /**
     * 메시지 요약 (로깅용)
     */
    public String getSummary() {
        return String.format("ChatMessage[id=%s, user=%s, status=%s, tokens=%d/%d]",
                requestId, userId, status, tokensUsed, maxTokens);
    }

    /**
     * 디버깅용 문자열 표현
     */
    @Override
    public String toString() {
        return String.format("ChatMessage{requestId='%s', userId='%s', status=%s, " +
                        "messageLength=%d, responseLength=%d, tokensUsed=%d, maxTokens=%d}",
                requestId, userId, status,
                message != null ? message.length() : 0,
                response != null ? response.length() : 0,
                tokensUsed != null ? tokensUsed : 0,
                maxTokens != null ? maxTokens : 0);
    }

    // ============ 동등성 비교 (requestId 기준) ============

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        ChatMessage that = (ChatMessage) obj;
        return requestId != null && requestId.equals(that.requestId);
    }

    @Override
    public int hashCode() {
        return requestId != null ? requestId.hashCode() : 0;
    }
}
