package com.yvolabs.hogwartsartifactsapi.hogwartsuser;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.yvolabs.hogwartsartifactsapi.hogwartsuser.dto.UserDto;
import com.yvolabs.hogwartsartifactsapi.system.StatusCode;
import com.yvolabs.hogwartsartifactsapi.system.exception.ObjectNotFoundException;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * @author Yvonne N
 */
@WebMvcTest(controllers = UserController.class)
@AutoConfigureMockMvc(addFilters = false) //disable spring security as this is unit test, tested in integration tests instead
class UserControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @Autowired
    ObjectMapper objectMapper;

    @Value("${api.endpoint.base-url}/users")
    String PATH;

    List<HogwartsUser> userList;

    @BeforeEach
    void setUp() {
        setUserTestData();
    }

    @Test
    void testFindAllUsersSuccess() throws Exception {
        given(userService.findAll()).willReturn(userList);

        mockMvc.perform(get(PATH).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.flag").value(true))
                .andExpect(jsonPath("$.code").value(StatusCode.SUCCESS))
                .andExpect(jsonPath("$.message").value("Find All Success"))
                .andExpect(jsonPath("$.data", Matchers.hasSize(userList.size())));
        verify(userService).findAll();
    }

    @Test
    void testAddUserSuccess() throws Exception {
        HogwartsUser newUser = HogwartsUser.builder()
                .id(4)
                .username("some username")
                .password("some-password-123")
                .enabled(true)
                .roles("admin newUser")
                .build();

        given(userService.save(newUser)).willReturn(newUser);

        String jsonRequest = objectMapper.writeValueAsString(newUser);

        mockMvc.perform(post(PATH)
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.flag").value(true))
                .andExpect(jsonPath("$.code").value(StatusCode.SUCCESS))
                .andExpect(jsonPath("$.message").value("Add Success"))
                .andExpect(jsonPath("$.data.id").value(4))
                .andExpect(jsonPath("$.data.username").value("some username"))
                .andExpect(jsonPath("$.data.password").doesNotExist())
                .andExpect(jsonPath("$.data.enabled").value(true))
                .andExpect(jsonPath("$.data.roles").value("admin newUser"));
        verify(userService).save(newUser);
    }

    @Test
    void testAddUserValidation() throws Exception {
        HogwartsUser newUser = HogwartsUser.builder()
                .username("")
                .password("")
                .enabled(false)
                .roles("")
                .build();

        String jsonRequest = objectMapper.writeValueAsString(newUser);

        mockMvc.perform(post(PATH)
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(result -> assertInstanceOf(MethodArgumentNotValidException.class, result.getResolvedException()))
                .andExpect(jsonPath("$.flag").value(false))
                .andExpect(jsonPath("$.code").value(StatusCode.INVALID_ARGUMENT))
                .andExpect(jsonPath("$.message").value("Provided arguments are invalid, see data for details."))
                .andExpect(jsonPath("$.data.username").value("username is required."))
                .andExpect(jsonPath("$.data.password").value("password is required."))
                .andExpect(jsonPath("$.data.roles").value("roles are required."));
        verify(userService, times(0)).save(any());
    }

    @Test
    void testFindUserByIdSuccess() throws Exception {
        int userId = 1;
        HogwartsUser foundUser = userList.get(0);
        given(userService.findById(userId)).willReturn(foundUser);

        mockMvc.perform(get(PATH + "/" + userId).accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.flag").value(true))
                .andExpect(jsonPath("$.code").value(StatusCode.SUCCESS))
                .andExpect(jsonPath("$.message").value("Find One Success"))
                .andExpect(jsonPath("$.data.id").value(foundUser.getId()))
                .andExpect(jsonPath("$.data.username").value(foundUser.getUsername()))
                .andExpect(jsonPath("$.data.password").doesNotExist())
                .andExpect(jsonPath("$.data.enabled").value(foundUser.isEnabled()))
                .andExpect(jsonPath("$.data.roles").value(foundUser.getRoles()));
        verify(userService).findById(userId);

    }

    @Test
    void testFindUserByIdThrowsForNonExistentId() throws Exception {
        int userId = 1;
        given(userService.findById(userId)).willThrow(new ObjectNotFoundException("user", userId));

        mockMvc.perform(get(PATH + "/" + userId).accept(MediaType.APPLICATION_JSON))
                .andExpect(result -> assertInstanceOf(ObjectNotFoundException.class, result.getResolvedException()))
                .andExpect(jsonPath("$.flag").value(false))
                .andExpect(jsonPath("$.code").value(StatusCode.NOT_FOUND))
                .andExpect(jsonPath("$.message").value("Could not find user with Id 1"));
        verify(userService).findById(userId);
    }

    @Test
    void testUpdateUserSuccess() throws Exception {
        int userId = 1;
        UserDto update = UserDto.builder()
                .username("john update")
                .enabled(false)
                .roles("user")
                .build();
        HogwartsUser updatedUser = HogwartsUser.builder()
                .id(userId)
                .username("john update")
                .password("123456")
                .enabled(false)
                .roles("user")
                .build();

        String jsonUpdate = objectMapper.writeValueAsString(update);

        given(userService.update(eq(userId), any())).willReturn(updatedUser);

        mockMvc.perform(put(PATH + "/" + userId)
                        .content(jsonUpdate)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.flag").value(true))
                .andExpect(jsonPath("$.code").value(StatusCode.SUCCESS))
                .andExpect(jsonPath("$.message").value("Update Success"))
                .andExpect(jsonPath("$.data.id").value(userId))
                .andExpect(jsonPath("$.data.username").value("john update"))
                .andExpect(jsonPath("$.data.password").doesNotExist())
                .andExpect(jsonPath("$.data.enabled").value(false))
                .andExpect(jsonPath("$.data.roles").value("user"));
        verify(userService).update(eq(userId), any());
    }

    @Test
    void testUpdateUserValidation() throws Exception {
        int userId = 1;
        UserDto update = UserDto.builder()
                .username("")
                .enabled(false)
                .roles("")
                .build();
        String jsonUpdate = objectMapper.writeValueAsString(update);

        mockMvc.perform(put(PATH + "/" + userId)
                        .content(jsonUpdate)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(result -> assertInstanceOf(MethodArgumentNotValidException.class, result.getResolvedException()))
                .andExpect(jsonPath("$.flag").value(false))
                .andExpect(jsonPath("$.code").value(StatusCode.INVALID_ARGUMENT))
                .andExpect(jsonPath("$.message").value("Provided arguments are invalid, see data for details."))
                .andExpect(jsonPath("$.data.username").value("username is required."))
                .andExpect(jsonPath("$.data.roles").value("roles are required."));
        verify(userService, times(0)).update(eq(userId), any());
    }

    @Test
    void testUpdateUserThrowsForNonExistentId() throws Exception {
        int userId = 1;
        UserDto update = UserDto.builder()
                .username("john update")
                .enabled(false)
                .roles("user")
                .build();
        String jsonUpdate = objectMapper.writeValueAsString(update);

        given(userService.update(eq(userId), any())).willThrow(new ObjectNotFoundException("user", userId));

        mockMvc.perform(put(PATH + "/" + userId)
                        .content(jsonUpdate)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(result -> assertInstanceOf(ObjectNotFoundException.class, result.getResolvedException()))
                .andExpect(jsonPath("$.flag").value(false))
                .andExpect(jsonPath("$.code").value(StatusCode.NOT_FOUND))
                .andExpect(jsonPath("$.message").value("Could not find user with Id 1"));
        verify(userService).update(eq(userId), any());
    }

    @Test
    void testDeleteUserSuccess() throws Exception {
        int userId = 1;
        doNothing().when(userService).delete(userId);

        mockMvc.perform(delete(PATH + "/" + userId).accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.flag").value(true))
                .andExpect(jsonPath("$.code").value(StatusCode.SUCCESS))
                .andExpect(jsonPath("$.message").value("Delete Success"))
                .andExpect(jsonPath("$.data").doesNotExist());
        verify(userService).delete(userId);
    }

    @Test
    void testDeleteUserThrowsForNonExistentId() throws Exception {
        int userId = 1;
        doThrow(new ObjectNotFoundException("user", userId)).when(userService).delete(userId);

        mockMvc.perform(delete(PATH + "/" + userId).accept(MediaType.APPLICATION_JSON))
                .andExpect(result -> assertInstanceOf(ObjectNotFoundException.class, result.getResolvedException()))
                .andExpect(jsonPath("$.flag").value(false))
                .andExpect(jsonPath("$.code").value(StatusCode.NOT_FOUND))
                .andExpect(jsonPath("$.message").value("Could not find user with Id 1"));
        verify(userService).delete(userId);
    }

    void setUserTestData() {
        HogwartsUser u1 = new HogwartsUser();
        u1.setId(1);
        u1.setUsername("john");
        u1.setPassword("123456");
        u1.setEnabled(true);
        u1.setRoles("admin user");

        HogwartsUser u2 = new HogwartsUser();
        u2.setId(2);
        u2.setUsername("eric");
        u2.setPassword("654321");
        u2.setEnabled(true);
        u2.setRoles("user");

        HogwartsUser u3 = new HogwartsUser();
        u3.setId(3);
        u3.setUsername("tom");
        u3.setPassword("qwerty");
        u3.setEnabled(false);
        u3.setRoles("user");

        userList = new ArrayList<>();
        userList.add(u1);
        userList.add(u2);
        userList.add(u3);

    }
}