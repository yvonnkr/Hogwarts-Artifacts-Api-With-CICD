package com.yvolabs.hogwartsartifactsapi.wizard;

import java.util.List;

/**
 * @author Yvonne N
 */
public interface WizardService {
    List<Wizard> findAll();

    Wizard findById(Integer wizardId);

    void delete(Integer wizardId);

    Wizard save(Wizard wizard);

    Wizard update(Integer wizardId, Wizard update);

    void assignArtifact(Integer wizardId, String artifactId);
}
