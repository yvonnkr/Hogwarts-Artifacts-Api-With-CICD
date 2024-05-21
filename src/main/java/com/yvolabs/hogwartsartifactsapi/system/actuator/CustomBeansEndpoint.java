package com.yvolabs.hogwartsartifactsapi.system.actuator;

import com.yvolabs.hogwartsartifactsapi.system.Result;
import com.yvolabs.hogwartsartifactsapi.system.StatusCode;
import org.springframework.boot.actuate.endpoint.annotation.Endpoint;
import org.springframework.boot.actuate.endpoint.annotation.ReadOperation;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

/**
 * @author Yvonne N
 */
@Component
@Endpoint(id = "custom-beans")
public class CustomBeansEndpoint {
    private final ApplicationContext applicationContext;

    public CustomBeansEndpoint(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @ReadOperation
    public Result beanCount(){

        int beanDefinitionCount = applicationContext.getBeanDefinitionCount();

        return Result.builder()
                .flag(true)
                .code(StatusCode.SUCCESS)
                .message("Beans Count from custom-endpoint")
                .data(beanDefinitionCount)
                .build();
    }


}
