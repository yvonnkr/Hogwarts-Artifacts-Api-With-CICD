package com.yvolabs.hogwartsartifactsapi.artifact;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.yvolabs.hogwartsartifactsapi.artifact.dto.ArtifactDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;

/**
 * @author Yvonne N
 */
public interface ArtifactService {
    Artifact findById(String artifactId);

    List<Artifact> findAll();

    Page<Artifact> findAll(Pageable pageable);

    Artifact save(Artifact newArtifact);

    Artifact update(String artifactId, Artifact update);

    void delete(String artifactId);

    String summarize(List<ArtifactDto> artifactDtos) throws JsonProcessingException;

    Page<Artifact> findByCriteria(Map<String, String> searchCriteria, Pageable pageable);

}
