package com.github.amangusss.gym_application.util;

import com.github.amangusss.gym_application.exception.ValidationException;
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
        String firstName = "John";
        String lastName = "Doe";
        Set<String> existingUsernames = new HashSet<>();

        String result = usernameGenerator.generateUsername(firstName, lastName, existingUsernames);

        assertEquals("John.Doe", result);
    }

    @Test
    void generateUsername_WithExistingName_ShouldAddNumber() {
        String firstName = "John";
        String lastName = "Doe";
        Set<String> existingUsernames = new HashSet<>();
        existingUsernames.add("John.Doe");

        String result = usernameGenerator.generateUsername(firstName, lastName, existingUsernames);

        assertEquals("John.Doe1", result);
    }

    @Test
    void generateUsername_WithMultipleExistingNames_ShouldAddIncrementalNumber() {
        String firstName = "John";
        String lastName = "Doe";
        Set<String> existingUsernames = new HashSet<>();
        existingUsernames.add("John.Doe");
        existingUsernames.add("John.Doe1");
        existingUsernames.add("John.Doe2");

        String result = usernameGenerator.generateUsername(firstName, lastName, existingUsernames);

        assertEquals("John.Doe3", result);
    }

    @Test
    void generateUsername_WithNullFirstName_ShouldThrowException() {
        String firstName = null;
        String lastName = "Doe";
        Set<String> existingUsernames = new HashSet<>();

        assertThrows(ValidationException.class, () -> usernameGenerator.generateUsername(firstName, lastName, existingUsernames));
    }

    @Test
    void generateUsername_WithNullLastName_ShouldThrowException() {
        String firstName = "John";
        String lastName = null;
        Set<String> existingUsernames = new HashSet<>();

        assertThrows(ValidationException.class, () -> usernameGenerator.generateUsername(firstName, lastName, existingUsernames));
    }

    @Test
    void generateUsername_WithWhitespace_ShouldTrim() {
        String firstName = "  John  ";
        String lastName = "  Doe  ";
        Set<String> existingUsernames = new HashSet<>();

        String result = usernameGenerator.generateUsername(firstName, lastName, existingUsernames);

        assertEquals("John.Doe", result);
    }
}