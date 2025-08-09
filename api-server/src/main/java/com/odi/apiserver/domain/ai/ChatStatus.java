package com.odi.apiserver.domain.ai;

public enum ChatStatus {
    PENDING("대기 중"),
    PROCESSING("처리 중"),
    COMPLETED("완료"),
    FAILED("실패");

    private final String description;

    ChatStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    /**
     * 다음 상태로 전이 가능한지 확인
     */
    public boolean canTransitionTo(ChatStatus nextStatus) {
        return switch (this) {
            case PENDING -> nextStatus == PROCESSING || nextStatus == FAILED;
            case PROCESSING -> nextStatus == COMPLETED || nextStatus == FAILED;
            case COMPLETED, FAILED -> false; // 최종 상태
        };
    }

    /**
     * 최종 상태인지 확인
     */
    public boolean isFinalStatus() {
        return this == COMPLETED || this == FAILED;
    }

    /**
     * 진행 중 상태인지 확인
     */
    public boolean isInProgress() {
        return this == PENDING || this == PROCESSING;
    }
}
