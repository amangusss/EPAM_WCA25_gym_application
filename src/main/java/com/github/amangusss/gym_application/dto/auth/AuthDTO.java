package com.github.amangusss.gym_application.dto.auth;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

public class AuthDTO {

    private AuthDTO() {
        throw new UnsupportedOperationException("Utility class");
    }

    public static class Request {
        public record Login(
                @NotBlank String username,
                @NotBlank String password
        ) {}

        @Builder
        public record ChangePassword(
                @NotBlank String oldPassword,
                @NotBlank String newPassword
        ) {}
    }

    public static class Response {
        public record Login(
                String token,
                String username
        ) {}

        public record Logout(
                String message
        ) {}
    }
}
