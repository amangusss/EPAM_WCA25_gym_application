package com.github.amangusss.gym_application.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI gymApplicationOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Gym Application API")
                        .description("REST API for Gym Management System - WCA#25 Capstone Project")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Aman Nazarkulov")
                                .email("your-email@example.com")));
    }
}
