package com.github.amangusss.gym_application.dto.trainer;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public class UpdateTrainersDTO {

    public static class Request {
        public record Update(
                @NotBlank String traineeUsername,
                @NotEmpty List<String> trainerUsernames
        ) {}
    }
}
