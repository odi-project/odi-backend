package com.odi.apiserver.application.port.in;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class SendChatCommand {
    private final String message;
    private final Integer maxTokens;
}
