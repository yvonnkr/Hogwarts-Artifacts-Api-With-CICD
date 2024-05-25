package com.yvolabs.hogwartsartifactsapi.artifact;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.yvolabs.hogwartsartifactsapi.artifact.dto.ArtifactDto;

import java.util.List;

/**
 * @author Yvonne N
 */
public interface ArtifactService {
    Artifact findById(String artifactId);

    List<Artifact> findAll();

    Artifact save(Artifact newArtifact);

    Artifact update(String artifactId, Artifact update);

    void delete(String artifactId);

    String summarize(List<ArtifactDto> artifactDtos) throws JsonProcessingException;
}
