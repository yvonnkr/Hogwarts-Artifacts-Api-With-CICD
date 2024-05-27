package com.yvolabs.hogwartsartifactsapi.system.exception;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nimbusds.jose.shaded.gson.JsonObject;
import com.nimbusds.jose.shaded.gson.JsonParser;
import com.yvolabs.hogwartsartifactsapi.system.Result;
import com.yvolabs.hogwartsartifactsapi.system.StatusCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mapping.PropertyReferenceException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.HashMap;
import java.util.List;

/**
 * @author Yvonne N
 */
@RestControllerAdvice
@Slf4j
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
                .message("No permission to access this resource")
                .data(ex.getMessage())
                .build();

    }

    @ExceptionHandler(InsufficientAuthenticationException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    Result handleInsufficientAuthenticationException(InsufficientAuthenticationException ex) {
        return Result.builder()
                .flag(false)
                .code(StatusCode.UNAUTHORIZED)
                .message("Login credentials are missing")
                .data(ex.getMessage())
                .build();
    }

    // Rest Client Error
    @ExceptionHandler({HttpClientErrorException.class, HttpServerErrorException.class})
    ResponseEntity<Result> handleRestClientException(HttpStatusCodeException ex) throws JsonProcessingException {

//         String formattedExceptionMessage = getFormattedErrorMessageUsingJackson(ex);
        String formattedExceptionMessage = getFormattedErrorMessageUsingGson(ex);

        Result result = Result.builder()
                .flag(false)
                .code(ex.getStatusCode().value())
                .message("A rest client error occurs, see data for details")
                .data(formattedExceptionMessage)
                .build();

        return new ResponseEntity<>(result, ex.getStatusCode());
    }

    // mapping errors
    @ExceptionHandler(PropertyReferenceException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    Result handlePropertyReferenceException(PropertyReferenceException ex) {
        return Result.builder()
                .flag(false)
                .code(StatusCode.INVALID_ARGUMENT)
                .message("Invalid property reference")
                .data(ex.getMessage())
                .build();
    }

    // endpoint errors
    @ExceptionHandler({NoHandlerFoundException.class, NoResourceFoundException.class})
    @ResponseStatus(HttpStatus.NOT_FOUND)
    Result handleNoHandlerFoundException(Exception ex) {
        return Result.builder()
                .flag(false)
                .code(StatusCode.NOT_FOUND)
                .message("This API endpoint was not found")
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
                .code(StatusCode.INTERNAL_SERVER_ERROR)
                .message("A server internal error occurred.")
                .data(ex.getMessage())
                .build();
    }

    private String getFormattedErrorMessageUsingGson(HttpStatusCodeException ex) {
        String exceptionMessage = ex.getMessage();

        // Replace <EOL> with actual newline characters
        exceptionMessage = exceptionMessage.replace("<EOL>", "\n");

        // Extract the JSON part from the string.
        String jsonString = exceptionMessage.substring(exceptionMessage.indexOf("{"), exceptionMessage.lastIndexOf("}") + 1);

        // Parse JSON string
        JsonObject jsonObject = JsonParser.parseString(jsonString).getAsJsonObject();

        String formattedExceptionMessage = jsonObject
                .getAsJsonObject("error")
                .get("message")
                .getAsString();

        log.info("formattedExceptionMessage: {}", formattedExceptionMessage);

        return formattedExceptionMessage;
    }

    private String getFormattedErrorMessageUsingJackson(HttpStatusCodeException ex) throws JsonProcessingException {

        String exceptionMessage = ex.getMessage();

        // Replace <EOL> with actual newlines.
        exceptionMessage = exceptionMessage.replace("<EOL>", "\n");

        // Extract the JSON part from the string.
        String jsonPart = exceptionMessage.substring(exceptionMessage.indexOf("{"), exceptionMessage.lastIndexOf("}") + 1);
        log.info("jsonPart = {} ", jsonPart);

        // Create an ObjectMapper instance.
        ObjectMapper objectMapper = new ObjectMapper();

        // Parse the JSON string to a JsonNode.
        JsonNode rootNode = objectMapper.readTree(jsonPart);
        log.info("rootNode = {} ", rootNode);

        // Extract the message.
        String formattedExceptionMessage = rootNode.path("error").path("message").asText();
        log.info("formattedExceptionMessage = {}", formattedExceptionMessage);

        return formattedExceptionMessage;

    }


}
