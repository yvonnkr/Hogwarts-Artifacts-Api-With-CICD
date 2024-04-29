package com.yvolabs.hogwartsartifactsapi.hogwartsuser.converter;

import com.yvolabs.hogwartsartifactsapi.hogwartsuser.HogwartsUser;
import com.yvolabs.hogwartsartifactsapi.hogwartsuser.dto.UserDto;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

/**
 * @author Yvonne N
 */
@Component
public class UserDtoToUserConverter implements Converter<UserDto, HogwartsUser> {
    @Override
    public HogwartsUser convert(UserDto source) {

        return HogwartsUser.builder()
                .username(source.username())
                .enabled(source.enabled())
                .roles(source.roles())
                .build();
    }
}
