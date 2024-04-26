package com.yvolabs.hogwartsartifactsapi;

import com.yvolabs.hogwartsartifactsapi.utils.IdWorker;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class HogwartsArtifactsApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(HogwartsArtifactsApiApplication.class, args);
    }

    @Bean
    public IdWorker idWorker(){
        return new IdWorker(1, 1);
    }

}
