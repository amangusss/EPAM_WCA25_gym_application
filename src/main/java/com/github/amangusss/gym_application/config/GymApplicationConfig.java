package com.github.amangusss.gym_application.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;

import javax.annotation.PostConstruct;

@Configuration
@ComponentScan("com.github.amangusss.gym_application")
@PropertySource("classpath:application.properties")
@Import(HibernateConfig.class)
public class GymApplicationConfig {

    public static final Logger logger = LoggerFactory.getLogger(GymApplicationConfig.class);

    @Bean
    public static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
        PropertySourcesPlaceholderConfigurer configurer = new PropertySourcesPlaceholderConfigurer();
        configurer.setIgnoreUnresolvablePlaceholders(true);
        return configurer;
    }

    @PostConstruct
    public void init() {
        logger.info("Gym application configuration initialized");
    }
}
