package com.yvolabs.hogwartsartifactsapi.wizard;

import com.yvolabs.hogwartsartifactsapi.artifact.Artifact;
import com.yvolabs.hogwartsartifactsapi.artifact.ArtifactRepository;
import com.yvolabs.hogwartsartifactsapi.system.exception.ObjectNotFoundException;
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
public class WizardServiceImpl implements WizardService {
    private final WizardRepository wizardRepository;
    private final ArtifactRepository artifactRepository;

    @Override
    public List<Wizard> findAll() {
        return wizardRepository.findAll();
    }

    @Override
    public Wizard findById(Integer wizardId) {
        return wizardRepository.findById(wizardId)
                .orElseThrow(() -> new ObjectNotFoundException("wizard", wizardId));
    }

    @Override
    public Wizard save(Wizard wizard) {
        return wizardRepository.save(wizard);
    }

    // We are not updating a wizard's artifacts through this method, we only update their name.
    @Override
    public Wizard update(Integer wizardId, Wizard update) {
        Wizard foundWizard = wizardRepository.findById(wizardId)
                .orElseThrow(() -> new ObjectNotFoundException("wizard", wizardId));

        foundWizard.setName(update.getName());
        return wizardRepository.save(foundWizard);

    }


    @Override
    public void assignArtifact(Integer wizardId, String artifactId) {
        Artifact artifact = artifactRepository.findById(artifactId)
                .orElseThrow(() -> new ObjectNotFoundException("artifact", artifactId));

        Wizard wizard = wizardRepository.findById(wizardId)
                .orElseThrow(() -> new ObjectNotFoundException("wizard", wizardId));

        wizard.addArtifact(artifact);
    }

    @Override
    public void delete(Integer wizardId) {
        Wizard wizardTobeDeleted = wizardRepository.findById(wizardId)
                .orElseThrow(() -> new ObjectNotFoundException("wizard", wizardId));

        // Before deletion, we will unassign this wizard's owned artifacts.
        wizardTobeDeleted.removeAllArtifacts();
        wizardRepository.deleteById(wizardId);

    }
}
