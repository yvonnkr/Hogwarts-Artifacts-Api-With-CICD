package com.yvolabs.hogwartsartifactsapi.artifact;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yvolabs.hogwartsartifactsapi.artifact.dto.ArtifactDto;
import com.yvolabs.hogwartsartifactsapi.client.ai.chat.ChatClient;
import com.yvolabs.hogwartsartifactsapi.client.ai.chat.dto.ChatRequest;
import com.yvolabs.hogwartsartifactsapi.client.ai.chat.dto.ChatResponse;
import com.yvolabs.hogwartsartifactsapi.client.ai.chat.dto.Message;
import com.yvolabs.hogwartsartifactsapi.system.exception.ObjectNotFoundException;
import com.yvolabs.hogwartsartifactsapi.utils.IdWorker;
import io.micrometer.core.annotation.Timed;
import io.micrometer.observation.annotation.Observed;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author Yvonne N
 */
@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class ArtifactServiceImpl implements ArtifactService {
    private final ArtifactRepository artifactRepository;
    private final IdWorker idWorker;
    private final ChatClient chatClient;

    @Override
    @Observed(name = "artifact", contextualName = "findByIdService")
    public Artifact findById(String artifactId) {

        return artifactRepository.findById(artifactId)
                .orElseThrow(() -> new ObjectNotFoundException("artifact", artifactId));
    }


    // with pagination
    @Override
    public Page<Artifact> findAll(Pageable pageable) {

        return artifactRepository.findAll(pageable);
    }

    // without pagination
    @Override
    @Timed("findAllArtifactsService.time") // actuator metrics - measure the latency of this method
    public List<Artifact> findAll() {

        // set logger test
        // POST actuator/loggers/com.yvolabs.hogwartsartifactsapi.artifact.ArtifactServiceImpl
        // {"configuredLevel": "DEBUG" or null}
        log.info("logging-info");
        log.debug("logging-debug");

        return artifactRepository.findAll();
    }

    @Override
    public Artifact save(Artifact newArtifact) {
        long generatedId = idWorker.nextId();
        newArtifact.setId(String.valueOf(generatedId));
        return artifactRepository.save(newArtifact);
//        return null;
    }

    @Override
    public Artifact update(String artifactId, Artifact update) {
        return artifactRepository.findById(artifactId)
                .map(oldArtifact -> {
                    oldArtifact.setName(update.getName());
                    oldArtifact.setDescription(update.getDescription());
                    oldArtifact.setImageUrl(update.getImageUrl());
                    return artifactRepository.save(oldArtifact);

                })
                .orElseThrow(() -> new ObjectNotFoundException("artifact", artifactId));
    }

    @Override
    public void delete(String artifactId) {
        artifactRepository.findById(artifactId)
                .orElseThrow(() -> new ObjectNotFoundException("artifact", artifactId));
        artifactRepository.deleteById(artifactId);

    }

    /**
     * Returns a summary of the existing artifacts. This method is responsible for preparing the AiChatRequest and parsing the AiChatResponse.
     *
     * @param artifactDtos a list of artifact dtos to be summarized
     * @return a summary of the existing artifacts
     * @throws JsonProcessingException thrown by ObjectMapper
     */
    @Override
    public String summarize(List<ArtifactDto> artifactDtos) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonArray = objectMapper.writeValueAsString(artifactDtos);

        // Prepare the messages for summarizing.
        List<Message> messages = List.of(
                Message.builder()
                        .role("system")
                        .content("Your task is to generate a short summary of a given JSON array in at most 100 words. The summary must include the number of artifacts, each artifact's description, and the ownership information. Don't mention that the summary is from a given JSON array.") //todo change to real content
                        .build(),
                Message.builder()
                        .role("user")
                        .content(jsonArray)
                        .build()
        );

        ChatRequest chatRequest = ChatRequest.builder()
                .model("gpt-4")
                .messages(messages)
                .build();

        ChatResponse chatResponse = chatClient.generate(chatRequest); // Tell chatClient to generate a text summary based on the given chatRequest.

        // Retrieve the AI-generated text and return to the controller.
        return chatResponse.choices().get(0).message().content();


    }


}
