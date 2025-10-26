package com.github.amangusss.gym_application.dto.auth;

import jakarta.validation.constraints.NotBlank;

public class AuthDTO {

    private AuthDTO() {
        throw new UnsupportedOperationException("Utility class");
    }

    public static class Request {
        public record Login(
                @NotBlank String username,
                @NotBlank String password
        ) {}

        public record ChangePassword(
                @NotBlank String username,
                @NotBlank String oldPassword,
                @NotBlank String newPassword
        ) {}
    }
}
