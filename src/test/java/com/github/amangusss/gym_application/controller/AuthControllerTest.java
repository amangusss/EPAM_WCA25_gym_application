package com.github.amangusss.gym_application.controller;

import com.github.amangusss.gym_application.dto.auth.AuthDTO;
import com.github.amangusss.gym_application.service.AuthService;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthController.class)
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

    @BeforeEach
    void setUp() {
        reset(authService);
    }

    @Test
    @DisplayName("Should return 200 OK when login with valid credentials")
    void shouldReturnOkWhenLoginWithValidCredentials() throws Exception {
        AuthDTO.Request.Login expectedLoginRequest = createLoginRequest();
        when(authService.login(expectedLoginRequest)).thenReturn(true);

        mockMvc.perform(get(LOGIN_ENDPOINT)
                        .param("username", VALID_USERNAME)
                        .param("password", VALID_PASSWORD))
                .andExpect(status().isOk());

        verify(authService, times(1)).login(expectedLoginRequest);
    }

    @Test
    @DisplayName("Should return 401 Unauthorized when login with invalid credentials")
    void shouldReturnUnauthorizedWhenLoginWithInvalidCredentials() throws Exception {
        when(authService.login(any(AuthDTO.Request.Login.class))).thenReturn(false);

        mockMvc.perform(get(LOGIN_ENDPOINT)
                        .param("username", VALID_USERNAME)
                        .param("password", INVALID_PASSWORD))
                .andExpect(status().isUnauthorized());

        verify(authService, times(1)).login(any(AuthDTO.Request.Login.class));
    }

    @Test
    @DisplayName("Should return 200 OK when password changed successfully")
    void shouldReturnOkWhenPasswordChangedSuccessfully() throws Exception {
        AuthDTO.Request.ChangePassword changePasswordRequest =
                createChangePasswordRequest(OLD_PASSWORD);
        when(authService.changePassword(any(AuthDTO.Request.ChangePassword.class))).thenReturn(true);

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
        when(authService.changePassword(any(AuthDTO.Request.ChangePassword.class))).thenReturn(false);

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
        when(authService.login(any(AuthDTO.Request.Login.class))).thenReturn(true);

        mockMvc.perform(get(LOGIN_ENDPOINT)
                        .param("username", VALID_USERNAME)
                        .param("password", VALID_PASSWORD))
                .andExpect(status().isOk());

        verify(authService, times(1)).login(loginCaptor.capture());
        AuthDTO.Request.Login capturedLogin = loginCaptor.getValue();

        assertEquals(VALID_USERNAME, capturedLogin.username(),
                "Username should match the request parameter");
        assertEquals(VALID_PASSWORD, capturedLogin.password(),
                "Password should match the request parameter");
    }

    private AuthDTO.Request.Login createLoginRequest() {
        return new AuthDTO.Request.Login(AuthControllerTest.VALID_USERNAME, AuthControllerTest.VALID_PASSWORD);
    }

    private AuthDTO.Request.ChangePassword createChangePasswordRequest(String oldPassword) {
        return new AuthDTO.Request.ChangePassword(AuthControllerTest.VALID_USERNAME, oldPassword, AuthControllerTest.NEW_PASSWORD);
    }
}
