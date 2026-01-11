package com.github.amangusss.gym_application.dto.workload;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.io.Serializable;
import java.time.LocalDate;

public class WorkloadDTO {

    private WorkloadDTO() {
        throw new UnsupportedOperationException("Utility class");
    }

    public enum ActionType {
        ADD,
        DELETE
    }

    public static class Request {
        @Builder
        public record Workload(
                @NotBlank String username,
                @NotBlank String firstName,
                @NotBlank String lastName,
                @NotNull Boolean isActive,
                @NotNull LocalDate trainingDate,
                @NotNull Double trainingDuration,
                @NotNull ActionType actionType
        ) implements Serializable {}
    }
}
