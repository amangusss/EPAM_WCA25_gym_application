package com.github.amangusss.gym_application.util;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class UsernameGeneratorTest {

    private UsernameGenerator usernameGenerator;

    @BeforeEach
    void setUp() {
        usernameGenerator = new UsernameGenerator();
    }

    @Test
    void generateUsername_WithUniqueName_ShouldReturnFirstNameLastName() {
        // Given
        String firstName = "John";
        String lastName = "Doe";
        Set<String> existingUsernames = new HashSet<>();

        // When
        String result = usernameGenerator.generateUsername(firstName, lastName, existingUsernames);

        // Then
        assertEquals("John.Doe", result);
    }

    @Test
    void generateUsername_WithExistingName_ShouldAddNumber() {
        // Given
        String firstName = "John";
        String lastName = "Doe";
        Set<String> existingUsernames = new HashSet<>();
        existingUsernames.add("John.Doe");

        // When
        String result = usernameGenerator.generateUsername(firstName, lastName, existingUsernames);

        // Then
        assertEquals("John.Doe1", result);
    }

    @Test
    void generateUsername_WithMultipleExistingNames_ShouldAddIncrementalNumber() {
        // Given
        String firstName = "John";
        String lastName = "Doe";
        Set<String> existingUsernames = new HashSet<>();
        existingUsernames.add("John.Doe");
        existingUsernames.add("John.Doe1");
        existingUsernames.add("John.Doe2");

        // When
        String result = usernameGenerator.generateUsername(firstName, lastName, existingUsernames);

        // Then
        assertEquals("John.Doe3", result);
    }

    @Test
    void generateUsername_WithNullFirstName_ShouldThrowException() {
        // Given
        String firstName = null;
        String lastName = "Doe";
        Set<String> existingUsernames = new HashSet<>();

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> usernameGenerator.generateUsername(firstName, lastName, existingUsernames));
    }

    @Test
    void generateUsername_WithNullLastName_ShouldThrowException() {
        // Given
        String firstName = "John";
        String lastName = null;
        Set<String> existingUsernames = new HashSet<>();

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> usernameGenerator.generateUsername(firstName, lastName, existingUsernames));
    }

    @Test
    void generateUsername_WithWhitespace_ShouldTrim() {
        // Given
        String firstName = "  John  ";
        String lastName = "  Doe  ";
        Set<String> existingUsernames = new HashSet<>();

        // When
        String result = usernameGenerator.generateUsername(firstName, lastName, existingUsernames);

        // Then
        assertEquals("John.Doe", result);
    }
}
