package com.github.amangusss.gym_application.util;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PasswordGeneratorTest {

    private PasswordGenerator passwordGenerator;

    @BeforeEach
    void setUp() {
        passwordGenerator = new PasswordGenerator();
    }

    @Test
    void generatePassword_ShouldReturnPasswordOfCorrectLength() {
        // When
        String password = passwordGenerator.generatePassword();

        // Then
        assertNotNull(password);
        assertEquals(10, password.length());
    }

    @Test
    void generatePassword_ShouldReturnDifferentPasswords() {
        // When
        String password1 = passwordGenerator.generatePassword();
        String password2 = passwordGenerator.generatePassword();

        // Then
        assertNotEquals(password1, password2);
    }

    @Test
    void generatePassword_ShouldContainOnlyValidCharacters() {
        // Given
        String validCharacters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";

        // When
        String password = passwordGenerator.generatePassword();

        // Then
        assertNotNull(password);
        for (char c : password.toCharArray()) {
            assertTrue(validCharacters.indexOf(c) >= 0, 
                "Password contains invalid character: " + c);
        }
    }

    @Test
    void generatePassword_MultipleCalls_ShouldGenerateDifferentPasswords() {
        // Given
        int numberOfPasswords = 100;
        String[] passwords = new String[numberOfPasswords];

        // When
        for (int i = 0; i < numberOfPasswords; i++) {
            passwords[i] = passwordGenerator.generatePassword();
        }

        // Then
        for (int i = 0; i < numberOfPasswords; i++) {
            for (int j = i + 1; j < numberOfPasswords; j++) {
                assertNotEquals(passwords[i], passwords[j], 
                    "Generated duplicate passwords at positions " + i + " and " + j);
            }
        }
    }
}
