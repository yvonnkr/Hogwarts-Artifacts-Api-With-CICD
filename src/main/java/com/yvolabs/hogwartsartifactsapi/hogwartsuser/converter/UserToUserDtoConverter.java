package com.yvolabs.hogwartsartifactsapi.hogwartsuser.converter;

import com.yvolabs.hogwartsartifactsapi.hogwartsuser.HogwartsUser;
import com.yvolabs.hogwartsartifactsapi.hogwartsuser.dto.UserDto;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

/**
 * @author Yvonne N
 */
@Component
public class UserToUserDtoConverter implements Converter<HogwartsUser, UserDto> {
    @Override
    public UserDto convert(HogwartsUser source) {

        return UserDto.builder()
                .id(source.getId())
                .username(source.getUsername())
                .enabled(source.isEnabled())
                .roles(source.getRoles())
                .build();
    }
}
