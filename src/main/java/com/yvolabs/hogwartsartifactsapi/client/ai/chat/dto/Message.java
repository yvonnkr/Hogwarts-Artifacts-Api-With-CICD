package com.yvolabs.hogwartsartifactsapi.client.ai.chat.dto;

import lombok.Builder;

/**
 * @author Yvonne N
 */
@Builder
public record Message(String role, String content) {
}
