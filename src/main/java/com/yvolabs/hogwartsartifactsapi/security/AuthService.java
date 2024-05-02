package com.yvolabs.hogwartsartifactsapi.security;

import com.yvolabs.hogwartsartifactsapi.hogwartsuser.HogwartsUser;
import com.yvolabs.hogwartsartifactsapi.hogwartsuser.MyUserPrincipal;
import com.yvolabs.hogwartsartifactsapi.hogwartsuser.converter.UserToUserDtoConverter;
import com.yvolabs.hogwartsartifactsapi.hogwartsuser.dto.UserDto;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Yvonne N
 */
@Service
@RequiredArgsConstructor
public class AuthService {
    private final JwtProvider jwtProvider;
    private final UserToUserDtoConverter userToUserDtoConverter;

    public Map<String, Object> createLoginInfo(Authentication authentication) {
        // Create User Info
        MyUserPrincipal principal = (MyUserPrincipal) authentication.getPrincipal();
        HogwartsUser hogwartsUser = principal.getHogwartsUser();
        UserDto userDto = userToUserDtoConverter.convert(hogwartsUser);

        // Create a JWT
        String token = jwtProvider.createToken(authentication);

        // Create A Map
        Map<String, Object> loginResultMap = new HashMap<>();
        loginResultMap.put("userInfo", userDto);
        loginResultMap.put("token", token);

        return loginResultMap;
    }
}
