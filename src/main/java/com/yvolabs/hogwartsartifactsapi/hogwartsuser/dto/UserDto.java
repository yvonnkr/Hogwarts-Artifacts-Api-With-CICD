package com.yvolabs.hogwartsartifactsapi.hogwartsuser.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.Builder;

/**
 * @author Yvonne N
 */
@Builder
public record UserDto(
        Integer id,

        @NotEmpty(message = "username is required.")
        String username,

        boolean enabled,

        @NotEmpty(message = "roles are required.")
        String roles
) {

}
