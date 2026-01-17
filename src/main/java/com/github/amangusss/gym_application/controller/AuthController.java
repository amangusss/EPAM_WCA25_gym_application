package com.github.amangusss.gym_application.controller;

import com.github.amangusss.dto.generated.LoginRequest;
import com.github.amangusss.dto.generated.LoginResponse;
import com.github.amangusss.dto.generated.ChangePasswordRequest;
import com.github.amangusss.gym_application.dto.auth.AuthDTO;
import com.github.amangusss.gym_application.service.AuthService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

import jakarta.validation.Valid;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Tag(name = "Authentication", description = "Authentication APIs")
public class AuthController {

    AuthService authService;

    @PostMapping("/login")
    @Operation(summary = "Login", description = "Authenticates user (trainee or trainer) with username and password. Returns JWT token.")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {

        String transactionId = UUID.randomUUID().toString();
        log.info("[Transaction: {}] POST /api/auth/login - user: {}", transactionId, request.getUsername());

        AuthDTO.Request.Login internalRequest = new AuthDTO.Request.Login(
                request.getUsername(),
                request.getPassword()
        );

        AuthDTO.Response.Login internalResponse = authService.login(internalRequest);

        LoginResponse response = LoginResponse.builder()
                .token(internalResponse.token())
                .build();

        log.info("[Transaction: {}] Response: 200 OK - token generated", transactionId);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/change-password")
    @SecurityRequirement(name = "Bearer Authentication")
    @Operation(summary = "Change password", description = "Changes user password (trainee or trainer). Requires authentication.")
    public ResponseEntity<Void> changePassword(
            @AuthenticationPrincipal UserDetails user,
            @Valid @RequestBody ChangePasswordRequest request) {

        String transactionId = UUID.randomUUID().toString();
        String username = user.getUsername();
        log.info("[Transaction: {}] PUT /api/auth/change-password - user: {}", transactionId, username);

        AuthDTO.Request.ChangePassword internalRequest = new AuthDTO.Request.ChangePassword(
                request.getOldPassword(),
                request.getNewPassword()
        );

        authService.changePassword(username, internalRequest);

        log.info("[Transaction: {}] Response: 200 OK", transactionId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/logout")
    @SecurityRequirement(name = "Bearer Authentication")
    @Operation(summary = "Logout", description = "Logs out the authenticated user")
    public ResponseEntity<AuthDTO.Response.Logout> logout(Authentication authentication) {

        String transactionId = UUID.randomUUID().toString();
        String username = authentication.getName();
        log.info("[Transaction: {}] POST /api/auth/logout - user: {}", transactionId, username);

        authService.logout(username);

        log.info("[Transaction: {}] Response: 200 OK", transactionId);
        return ResponseEntity.ok(new AuthDTO.Response.Logout("Logged out successfully"));
    }
}
