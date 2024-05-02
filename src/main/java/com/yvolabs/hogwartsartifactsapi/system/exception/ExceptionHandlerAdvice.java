package com.yvolabs.hogwartsartifactsapi.system.exception;

import com.yvolabs.hogwartsartifactsapi.system.Result;
import com.yvolabs.hogwartsartifactsapi.system.StatusCode;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AccountStatusException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.server.resource.InvalidBearerTokenException;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.List;

/**
 * @author Yvonne N
 */
@RestControllerAdvice
public class ExceptionHandlerAdvice {

    @ExceptionHandler(ObjectNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    Result handleObjectNotFoundException(ObjectNotFoundException ex) {
        return Result.builder()
                .flag(false)
                .code(StatusCode.NOT_FOUND)
                .message(ex.getMessage())
                .build();
    }


    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    Result handleValidationException(MethodArgumentNotValidException ex) {
        List<ObjectError> errors = ex.getBindingResult().getAllErrors();
        HashMap<Object, Object> map = new HashMap<>(errors.size());
        errors.forEach(error -> {
            String key = ((FieldError) error).getField();
            String value = error.getDefaultMessage();
            map.put(key, value);
        });

        return Result.builder()
                .flag(false)
                .code(StatusCode.INVALID_ARGUMENT)
                .message("Provided arguments are invalid, see data for details.")
                .data(map)
                .build();
    }

    // Security Errors

    @ExceptionHandler({UsernameNotFoundException.class, BadCredentialsException.class})
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    Result handleAuthenticationException(Exception ex) {
        return Result.builder()
                .flag(false)
                .code(StatusCode.UNAUTHORIZED)
                .message("username or password is incorrect")
                .data(ex.getMessage())
                .build();
    }

    @ExceptionHandler(AccountStatusException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    Result handleAccountStatusException(AccountStatusException ex) {
        return Result.builder()
                .flag(false)
                .code(StatusCode.UNAUTHORIZED)
                .message("User account is abnormal")
                .data(ex.getMessage())
                .build();
    }

    @ExceptionHandler(InvalidBearerTokenException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    Result handleInvalidBearerTokenException(InvalidBearerTokenException ex) {
        return Result.builder()
                .flag(false)
                .code(StatusCode.UNAUTHORIZED)
                .message("The access token provided is expired, revoked, malformed or invalid for other reasons.")
                .data(ex.getMessage())
                .build();
    }

    @ExceptionHandler(AccessDeniedException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    Result handleAccessDeniedException(AccessDeniedException ex) {
        return Result.builder()
                .flag(false)
                .code(StatusCode.FORBIDDEN)
                .message("No permission")
                .data(ex.getMessage())
                .build();
    }

    @ExceptionHandler(InsufficientAuthenticationException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    Result handleInsufficientAuthenticationException(InsufficientAuthenticationException ex) {
        return Result.builder()
                .flag(false)
                .code(StatusCode.UNAUTHORIZED)
                .message("Full authentication is required to access this resource")
                .data(ex.getMessage())
                .build();
    }

    /**
     * Fallback handles any unhandled exception
     */
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    Result handleOtherException(Exception ex) {
        return Result.builder()
                .flag(false)
                .code(StatusCode.UNAUTHORIZED)
                .message("A server internal error occurred.")
                .data(ex.getMessage())
                .build();
    }

}
