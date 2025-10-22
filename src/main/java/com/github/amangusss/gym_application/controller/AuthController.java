package com.github.amangusss.gym_application.controller;

import com.github.amangusss.gym_application.dto.auth.AuthDTO;
import com.github.amangusss.gym_application.facade.AuthFacade;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "Authentication APIs")
public class AuthController {

    private final AuthFacade authFacade;

    @GetMapping("/login")
    @Operation(summary = "Login", description = "Authenticates user (trainee or trainer) with username and password")
    public ResponseEntity<Void> login(@Valid @ModelAttribute AuthDTO.Request.Login request) {

        String transactionId = UUID.randomUUID().toString();
        log.info("[Transaction: {}] GET /api/auth/login", transactionId);

        boolean authenticated = authFacade.login(request);

        if (!authenticated) {
            log.warn("[Transaction: {}] Response: 401 Unauthorized", transactionId);
            return ResponseEntity.status(401).build();
        }

        log.info("[Transaction: {}] Response: 200 OK", transactionId);
        return ResponseEntity.ok().build();
    }

    @PutMapping(value = "/change-password", consumes = "application/json")
    @Operation(summary = "Change password", description = "Changes user password (trainee or trainer)")
    public ResponseEntity<Void> changePassword(@Valid @RequestBody AuthDTO.Request.ChangePassword request) {

        String transactionId = UUID.randomUUID().toString();
        log.info("[Transaction: {}] PUT /api/auth/change-password", transactionId);

        boolean success = authFacade.changePassword(request);

        if (!success) {
            log.error("[Transaction: {}] Response: 401 Unauthorized", transactionId);
            return ResponseEntity.status(401).build();
        }

        log.info("[Transaction: {}] Response: 200 OK", transactionId);
        return ResponseEntity.ok().build();
    }
}
