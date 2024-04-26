package com.yvolabs.hogwartsartifactsapi.artifact;

import com.yvolabs.hogwartsartifactsapi.utils.IdWorker;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author Yvonne N
 */
@Service
@Transactional
@RequiredArgsConstructor
public class ArtifactServiceImpl implements ArtifactService {
    private final ArtifactRepository artifactRepository;
    private final IdWorker idWorker;

    @Override
    public Artifact findById(String artifactId) {

        return artifactRepository.findById(artifactId)
                .orElseThrow(() -> new ArtifactNotFoundException(artifactId));
    }

    @Override
    public List<Artifact> findAll() {
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
                .orElseThrow(() -> new ArtifactNotFoundException(artifactId));
    }

    @Override
    public void delete(String artifactId) {
        artifactRepository.findById(artifactId)
                .orElseThrow(() -> new ArtifactNotFoundException(artifactId));
        artifactRepository.deleteById(artifactId);

    }
}
