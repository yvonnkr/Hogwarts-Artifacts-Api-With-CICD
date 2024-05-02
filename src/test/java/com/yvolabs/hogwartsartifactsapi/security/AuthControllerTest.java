package com.yvolabs.hogwartsartifactsapi.security;


import com.yvolabs.hogwartsartifactsapi.system.StatusCode;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

/**
 * @author Yvonne N
 */
@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("Integration tests for Auth API endpoint")
@Tag("Integration")
class AuthControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Value("${api.endpoint.base-url}/users")
    String PATH;

    @BeforeEach
    void setUp() {
    }

    @Test
    void testGetLoginInfoSuccess() throws Exception {

        mockMvc.perform(post(PATH + "/login")
                        .with(httpBasic("john", "123456")))
                .andExpect(jsonPath("$.flag").value(true))
                .andExpect(jsonPath("$.code").value(StatusCode.SUCCESS))
                .andExpect(jsonPath("$.message").value("User Info and JSON Web Token"))
                .andExpect(jsonPath("$.data.token").isNotEmpty())
                .andExpect(jsonPath("$.data.token", Matchers.containsString("eyJhbGciOiJSUzI1NiJ9")))
                .andExpect(jsonPath("$.data.userInfo.id").value(1))
                .andExpect(jsonPath("$.data.userInfo.username").value("john"))
                .andExpect(jsonPath("$.data.userInfo.enabled").value(true))
                .andExpect(jsonPath("$.data.userInfo.roles").value("admin user"));
    }

    @Test
    void testGetLoginInfoThrowsUsernameNotFoundExceptionWithInvalidCredentials() throws Exception {

        mockMvc.perform(post(PATH + "/login")
                        .with(httpBasic("unknownUsername", "wrongPassword")))
                .andExpect(jsonPath("$.flag").value(false))
                .andExpect(jsonPath("$.code").value(StatusCode.UNAUTHORIZED))
                .andExpect(jsonPath("$.message").value("username or password is incorrect"))
                .andExpect(jsonPath("$.data").value("Bad credentials"));
    }

    @Test
    void testGetLoginInfoThrowsAccountStatusExceptionWithDisabledUser() throws Exception {

        mockMvc.perform(post(PATH + "/login")
                        .with(httpBasic("tom", "qwerty")))
                .andExpect(jsonPath("$.flag").value(false))
                .andExpect(jsonPath("$.code").value(StatusCode.UNAUTHORIZED))
                .andExpect(jsonPath("$.message").value("User account is abnormal"))
                .andExpect(jsonPath("$.data").value("User is disabled"));
    }
    @Test
    void testGetLoginInfoThrowsInsufficientAuthenticationExceptionWithNoUsernameAndPassword() throws Exception {

        mockMvc.perform(post(PATH + "/login"))
                .andExpect(jsonPath("$.flag").value(false))
                .andExpect(jsonPath("$.code").value(StatusCode.UNAUTHORIZED))
                .andExpect(jsonPath("$.message").value("Full authentication is required to access this resource"))
                .andExpect(jsonPath("$.data").value("Full authentication is required to access this resource"));
    }
}