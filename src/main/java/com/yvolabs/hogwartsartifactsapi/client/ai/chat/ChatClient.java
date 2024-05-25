package com.yvolabs.hogwartsartifactsapi.client.ai.chat;

import com.yvolabs.hogwartsartifactsapi.client.ai.chat.dto.ChatRequest;
import com.yvolabs.hogwartsartifactsapi.client.ai.chat.dto.ChatResponse;

/**
 * @author Yvonne N
 */
public interface ChatClient {

    ChatResponse generate(ChatRequest chatRequest);
}
