package com.github.amangusss.gym_application.util;

import com.github.amangusss.gym_application.exception.ValidationException;
import com.github.amangusss.gym_application.util.credentials.UsernameGenerator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("UsernameGenerator Tests")
class UsernameGeneratorTest {

    private UsernameGenerator usernameGenerator;

    @BeforeEach
    void setUp() {
        usernameGenerator = new UsernameGenerator();
    }

    @Nested
    @DisplayName("Username Generation Tests")
    class UsernameGenerationTests {

        @Test
        @DisplayName("Should generate username from first and last name")
        void shouldGenerateUsernameFromNames() {
            String username = usernameGenerator.generateUsername("John", "Doe", name -> false);

            assertThat(username).isEqualTo("John.Doe");
        }

        @Test
        @DisplayName("Should generate username with suffix when base username exists")
        void shouldGenerateUsernameWithSuffix() {
            Set<String> existingUsernames = new HashSet<>();
            existingUsernames.add("John.Doe");

            String username = usernameGenerator.generateUsername("John", "Doe", existingUsernames::contains);

            assertThat(username).isEqualTo("John.Doe1");
        }

        @Test
        @DisplayName("Should increment suffix until unique username is found")
        void shouldIncrementSuffixUntilUnique() {
            Set<String> existingUsernames = new HashSet<>();
            existingUsernames.add("John.Doe");
            existingUsernames.add("John.Doe1");
            existingUsernames.add("John.Doe2");

            String username = usernameGenerator.generateUsername("John", "Doe", existingUsernames::contains);

            assertThat(username).isEqualTo("John.Doe3");
        }

        @Test
        @DisplayName("Should trim whitespace from names")
        void shouldTrimWhitespaceFromNames() {
            String username = usernameGenerator.generateUsername("  John  ", "  Doe  ", name -> false);

            assertThat(username).isEqualTo("John.Doe");
        }
    }

    @Nested
    @DisplayName("Validation Tests")
    class ValidationTests {

        @Test
        @DisplayName("Should throw ValidationException when firstName is null")
        void shouldThrowExceptionWhenFirstNameIsNull() {
            assertThatThrownBy(() -> usernameGenerator.generateUsername(null, "Doe", name -> false))
                    .isInstanceOf(ValidationException.class)
                    .hasMessageContaining("must not be null");
        }

        @Test
        @DisplayName("Should throw ValidationException when lastName is null")
        void shouldThrowExceptionWhenLastNameIsNull() {
            assertThatThrownBy(() -> usernameGenerator.generateUsername("John", null, name -> false))
                    .isInstanceOf(ValidationException.class)
                    .hasMessageContaining("must not be null");
        }

        @Test
        @DisplayName("Should throw ValidationException when both names are null")
        void shouldThrowExceptionWhenBothNamesAreNull() {
            assertThatThrownBy(() -> usernameGenerator.generateUsername(null, null, name -> false))
                    .isInstanceOf(ValidationException.class);
        }
    }
}
