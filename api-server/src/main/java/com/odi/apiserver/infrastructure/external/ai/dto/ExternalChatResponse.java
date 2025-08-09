package com.odi.apiserver.infrastructure.external.ai.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ExternalChatResponse {
    @JsonProperty("response")
    private String response;

    @JsonProperty("usage")
    private String usage;

    /**
     * 응답이 성공인지 확인
     */
    public boolean isSuccess() {
        return response != null && !response.trim().isEmpty();
    }

    /**
     * 에러 메시지 반환
     */
    public String getErrorMessage() {
        if (response == null || response.trim().isEmpty()) {
            return "외부 서버에서 빈 응답을 받았습니다";
        }
        return null;
    }
}
