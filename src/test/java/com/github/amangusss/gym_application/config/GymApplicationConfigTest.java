package com.github.amangusss.gym_application.config;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class GymApplicationConfigTest {

    @Test
    void gymApplicationConfig_ShouldBeInstantiable() {
        GymApplicationConfig config = new GymApplicationConfig();

        assertNotNull(config);
    }

    @Test
    void gymApplicationConfig_ShouldHaveComponentScanAnnotation() {
        Class<?> clazz = GymApplicationConfig.class;

        boolean hasComponentScan = clazz.isAnnotationPresent(org.springframework.context.annotation.ComponentScan.class);
        boolean hasConfiguration = clazz.isAnnotationPresent(org.springframework.context.annotation.Configuration.class);
        boolean hasPropertySource = clazz.isAnnotationPresent(org.springframework.context.annotation.PropertySource.class);

        assertTrue(hasComponentScan, "GymApplicationConfig should have @ComponentScan annotation");
        assertTrue(hasConfiguration, "GymApplicationConfig should have @Configuration annotation");
        assertTrue(hasPropertySource, "GymApplicationConfig should have @PropertySource annotation");
    }

    @Test
    void gymApplicationConfig_ShouldHaveCorrectPackageScan() {
        Class<?> clazz = GymApplicationConfig.class;
        org.springframework.context.annotation.ComponentScan componentScan = 
            clazz.getAnnotation(org.springframework.context.annotation.ComponentScan.class);

        assertNotNull(componentScan);
        String[] basePackages = componentScan.basePackages();

        assertEquals(0, basePackages.length);
    }

    @Test
    void gymApplicationConfig_ShouldHaveCorrectPropertySource() {
        Class<?> clazz = GymApplicationConfig.class;
        org.springframework.context.annotation.PropertySource propertySource = 
            clazz.getAnnotation(org.springframework.context.annotation.PropertySource.class);

        assertNotNull(propertySource);
        String[] locations = propertySource.value();

        assertEquals(1, locations.length);
        assertEquals("classpath:application.properties", locations[0]);
    }
}