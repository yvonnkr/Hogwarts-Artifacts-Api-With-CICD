package com.yvolabs.hogwartsartifactsapi.artifact;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

/**
 * @author Yvonne N
 * @apiNote To support Specification, We need to extend repo with JpaSpecificationExecutor Class
 *          We define some specification then we can use them to create a query and can combine the specs using logical operators(and,or,not etc.)
 *          The findAll(Specification<T> spec) will be called
 * @see     ArtifactController ::summarizeArtifacts
 * @see     ArtifactSpecs

 */
@Repository
public interface ArtifactRepository extends JpaRepository<Artifact, String>, JpaSpecificationExecutor<Artifact> {
}
