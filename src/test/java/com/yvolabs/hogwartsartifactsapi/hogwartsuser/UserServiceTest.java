package com.yvolabs.hogwartsartifactsapi.hogwartsuser;

import com.yvolabs.hogwartsartifactsapi.system.exception.ObjectNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

/**
 * @author Yvonne N
 */
@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    @Mock
    UserRepository userRepository;

    @Mock
    PasswordEncoder passwordEncoder;

    @InjectMocks
    UserServiceImpl userService;

    List<HogwartsUser> userList;

    @BeforeEach
    void setUp() {
        setUserTestData();
    }

    @Test
    void testFindAllSuccess() {
        given(userRepository.findAll()).willReturn(userList);
        List<HogwartsUser> users = userService.findAll();

        assertThat(users).hasSize(userList.size());
        verify(userRepository).findAll();
    }

    @Test
    void testAddUserSuccess() {
        HogwartsUser newUser = HogwartsUser.builder()
                .id(4)
                .username("some username")
                .password("some-password-123")
                .enabled(true)
                .roles("admin newUser")
                .build();

        given(passwordEncoder.encode(newUser.getPassword())).willReturn("encoded-password");
        given(userRepository.save(newUser)).willReturn(newUser);

        HogwartsUser savedUser = userService.save(newUser);
        assertThat(savedUser).isNotNull();
        assertThat(savedUser.getId()).isEqualTo(4);
        assertThat(savedUser.getUsername()).isEqualTo("some username");
        assertThat(savedUser.getPassword()).isEqualTo("encoded-password");
        assertThat(savedUser.isEnabled()).isEqualTo(true);
        assertThat(savedUser.getRoles()).isEqualTo("admin newUser");
        verify(userRepository).save(newUser);
    }

    @Test
    void testFindByIdSuccess() {
        int userId = 1;
        HogwartsUser foundUser = userList.get(0);

        given(userRepository.findById(userId)).willReturn(Optional.of(foundUser));
        HogwartsUser user = userService.findById(userId);
        assertThat(user).isNotNull();
        assertThat(user.getId()).isEqualTo(userId);
        assertThat(user.getUsername()).isEqualTo(foundUser.getUsername());
        assertThat(user.getPassword()).isEqualTo(foundUser.getPassword());
        assertThat(user.isEnabled()).isEqualTo(foundUser.isEnabled());
        assertThat(user.getRoles()).isEqualTo(foundUser.getRoles());
        verify(userRepository).findById(userId);

    }

    @Test
    void testFindByIdThrowsForNonExistingUserid() {
        int userId = 1;
        given(userRepository.findById(userId)).willReturn(Optional.empty());

        Throwable throwable = catchThrowable(() -> userService.findById(userId));

        assertThat(throwable)
                .isInstanceOf(ObjectNotFoundException.class)
                .hasMessageContaining("Could not find user with Id 1");
        verify(userRepository).findById(userId);
    }

    @Test
    void testUpdateSuccess() {
        int userId = 1;
        HogwartsUser oldUser = userList.get(0);
        HogwartsUser update = HogwartsUser.builder()
                .username("john update")
                .password("123456")
                .enabled(true)
                .roles("user")
                .build();

        given(userRepository.findById(userId)).willReturn(Optional.of(oldUser));
        given(userRepository.save(oldUser)).willReturn(oldUser);
        HogwartsUser updatedUser = userService.update(userId, update);

        assertThat(updatedUser).isNotNull();
        assertThat(updatedUser.getId()).isEqualTo(userId);
        assertThat(updatedUser.getUsername()).isEqualTo("john update");
        assertThat(updatedUser.getPassword()).isEqualTo("123456");
        assertThat(updatedUser.isEnabled()).isEqualTo(true);
        assertThat(updatedUser.getRoles()).isEqualTo("user");
        verify(userRepository).findById(userId);
    }

    @Test
    void testUpdateThrowsForNonExistingUserid() {
        int userId = 1;
        HogwartsUser update = HogwartsUser.builder()
                .username("john update")
                .password("123456")
                .enabled(true)
                .roles("user")
                .build();

        given(userRepository.findById(userId)).willReturn(Optional.empty());

        Throwable throwable = catchThrowable(() -> userService.update(userId, update));
        assertThat(throwable)
                .isInstanceOf(ObjectNotFoundException.class)
                .hasMessageContaining("Could not find user with Id 1");
        verify(userRepository).findById(userId);
    }

    @Test
    void deleteSuccess() {
        int userId = 1;
        given(userRepository.findById(userId)).willReturn(Optional.of(userList.get(0)));
        doNothing().when(userRepository).deleteById(userId);
        userService.delete(userId);

        verify(userRepository).deleteById(userId);

    }

    @Test
    void deleteThrowsForNonExistingUserid() {
        int userId = 1;
        given(userRepository.findById(userId)).willReturn(Optional.empty());

        Throwable throwable = catchThrowable(() -> userService.delete(userId));

        assertThat(throwable)
                .isInstanceOf(ObjectNotFoundException.class)
                .hasMessageContaining("Could not find user with Id 1");
        verify(userRepository).findById(userId);
        verify(userRepository, times(0)).deleteById(userId);

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