package com.yvolabs.hogwartsartifactsapi.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerExceptionResolver;

/**
 * @author Yvonne N
 * Spring Security throws exception before our controllers are invoked hence Errors thrown will not be caught by our "ExceptionHandlerAdvice.class" handlerMethods
 * This class implements AuthenticationEntryPoint and is responsible for handling un-successfull basic authentication.
 * If a basic authentication fails, this "commence" method will get called, we 1st add a header then deligate the work to handlerExceptionResolver.
 * This resolver will resolve the exception, so that this ex can be handled by an ex handler method in our controller advice class(ExceptionHandlerAdvice).
 * <p>
 * We then overide the default basic entry point in our "SecurityConfiguration.class"
 * Before Was:      SecurityConfiguration::securityFilterChain.httpBasic(Customizer.withDefaults())
 * After Overide:   .httpBasic(httpBasic -> httpBasic.authenticationEntryPoint(customBasicAuthenticationEntryPoint))
 * </p>
 * handles invalid username or password
 */
@Component
public class CustomBasicAuthenticationEntryPoint implements AuthenticationEntryPoint {
    /*
      Here we've injected the DefaultHandlerExceptionResolver and delegated the handler to this resolver.
      This security exception can now be handled with controller advice with an exception handler method.
     */
    private final HandlerExceptionResolver resolver;

    public CustomBasicAuthenticationEntryPoint(@Qualifier("handlerExceptionResolver") HandlerExceptionResolver resolver) {
        this.resolver = resolver;
    }

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) {
        response.addHeader("WWW-Authenticate", "Basic realm=\"Realm\"");
        this.resolver.resolveException(request, response, null, authException);
    }
}
