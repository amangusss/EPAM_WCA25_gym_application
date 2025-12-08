package com.github.amangusss.gym_application.config;

import io.github.cdimascio.dotenv.Dotenv;
import io.micrometer.common.lang.NonNullApi;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@NonNullApi
public class DotenvConfig implements ApplicationContextInitializer<ConfigurableApplicationContext> {

    @Override
    public void initialize(ConfigurableApplicationContext applicationContext) {
        try {
            Dotenv dotenv = Dotenv.configure()
                    .ignoreIfMissing()
                    .load();

            ConfigurableEnvironment environment = applicationContext.getEnvironment();
            Map<String, Object> dotenvProperties = new HashMap<>();

            dotenv.entries().forEach(entry -> {
                dotenvProperties.put(entry.getKey(), entry.getValue());
            });

            environment.getPropertySources()
                    .addFirst(new MapPropertySource("dotenvProperties", dotenvProperties));

            log.info("Successfully loaded .env file with {} properties", dotenvProperties.size());
        } catch (Exception e) {
            log.warn("Could not load .env file: {}", e.getMessage());
        }
    }
}

