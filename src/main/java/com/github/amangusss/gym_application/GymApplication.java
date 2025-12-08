package com.github.amangusss.gym_application;

import lombok.extern.slf4j.Slf4j;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.scheduling.annotation.EnableScheduling;

@Slf4j
@EnableScheduling
@EnableDiscoveryClient
@SpringBootApplication
public class GymApplication {

    public static void main(String[] args) {
        SpringApplication.run(GymApplication.class, args);
        log.info("Gym Application started successfully. REST API is available.");
    }
}
