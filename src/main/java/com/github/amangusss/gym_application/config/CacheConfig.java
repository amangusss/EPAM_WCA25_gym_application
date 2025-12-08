package com.github.amangusss.gym_application.config;

import com.github.benmanes.caffeine.cache.Caffeine;

import lombok.extern.slf4j.Slf4j;

import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Slf4j
@Configuration
@EnableCaching
public class CacheConfig {

    public static final String LOGIN_ATTEMPTS_CACHE = "loginAttempts";

    @Bean
    public CacheManager cacheManager() {
        log.info("Initializing Caffeine Cache Manager");

        CaffeineCacheManager cacheManager = new CaffeineCacheManager(LOGIN_ATTEMPTS_CACHE);
        cacheManager.setCaffeine(caffeineCacheBuilder());
        cacheManager.setAllowNullValues(false);

        log.debug("Caffeine Cache Manager configured with cache: {}", LOGIN_ATTEMPTS_CACHE);
        return cacheManager;
    }

    @Bean
    public Caffeine<Object, Object> caffeineCacheBuilder() {
        return Caffeine.newBuilder()
                .expireAfterWrite(Duration.ofMinutes(15))
                .maximumSize(10000)
                .recordStats()
                .removalListener((key, value, cause) ->
                    log.debug("Cache entry removed: key={}, cause={}", key, cause)
                );
    }
}
