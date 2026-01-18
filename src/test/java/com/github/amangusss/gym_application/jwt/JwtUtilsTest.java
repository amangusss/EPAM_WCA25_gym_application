package com.github.amangusss.gym_application.jwt;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Collections;
import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("JwtUtils Tests")
class JwtUtilsTest {

    private static final String SECRET = "verysecretkeythatisatleast256bitslong1234567890abcdefghij";
    private static final long EXPIRATION_MS = 3600000; // 1 hour
    private static final String USERNAME = "John.Doe";

    private JwtUtils jwtUtils;
    private UserDetails userDetails;

    @BeforeEach
    void setUp() {
        jwtUtils = new JwtUtils(SECRET);
        ReflectionTestUtils.setField(jwtUtils, "expirationMillis", EXPIRATION_MS);

        userDetails = User.builder()
                .username(USERNAME)
                .password("password")
                .authorities(Collections.emptyList())
                .build();
    }

    @Nested
    @DisplayName("Token Generation Tests")
    class TokenGenerationTests {

        @Test
        @DisplayName("Should generate valid token")
        void shouldGenerateValidToken() {
            String token = jwtUtils.generateToken(userDetails);

            assertThat(token).isNotNull();
            assertThat(token).isNotEmpty();
            assertThat(token.split("\\.")).hasSize(3);
        }

        @Test
        @DisplayName("Should generate different tokens for different users")
        void shouldGenerateDifferentTokensForDifferentUsers() {
            UserDetails otherUser = User.builder()
                    .username("Jane.Doe")
                    .password("password")
                    .authorities(Collections.emptyList())
                    .build();

            String token1 = jwtUtils.generateToken(userDetails);
            String token2 = jwtUtils.generateToken(otherUser);

            assertThat(token1).isNotEqualTo(token2);
        }
    }

    @Nested
    @DisplayName("Token Extraction Tests")
    class TokenExtractionTests {

        @Test
        @DisplayName("Should extract username from token")
        void shouldExtractUsernameFromToken() {
            String token = jwtUtils.generateToken(userDetails);

            String extractedUsername = jwtUtils.extractUsername(token);

            assertThat(extractedUsername).isEqualTo(USERNAME);
        }

        @Test
        @DisplayName("Should extract expiration from token")
        void shouldExtractExpirationFromToken() {
            String token = jwtUtils.generateToken(userDetails);

            Date expiration = jwtUtils.extractExpiration(token);

            assertThat(expiration).isNotNull();
            assertThat(expiration).isAfter(new Date());
        }
    }

    @Nested
    @DisplayName("Token Validation Tests")
    class TokenValidationTests {

        @Test
        @DisplayName("Should validate token successfully")
        void shouldValidateTokenSuccessfully() {
            String token = jwtUtils.generateToken(userDetails);

            Boolean isValid = jwtUtils.validateToken(token, userDetails);

            assertThat(isValid).isTrue();
        }

        @Test
        @DisplayName("Should return false for token with wrong username")
        void shouldReturnFalseForWrongUsername() {
            String token = jwtUtils.generateToken(userDetails);
            UserDetails otherUser = User.builder()
                    .username("Jane.Doe")
                    .password("password")
                    .authorities(Collections.emptyList())
                    .build();

            Boolean isValid = jwtUtils.validateToken(token, otherUser);

            assertThat(isValid).isFalse();
        }

        @Test
        @DisplayName("Should return false for malformed token")
        void shouldReturnFalseForMalformedToken() {
            Boolean isValid = jwtUtils.validateToken("invalid.token.here", userDetails);

            assertThat(isValid).isFalse();
        }

        @Test
        @DisplayName("Should return false for token with invalid signature")
        void shouldReturnFalseForInvalidSignature() {
            String token = jwtUtils.generateToken(userDetails);
            String tamperedToken = token.substring(0, token.lastIndexOf('.') + 1) + "invalidsignature";

            Boolean isValid = jwtUtils.validateToken(tamperedToken, userDetails);

            assertThat(isValid).isFalse();
        }
    }

    @Nested
    @DisplayName("Constructor Tests")
    class ConstructorTests {

        @Test
        @DisplayName("Should throw exception for short secret key")
        void shouldThrowExceptionForShortSecretKey() {
            assertThatThrownBy(() -> new JwtUtils("shortkey"))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("256 bits");
        }
    }
}
