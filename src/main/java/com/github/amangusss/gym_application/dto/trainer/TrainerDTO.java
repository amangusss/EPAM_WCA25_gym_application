package com.github.amangusss.gym_application.dto.trainer;

import com.github.amangusss.gym_application.dto.trainee.TraineeDTO;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public class TrainerDTO {

    private TrainerDTO() {
        throw new UnsupportedOperationException("Utility class");
    }

    public static class Request {
        public record Register(
                @NotBlank String firstName,
                @NotBlank String lastName,
                @NotNull Long specialization
        ) {}

        public record Update(
                @NotBlank String firstName,
                @NotBlank String lastName,
                @NotNull Long specialization,
                @NotNull Boolean isActive
        ) {}

        public record UpdateStatus(
                @NotNull Boolean isActive
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
                String specializationName,
                Boolean isActive,
                List<TraineeDTO.Response.InList> trainees
        ) {}

        public record Updated(
                String username,
                String firstName,
                String lastName,
                String specializationName,
                Boolean isActive,
                List<TraineeDTO.Response.InList> trainees
        ) {}

        public record InList(
                String username,
                String firstName,
                String lastName,
                String specializationName
        ) {}

        public record Unassigned(
                String username,
                String firstName,
                String lastName,
                String specializationName
        ) {}
    }
}
