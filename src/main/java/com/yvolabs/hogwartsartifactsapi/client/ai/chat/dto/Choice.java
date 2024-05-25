package com.yvolabs.hogwartsartifactsapi.client.ai.chat.dto;

import lombok.Builder;

/**
 * @author Yvonne N
 */
@Builder
public record Choice(int index, Message message) {
}
