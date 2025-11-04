package com.github.amangusss.gym_application.health;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import javax.sql.DataSource;
import java.sql.Connection;

//TODO make some tests for this health indicator
@Slf4j
@Component
@RequiredArgsConstructor
public class DatabaseConnectionHealthIndicator implements HealthIndicator {

    private final DataSource dataSource;

    @Value("${spring.datasource.url}")
    private String dbUrl;

    @Value("${spring.datasource.username}")
    private String dbUsername;

    @Override
    public Health health() {
        try (Connection connection = dataSource.getConnection()) {
            if (connection != null && !connection.isClosed()) {
                log.debug("Database connection health check: UP");
                return Health.up()
                        .withDetail("database", "PostgreSQL")
                        .withDetail("status", "Connected")
                        .withDetail("url", dbUrl)
                        .withDetail("username", dbUsername)
                        .build();
            }
        } catch (Exception e) {
            log.error("Database connection health check failed: {}", e.getMessage());
            return Health.down()
                    .withDetail("database", "PostgreSQL")
                    .withDetail("status", "Disconnected")
                    .withDetail("url", dbUrl)
                    .withDetail("username", dbUsername)
                    .withDetail("error", e.getMessage())
                    .build();
        }

        return Health.down()
                .withDetail("database", "PostgreSQL")
                .withDetail("status", "Unknown")
                .withDetail("url", dbUrl)
                .build();
    }
}
