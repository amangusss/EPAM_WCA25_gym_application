package com.github.amangusss.gym_application.dto.training;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.time.LocalDate;

public class TrainingDTO {

    private TrainingDTO() {
        throw new UnsupportedOperationException("Utility class");
    }

    public static class Request {
        public record Create(
                @NotBlank String traineeUsername,
                @NotBlank String trainerUsername,
                @NotBlank String trainingName,
                @NotNull LocalDate trainingDate,
                @NotNull @Positive Integer trainingDuration
        ) {}

        public record TrainerTrainingsFilter(
                LocalDate periodFrom,
                LocalDate periodTo,
                String traineeName
        ) {}

        public record TraineeTrainingsFilter(
                LocalDate periodFrom,
                LocalDate periodTo,
                String trainerName,
                String trainingType
        ) {}
    }

    public static class Response {
        public record TraineeTraining(
                String trainingName,
                LocalDate trainingDate,
                String trainingType,
                Integer trainingDuration,
                String trainerName
        ) {}

        public record TrainerTraining(
                String trainingName,
                LocalDate trainingDate,
                String trainingType,
                Integer trainingDuration,
                String traineeName
        ) {}
    }
}
