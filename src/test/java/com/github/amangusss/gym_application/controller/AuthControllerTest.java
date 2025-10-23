package com.github.amangusss.gym_application.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.amangusss.gym_application.dto.auth.AuthDTO;
import com.github.amangusss.gym_application.facade.AuthFacade;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthController.class)
@DisplayName("AuthController Tests")
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AuthFacade authFacade;

    @Test
    @DisplayName("Should login successfully with valid credentials")
    void login_WithValidCredentials_ShouldReturnOk() throws Exception {
        when(authFacade.login(any())).thenReturn(true);

        mockMvc.perform(get("/api/auth/login")
                        .param("username", "Aman.Nazarkulov")
                        .param("password", "password123"))
                .andExpect(status().isOk());

        verify(authFacade).login(any());
    }

    @Test
    @DisplayName("Should return 401 with invalid credentials")
    void login_WithInvalidCredentials_ShouldReturnUnauthorized() throws Exception {
        when(authFacade.login(any())).thenReturn(false);

        mockMvc.perform(get("/api/auth/login")
                        .param("username", "Aman.Nazarkulov")
                        .param("password", "wrongPassword"))
                .andExpect(status().isUnauthorized());

        verify(authFacade).login(any());
    }

    @Test
    @DisplayName("Should change password successfully")
    void changePassword_WithValidData_ShouldReturnOk() throws Exception {
        AuthDTO.Request.ChangePassword request = new AuthDTO.Request.ChangePassword(
                "Aman.Nazarkulov", "oldPassword", "newPassword"
        );

        when(authFacade.changePassword(any())).thenReturn(true);

        mockMvc.perform(put("/api/auth/change-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        verify(authFacade).changePassword(any());
    }

    @Test
    @DisplayName("Should return 401 when password change fails")
    void changePassword_WithInvalidData_ShouldReturnUnauthorized() throws Exception {
        AuthDTO.Request.ChangePassword request = new AuthDTO.Request.ChangePassword(
                "Aman.Nazarkulov", "wrongOldPassword", "newPassword"
        );

        when(authFacade.changePassword(any())).thenReturn(false);

        mockMvc.perform(put("/api/auth/change-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());

        verify(authFacade).changePassword(any());
    }
}
