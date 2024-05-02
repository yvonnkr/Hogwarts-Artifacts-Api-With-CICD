package com.yvolabs.hogwartsartifactsapi.security;

import com.yvolabs.hogwartsartifactsapi.system.Result;
import com.yvolabs.hogwartsartifactsapi.system.StatusCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * @author Yvonne N
 */
@RestController
@RequestMapping("${api.endpoint.base-url}/users")
@RequiredArgsConstructor
@Slf4j
public class AuthController {
    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<Result> getLoginInfo(Authentication authentication) {
        log.debug("Authenticated user: {}", authentication.getName());

        Map<String, Object> loginInfo = authService.createLoginInfo(authentication);

        Result result = Result.builder()
                .flag(true)
                .code(StatusCode.SUCCESS)
                .message("User Info and JSON Web Token")
                .data(loginInfo)
                .build();

        return ResponseEntity.ok(result);
    }
}
