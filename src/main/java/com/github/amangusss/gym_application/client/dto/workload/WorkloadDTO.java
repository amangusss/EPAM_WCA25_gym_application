package com.github.amangusss.gym_application.client.dto.workload;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.time.LocalDate;

public class WorkloadDTO {

    private WorkloadDTO() {
        throw new UnsupportedOperationException("Utility class");
    }

    public static class Request {
        @Builder
        public record Workload(
                @NotBlank String username,
                @NotBlank String firstName,
                @NotBlank String lastName,
                @NotBlank String status,
                @NotNull LocalDate trainingDate,
                @NotNull Double trainingDuration,
                @NotBlank String actionType
        ) {}
    }
}
