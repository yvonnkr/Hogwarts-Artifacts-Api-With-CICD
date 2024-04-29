package com.yvolabs.hogwartsartifactsapi.hogwartsuser;

import com.yvolabs.hogwartsartifactsapi.hogwartsuser.converter.UserDtoToUserConverter;
import com.yvolabs.hogwartsartifactsapi.hogwartsuser.converter.UserToUserDtoConverter;
import com.yvolabs.hogwartsartifactsapi.hogwartsuser.dto.UserDto;
import com.yvolabs.hogwartsartifactsapi.system.Result;
import com.yvolabs.hogwartsartifactsapi.system.StatusCode;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author Yvonne N
 */
@RestController
@RequestMapping("${api.endpoint.base-url}/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final UserToUserDtoConverter userToUserDtoConverter;
    private final UserDtoToUserConverter userDtoToUserConverter;

    @GetMapping
    public ResponseEntity<Result> findAllUsers() {
        List<HogwartsUser> users = userService.findAll();

        List<UserDto> userDtos = users.stream()
                .map(userToUserDtoConverter::convert)
                .toList();

        Result result = Result.builder()
                .flag(true)
                .code(StatusCode.SUCCESS)
                .message("Find All Success")
                .data(userDtos)
                .build();

        return ResponseEntity.ok(result);

    }

    @PostMapping
    public ResponseEntity<Result> addUser(@RequestBody @Valid HogwartsUser user) {
        HogwartsUser savedUser = userService.save(user);
        UserDto userDto = userToUserDtoConverter.convert(savedUser);
        Result result = Result.builder()
                .flag(true)
                .code(StatusCode.SUCCESS)
                .message("Add Success")
                .data(userDto)
                .build();

        return ResponseEntity.ok(result);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<Result> findUserById(@PathVariable Integer userId) {
        HogwartsUser foundUser = userService.findById(userId);
        UserDto userDto = userToUserDtoConverter.convert(foundUser);

        Result result = Result.builder()
                .flag(true)
                .code(StatusCode.SUCCESS)
                .message("Find One Success")
                .data(userDto)
                .build();

        return ResponseEntity.ok(result);
    }

    @PutMapping("/{userId}")
    public ResponseEntity<Result> updateUser(@PathVariable Integer userId, @RequestBody @Valid UserDto userDto) {
        HogwartsUser update = userDtoToUserConverter.convert(userDto);
        HogwartsUser updatedUser = userService.update(userId, update);
        UserDto updatedUserDto = userToUserDtoConverter.convert(updatedUser);
        Result result = Result.builder()
                .flag(true)
                .code(StatusCode.SUCCESS)
                .message("Update Success")
                .data(updatedUserDto)
                .build();

        return ResponseEntity.ok(result);
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Result> deleteUser(@PathVariable Integer userId) {
        userService.delete(userId);
        Result result = Result.builder()
                .flag(true)
                .code(StatusCode.SUCCESS)
                .message("Delete Success")
                .data(null)
                .build();
        return ResponseEntity.ok(result);
    }

}
