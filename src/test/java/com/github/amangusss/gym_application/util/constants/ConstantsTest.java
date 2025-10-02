package com.github.amangusss.gym_application.util.constants;

import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ConstantsTest {

    @Test
    void loggerConstants_ShouldHaveAllFieldsAsPublicStaticFinal() {
        Class<?> clazz = LoggerConstants.class;
        Field[] fields = clazz.getDeclaredFields();

        assertTrue(fields.length > 0, "LoggerConstants should have fields");
        
        for (Field field : fields) {
            assertTrue(Modifier.isPublic(field.getModifiers()), 
                "Field " + field.getName() + " should be public");
            assertTrue(Modifier.isStatic(field.getModifiers()), 
                "Field " + field.getName() + " should be static");
            assertTrue(Modifier.isFinal(field.getModifiers()), 
                "Field " + field.getName() + " should be final");
        }
    }

    @Test
    void validationConstants_ShouldHaveAllFieldsAsPublicStaticFinal() {
        Class<?> clazz = ValidationConstants.class;
        Field[] fields = clazz.getDeclaredFields();

        assertTrue(fields.length > 0, "ValidationConstants should have fields");
        
        for (Field field : fields) {
            assertTrue(Modifier.isPublic(field.getModifiers()), 
                "Field " + field.getName() + " should be public");
            assertTrue(Modifier.isStatic(field.getModifiers()), 
                "Field " + field.getName() + " should be static");
            assertTrue(Modifier.isFinal(field.getModifiers()), 
                "Field " + field.getName() + " should be final");
        }
    }

    @Test
    void demoConstants_ShouldHaveAllFieldsAsPublicStaticFinal() {
        Class<?> clazz = DemoConstants.class;
        Field[] fields = clazz.getDeclaredFields();

        assertTrue(fields.length > 0, "DemoConstants should have fields");
        
        for (Field field : fields) {
            assertTrue(Modifier.isPublic(field.getModifiers()), 
                "Field " + field.getName() + " should be public");
            assertTrue(Modifier.isStatic(field.getModifiers()), 
                "Field " + field.getName() + " should be static");
            assertTrue(Modifier.isFinal(field.getModifiers()), 
                "Field " + field.getName() + " should be final");
        }
    }

    @Test
    void configConstants_ShouldHaveAllFieldsAsPublicStaticFinal() {
        Class<?> clazz = ConfigConstants.class;
        Field[] fields = clazz.getDeclaredFields();

        assertTrue(fields.length > 0, "ConfigConstants should have fields");
        
        for (Field field : fields) {
            assertTrue(Modifier.isPublic(field.getModifiers()), 
                "Field " + field.getName() + " should be public");
            assertTrue(Modifier.isStatic(field.getModifiers()), 
                "Field " + field.getName() + " should be static");
            assertTrue(Modifier.isFinal(field.getModifiers()), 
                "Field " + field.getName() + " should be final");
        }
    }

    @Test
    void loggerConstants_ShouldNotBeEmpty() {
        Class<?> clazz = LoggerConstants.class;
        Field[] fields = clazz.getDeclaredFields();

        for (Field field : fields) {
            try {
                field.setAccessible(true);
                Object value = field.get(null);
                assertNotNull(value, "Field " + field.getName() + " should not be null");
                if (value instanceof String) {
                    assertFalse(((String) value).trim().isEmpty(), "Field " + field.getName() + " should not be empty");
                }
            } catch (IllegalAccessException e) {
                fail("Could not access field " + field.getName());
            }
        }
    }

    @Test
    void validationConstants_ShouldNotBeEmpty() {
        Class<?> clazz = ValidationConstants.class;
        Field[] fields = clazz.getDeclaredFields();

        for (Field field : fields) {
            try {
                field.setAccessible(true);
                Object value = field.get(null);
                assertNotNull(value, "Field " + field.getName() + " should not be null");
                if (value instanceof String) {
                    assertFalse(((String) value).trim().isEmpty(), "Field " + field.getName() + " should not be empty");
                }
            } catch (IllegalAccessException e) {
                fail("Could not access field " + field.getName());
            }
        }
    }

    @Test
    void demoConstants_ShouldNotBeEmpty() {
        Class<?> clazz = DemoConstants.class;
        Field[] fields = clazz.getDeclaredFields();

        for (Field field : fields) {
            try {
                field.setAccessible(true);
                Object value = field.get(null);
                assertNotNull(value, "Field " + field.getName() + " should not be null");
                if (value instanceof String) {
                    assertFalse(((String) value).trim().isEmpty(), "Field " + field.getName() + " should not be empty");
                }
            } catch (IllegalAccessException e) {
                fail("Could not access field " + field.getName());
            }
        }
    }

    @Test
    void configConstants_ShouldNotBeEmpty() {
        Class<?> clazz = ConfigConstants.class;
        Field[] fields = clazz.getDeclaredFields();

        for (Field field : fields) {
            try {
                field.setAccessible(true);
                Object value = field.get(null);
                assertNotNull(value, "Field " + field.getName() + " should not be null");
                if (value instanceof String) {
                    assertFalse(((String) value).trim().isEmpty(), "Field " + field.getName() + " should not be empty");
                }
            } catch (IllegalAccessException e) {
                fail("Could not access field " + field.getName());
            }
        }
    }

    @Test
    void allConstantsClasses_ShouldBeFinal() {
        List<Class<?>> constantClasses = Arrays.asList(
            LoggerConstants.class,
            ValidationConstants.class,
            DemoConstants.class,
            ConfigConstants.class
        );

        for (Class<?> clazz : constantClasses) {
            assertTrue(Modifier.isFinal(clazz.getModifiers()), 
                "Class " + clazz.getSimpleName() + " should be final");
        }
    }

    @Test
    void allConstantsClasses_ShouldHavePrivateConstructor() {
        List<Class<?>> constantClasses = Arrays.asList(
            LoggerConstants.class,
            ValidationConstants.class,
            DemoConstants.class,
            ConfigConstants.class
        );

        for (Class<?> clazz : constantClasses) {
            try {
                var constructor = clazz.getDeclaredConstructor();
                assertTrue(Modifier.isPrivate(constructor.getModifiers()), 
                    "Class " + clazz.getSimpleName() + " should have private constructor");
            } catch (NoSuchMethodException e) {
                fail("Class " + clazz.getSimpleName() + " should have a no-args constructor");
            }
        }
    }
}