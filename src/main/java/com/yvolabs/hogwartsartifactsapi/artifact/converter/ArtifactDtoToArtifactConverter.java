package com.yvolabs.hogwartsartifactsapi.artifact.converter;

import com.yvolabs.hogwartsartifactsapi.artifact.Artifact;
import com.yvolabs.hogwartsartifactsapi.artifact.dto.ArtifactDto;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

/**
 * @author Yvonne N
 */
@Component
public class ArtifactDtoToArtifactConverter implements Converter<ArtifactDto, Artifact> {
    @Override
    public Artifact convert(ArtifactDto source) {
        return Artifact.builder()
                .name(source.name())
                .description(source.description())
                .imageUrl(source.imageUrl())
                .build();
    }
}
