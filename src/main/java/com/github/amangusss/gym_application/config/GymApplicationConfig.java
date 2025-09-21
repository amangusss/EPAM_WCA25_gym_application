package com.github.amangusss.gym_application.config;

import com.github.amangusss.gym_application.util.constants.LoggerConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.*;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;

import javax.annotation.PostConstruct;

@Configuration
@ComponentScan("com.github.amangusss.gym_application")
@PropertySource("classpath:application.properties")
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
        logger.info(LoggerConstants.CONFIG_INITIALIZED);
    }
}
