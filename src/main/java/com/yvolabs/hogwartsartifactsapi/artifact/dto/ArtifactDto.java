package com.yvolabs.hogwartsartifactsapi.artifact.dto;

import com.yvolabs.hogwartsartifactsapi.wizard.dto.WizardDto;
import jakarta.validation.constraints.NotEmpty;
import lombok.Builder;

/**
 * @author Yvonne N
 */
@Builder
public record ArtifactDto(String id,
                          @NotEmpty(message = "name is required.")
                          String name,
                          @NotEmpty(message = "description is required.")
                          String description,
                          @NotEmpty(message = "imageUrl is required.")
                          String imageUrl,
                          WizardDto owner) {
}
