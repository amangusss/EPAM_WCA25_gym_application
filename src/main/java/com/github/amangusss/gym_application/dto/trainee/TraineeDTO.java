package com.github.amangusss.gym_application.dto.trainee;

import com.github.amangusss.gym_application.dto.trainer.TrainerDTO;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.List;

public class TraineeDTO {

    private TraineeDTO() {
        throw new UnsupportedOperationException("Utility class");
    }

    public static class Request {
        public record Register(
                @NotBlank String firstName,
                @NotBlank String lastName,
                LocalDate dateOfBirth,
                String address
        ) {}

        public record Update(
                @NotBlank String firstName,
                @NotBlank String lastName,
                LocalDate dateOfBirth,
                String address,
                @NotNull Boolean isActive
        ) {}

        public record UpdateStatus(
                @NotBlank String username,
                @NotNull Boolean isActive
        ) {}

        public record UpdateTrainers(
                @NotBlank String traineeUsername,
                @NotNull List<String> trainerUsernames
        ) {}
    }

    public static class Response {
        public record Registered(
                String username,
                String password
        ) {}

        public record Profile(
                String firstName,
                String lastName,
                LocalDate dateOfBirth,
                String address,
                Boolean isActive,
                List<TrainerDTO.Response.InList> trainers
        ) {}

        public record Updated(
                String username,
                String firstName,
                String lastName,
                LocalDate dateOfBirth,
                String address,
                Boolean isActive,
                List<TrainerDTO.Response.InList> trainers
        ) {}

        public record InList(
                String username,
                String firstName,
                String lastName
        ) {}
    }
}
