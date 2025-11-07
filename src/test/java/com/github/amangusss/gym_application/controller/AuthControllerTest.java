package com.github.amangusss.gym_application.controller;

import com.github.amangusss.gym_application.dto.auth.AuthDTO;
import com.github.amangusss.gym_application.exception.GlobalExceptionHandler;
import com.github.amangusss.gym_application.jwt.JwtUtils;
import com.github.amangusss.gym_application.service.AuthService;
import com.github.amangusss.gym_application.service.BruteForceProtectionService;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityFilterAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(
        controllers = AuthController.class,
        excludeAutoConfiguration = {
                SecurityAutoConfiguration.class,
                SecurityFilterAutoConfiguration.class,
                UserDetailsServiceAutoConfiguration.class
        }
)
@Import(GlobalExceptionHandler.class)
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
@DisplayName("AuthController Tests")
class AuthControllerTest {

    private static final String VALID_USERNAME = "Aman.Nazarkulov";
    private static final String VALID_PASSWORD = "password123";
    private static final String INVALID_PASSWORD = "wrongPassword";
    private static final String OLD_PASSWORD = "oldPassword";
    private static final String NEW_PASSWORD = "newPassword";
    private static final String LOGIN_ENDPOINT = "/api/auth/login";
    private static final String CHANGE_PASSWORD_ENDPOINT = "/api/auth/change-password";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private AuthService authService;

    @MockitoBean(name = "jwtUtils")
    private JwtUtils jwtUtils;

    @MockitoBean(name = "customUserDetailsService")
    private UserDetailsService userDetailsService;

    @MockitoBean(name = "bruteForceProtectionService")
    private BruteForceProtectionService bruteForceProtectionService;

    @BeforeEach
    void setUp() {
        reset(authService);
    }

    @Test
    @DisplayName("Should return 200 OK when login with valid credentials")
    void shouldReturnOkWhenLoginWithValidCredentials() throws Exception {
        AuthDTO.Response.Login loginResponse = new AuthDTO.Response.Login("mock-jwt-token", VALID_USERNAME);
        when(authService.login(any(AuthDTO.Request.Login.class))).thenReturn(loginResponse);

        mockMvc.perform(post(LOGIN_ENDPOINT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createLoginRequest())))
                .andExpect(status().isOk());

        verify(authService, times(1)).login(any(AuthDTO.Request.Login.class));
    }

    @Test
    @DisplayName("Should return 401 Unauthorized when login with invalid credentials")
    void shouldReturnUnauthorizedWhenLoginWithInvalidCredentials() throws Exception {
        when(authService.login(any(AuthDTO.Request.Login.class)))
                .thenThrow(new BadCredentialsException("Invalid credentials"));

        mockMvc.perform(post(LOGIN_ENDPOINT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createLoginRequest())))
                .andExpect(status().isUnauthorized());

        verify(authService, times(1)).login(any(AuthDTO.Request.Login.class));
    }

    @Test
    @DisplayName("Should return 200 OK when password changed successfully")
    void shouldReturnOkWhenPasswordChangedSuccessfully() throws Exception {
        AuthDTO.Request.ChangePassword changePasswordRequest =
                createChangePasswordRequest(OLD_PASSWORD);

        mockMvc.perform(put(CHANGE_PASSWORD_ENDPOINT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(changePasswordRequest)))
                .andExpect(status().isOk());

        verify(authService, times(1)).changePassword(any(AuthDTO.Request.ChangePassword.class));
    }

    @Test
    @DisplayName("Should return 401 Unauthorized when password change fails")
    void shouldReturnUnauthorizedWhenPasswordChangeFails() throws Exception {
        AuthDTO.Request.ChangePassword changePasswordRequest =
                createChangePasswordRequest(INVALID_PASSWORD);

        doThrow(new BadCredentialsException("Invalid old password"))
                .when(authService).changePassword(any(AuthDTO.Request.ChangePassword.class));

        mockMvc.perform(put(CHANGE_PASSWORD_ENDPOINT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(changePasswordRequest)))
                .andExpect(status().isUnauthorized());

        verify(authService, times(1)).changePassword(any(AuthDTO.Request.ChangePassword.class));
    }

    @Test
    @DisplayName("Should pass correct username and password to service on login")
    void shouldPassCorrectCredentialsToServiceOnLogin() throws Exception {
        ArgumentCaptor<AuthDTO.Request.Login> loginCaptor =
                ArgumentCaptor.forClass(AuthDTO.Request.Login.class);
        AuthDTO.Response.Login loginResponse = new AuthDTO.Response.Login("mock-jwt-token", VALID_USERNAME);
        when(authService.login(any(AuthDTO.Request.Login.class))).thenReturn(loginResponse);

        mockMvc.perform(post(LOGIN_ENDPOINT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createLoginRequest())))
                .andExpect(status().isOk());

        verify(authService, times(1)).login(loginCaptor.capture());
        AuthDTO.Request.Login capturedLogin = loginCaptor.getValue();

        assertEquals(VALID_USERNAME, capturedLogin.username(),
                "Username should match the request parameter");
        assertEquals(VALID_PASSWORD, capturedLogin.password(),
                "Password should match the request parameter");
    }

    private AuthDTO.Request.Login createLoginRequest() {
        return new AuthDTO.Request.Login(VALID_USERNAME, VALID_PASSWORD);
    }

    private AuthDTO.Request.ChangePassword createChangePasswordRequest(String oldPassword) {
        return new AuthDTO.Request.ChangePassword(VALID_USERNAME, oldPassword, NEW_PASSWORD);
    }
}
