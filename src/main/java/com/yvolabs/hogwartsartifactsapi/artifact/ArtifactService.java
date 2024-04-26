package com.yvolabs.hogwartsartifactsapi.artifact;

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
}
