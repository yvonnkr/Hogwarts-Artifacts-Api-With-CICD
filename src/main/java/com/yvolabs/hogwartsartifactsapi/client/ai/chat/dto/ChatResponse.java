package com.yvolabs.hogwartsartifactsapi.client.ai.chat.dto;

import lombok.Builder;

import java.util.List;

/**
 * @author Yvonne N
 */
@Builder
public record ChatResponse(List<Choice> choices) {
}
