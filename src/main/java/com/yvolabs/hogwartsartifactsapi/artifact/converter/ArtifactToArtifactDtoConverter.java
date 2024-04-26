package com.yvolabs.hogwartsartifactsapi.artifact.converter;

import com.yvolabs.hogwartsartifactsapi.artifact.Artifact;
import com.yvolabs.hogwartsartifactsapi.artifact.dto.ArtifactDto;
import com.yvolabs.hogwartsartifactsapi.wizard.converter.WizardToWizardDtoConverter;
import lombok.RequiredArgsConstructor;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

/**
 * @author Yvonne N
 */
@Component
@RequiredArgsConstructor
public class ArtifactToArtifactDtoConverter implements Converter<Artifact, ArtifactDto> {
    private final WizardToWizardDtoConverter wizardToWizardDtoConverter;

    @Override
    public ArtifactDto convert(Artifact source) {
        return new ArtifactDto(
                source.getId(),
                source.getName(),
                source.getDescription(),
                source.getImageUrl(),
                source.getOwner() != null ?
                        wizardToWizardDtoConverter.convert(source.getOwner()) :
                        null
        );
    }
}
