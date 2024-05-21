package com.yvolabs.hogwartsartifactsapi.system.actuator;

import org.springframework.boot.actuate.web.exchanges.HttpExchangeRepository;
import org.springframework.boot.actuate.web.exchanges.InMemoryHttpExchangeRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author Yvonne N
 * To access /actuator/httpexchanges enpoint we need to configure a HttpExchangeRepository
 */
@Configuration
public class ActuatorConfiguration {

    @Bean
    public HttpExchangeRepository httpExchangeRepository() {

        InMemoryHttpExchangeRepository repository = new InMemoryHttpExchangeRepository();
        repository.setCapacity(80); //default is 100
        return repository;
    }

}
