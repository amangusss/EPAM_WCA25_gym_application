package com.github.amangusss.gym_application;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class GymApplication {

    private static final Logger logger = LoggerFactory.getLogger(GymApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(GymApplication.class, args);
        logger.info("Gym Application started successfully. REST API is available.");
    }
}
