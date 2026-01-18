package com.github.amangusss.gym_application.service;

import com.github.amangusss.gym_application.dto.auth.AuthDTO;
import com.github.amangusss.gym_application.entity.CustomUser;
import com.github.amangusss.gym_application.jwt.JwtUtils;
import com.github.amangusss.gym_application.repository.UserRepository;
import com.github.amangusss.gym_application.service.impl.AuthServiceImpl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Collections;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@DisplayName("AuthService Tests")
class AuthServiceTest {

    private static final String USERNAME = "John.Doe";
    private static final String PASSWORD = "password123";
    private static final String ENCODED_PASSWORD = "encodedPassword123";
    private static final String OLD_PASSWORD = "oldPassword";
    private static final String NEW_PASSWORD = "newPassword";
    private static final String JWT_TOKEN = "jwt.token.here";

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private UserDetailsService userDetailsService;

    @Mock
    private JwtUtils jwtUtils;

    @Mock
    private BruteForceProtectionService bruteForceProtectionService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthServiceImpl authService;

    private CustomUser testUser;
    private UserDetails testUserDetails;

    @BeforeEach
    void setUp() {
        testUser = CustomUser.builder()
                .id(1L)
                .username(USERNAME)
                .password(ENCODED_PASSWORD)
                .firstName("John")
                .lastName("Doe")
                .isActive(true)
                .build();

        testUserDetails = User.builder()
                .username(USERNAME)
                .password(ENCODED_PASSWORD)
                .authorities(Collections.emptyList())
                .build();
    }

    @Nested
    @DisplayName("Login Tests")
    class LoginTests {

        @Test
        @DisplayName("Should login successfully with valid credentials")
        void shouldLoginSuccessfully() {
            AuthDTO.Request.Login request = new AuthDTO.Request.Login(USERNAME, PASSWORD);
            when(bruteForceProtectionService.isBlocked(USERNAME)).thenReturn(false);
            when(userDetailsService.loadUserByUsername(USERNAME)).thenReturn(testUserDetails);
            when(jwtUtils.generateToken(testUserDetails)).thenReturn(JWT_TOKEN);
            doNothing().when(bruteForceProtectionService).registerSuccessfulLogin(USERNAME);

            AuthDTO.Response.Login response = authService.login(request);

            assertThat(response).isNotNull();
            assertThat(response.token()).isEqualTo(JWT_TOKEN);
            assertThat(response.username()).isEqualTo(USERNAME);

            verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
            verify(bruteForceProtectionService).registerSuccessfulLogin(USERNAME);
        }

        @Test
        @DisplayName("Should throw LockedException when account is blocked")
        void shouldThrowLockedExceptionWhenBlocked() {
            AuthDTO.Request.Login request = new AuthDTO.Request.Login(USERNAME, PASSWORD);
            when(bruteForceProtectionService.isBlocked(USERNAME)).thenReturn(true);

            assertThatThrownBy(() -> authService.login(request))
                    .isInstanceOf(LockedException.class)
                    .hasMessageContaining("temporarily locked");

            verify(authenticationManager, never()).authenticate(any());
        }

        @Test
        @DisplayName("Should throw BadCredentialsException when authentication fails")
        void shouldThrowBadCredentialsWhenAuthFails() {
            AuthDTO.Request.Login request = new AuthDTO.Request.Login(USERNAME, "wrongPassword");
            when(bruteForceProtectionService.isBlocked(USERNAME)).thenReturn(false);
            doThrow(new BadCredentialsException("Bad credentials"))
                    .when(authenticationManager).authenticate(any());
            when(bruteForceProtectionService.getRemainingAttempts(USERNAME)).thenReturn(2);

            assertThatThrownBy(() -> authService.login(request))
                    .isInstanceOf(BadCredentialsException.class)
                    .hasMessageContaining("Invalid username or password");

            verify(bruteForceProtectionService).registerFailedLogin(USERNAME);
        }
    }

    @Nested
    @DisplayName("Change Password Tests")
    class ChangePasswordTests {

        @Test
        @DisplayName("Should change password successfully")
        void shouldChangePasswordSuccessfully() {
            AuthDTO.Request.ChangePassword request = new AuthDTO.Request.ChangePassword(OLD_PASSWORD, NEW_PASSWORD);
            when(userRepository.findByUsername(USERNAME)).thenReturn(Optional.of(testUser));
            when(passwordEncoder.matches(OLD_PASSWORD, testUser.getPassword())).thenReturn(true);
            when(passwordEncoder.encode(NEW_PASSWORD)).thenReturn("encodedNewPassword");
            when(userRepository.save(any(CustomUser.class))).thenReturn(testUser);

            authService.changePassword(USERNAME, request);

            verify(userRepository).save(testUser);
            verify(passwordEncoder).encode(NEW_PASSWORD);
        }

        @Test
        @DisplayName("Should throw BadCredentialsException when user not found")
        void shouldThrowBadCredentialsWhenUserNotFound() {
            AuthDTO.Request.ChangePassword request = new AuthDTO.Request.ChangePassword(OLD_PASSWORD, NEW_PASSWORD);
            when(userRepository.findByUsername(USERNAME)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> authService.changePassword(USERNAME, request))
                    .isInstanceOf(BadCredentialsException.class)
                    .hasMessageContaining("User not found");
        }

        @Test
        @DisplayName("Should throw BadCredentialsException when old password is invalid")
        void shouldThrowBadCredentialsWhenOldPasswordInvalid() {
            AuthDTO.Request.ChangePassword request = new AuthDTO.Request.ChangePassword("wrongOldPassword", NEW_PASSWORD);
            when(userRepository.findByUsername(USERNAME)).thenReturn(Optional.of(testUser));
            when(passwordEncoder.matches("wrongOldPassword", testUser.getPassword())).thenReturn(false);

            assertThatThrownBy(() -> authService.changePassword(USERNAME, request))
                    .isInstanceOf(BadCredentialsException.class)
                    .hasMessageContaining("Invalid old password");

            verify(userRepository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("Logout Tests")
    class LogoutTests {

        @Test
        @DisplayName("Should logout successfully")
        void shouldLogoutSuccessfully() {
            authService.logout(USERNAME);
        }
    }
}
