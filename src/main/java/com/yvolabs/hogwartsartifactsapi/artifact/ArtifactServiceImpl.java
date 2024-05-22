package com.yvolabs.hogwartsartifactsapi.artifact;

import com.yvolabs.hogwartsartifactsapi.system.exception.ObjectNotFoundException;
import com.yvolabs.hogwartsartifactsapi.utils.IdWorker;
import io.micrometer.core.annotation.Timed;
import io.micrometer.observation.annotation.Observed;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

    @Override
    @Observed(name = "artifact", contextualName = "findByIdService")
    public Artifact findById(String artifactId) {

        return artifactRepository.findById(artifactId)
                .orElseThrow(() -> new ObjectNotFoundException("artifact", artifactId));
    }

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
}
