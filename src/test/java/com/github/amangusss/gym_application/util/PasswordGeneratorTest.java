package com.github.amangusss.gym_application.util;

import com.github.amangusss.gym_application.util.credentials.PasswordGenerator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.RepeatedTest;

import java.util.HashSet;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("PasswordGenerator Tests")
class PasswordGeneratorTest {

    private static final String PASSWORD_CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%^&*";
    private static final int PASSWORD_LENGTH = 10;

    private PasswordGenerator passwordGenerator;

    @BeforeEach
    void setUp() {
        passwordGenerator = new PasswordGenerator(PASSWORD_CHARACTERS, PASSWORD_LENGTH);
    }

    @Nested
    @DisplayName("Password Generation Tests")
    class PasswordGenerationTests {

        @Test
        @DisplayName("Should generate password with correct length")
        void shouldGeneratePasswordWithCorrectLength() {
            String password = passwordGenerator.generatePassword();

            assertThat(password).hasSize(PASSWORD_LENGTH);
        }

        @Test
        @DisplayName("Should generate password with characters from allowed set")
        void shouldGeneratePasswordWithAllowedCharacters() {
            String password = passwordGenerator.generatePassword();

            for (char c : password.toCharArray()) {
                assertThat(PASSWORD_CHARACTERS).contains(String.valueOf(c));
            }
        }

        @RepeatedTest(10)
        @DisplayName("Should generate different passwords on each call")
        void shouldGenerateDifferentPasswords() {
            Set<String> passwords = new HashSet<>();

            for (int i = 0; i < 100; i++) {
                passwords.add(passwordGenerator.generatePassword());
            }

            assertThat(passwords.size()).isGreaterThan(90);
        }

        @Test
        @DisplayName("Should generate non-empty password")
        void shouldGenerateNonEmptyPassword() {
            String password = passwordGenerator.generatePassword();

            assertThat(password).isNotNull();
            assertThat(password).isNotEmpty();
        }
    }

    @Nested
    @DisplayName("Configuration Tests")
    class ConfigurationTests {

        @Test
        @DisplayName("Should respect custom password length")
        void shouldRespectCustomPasswordLength() {
            PasswordGenerator customGenerator = new PasswordGenerator(PASSWORD_CHARACTERS, 20);

            String password = customGenerator.generatePassword();

            assertThat(password).hasSize(20);
        }

        @Test
        @DisplayName("Should use only characters from custom character set")
        void shouldUseOnlyCustomCharacters() {
            String customChars = "ABC123";
            PasswordGenerator customGenerator = new PasswordGenerator(customChars, 10);

            String password = customGenerator.generatePassword();

            for (char c : password.toCharArray()) {
                assertThat(customChars).contains(String.valueOf(c));
            }
        }
    }
}
