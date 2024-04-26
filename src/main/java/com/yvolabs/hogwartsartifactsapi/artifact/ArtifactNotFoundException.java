package com.yvolabs.hogwartsartifactsapi.artifact;

/**
 * @author Yvonne N
 */
public class ArtifactNotFoundException extends RuntimeException {
    public ArtifactNotFoundException(String artifactId) {
        super("Could not find artifact with Id " + artifactId);
    }
}
