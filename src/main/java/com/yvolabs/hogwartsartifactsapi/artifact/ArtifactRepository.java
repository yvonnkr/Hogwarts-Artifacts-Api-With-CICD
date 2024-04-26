package com.yvolabs.hogwartsartifactsapi.artifact;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @author Yvonne N
 */
@Repository
public interface ArtifactRepository extends JpaRepository<Artifact, String> {
}
