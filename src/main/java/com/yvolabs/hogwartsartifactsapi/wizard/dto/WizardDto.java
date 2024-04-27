package com.yvolabs.hogwartsartifactsapi.wizard.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.Builder;

/**
 * @author Yvonne N
 */
@Builder
public record WizardDto(
        Integer id,

        @NotEmpty(message = "name is required")
        String name,

        Integer numberOfArtifacts) {
}
