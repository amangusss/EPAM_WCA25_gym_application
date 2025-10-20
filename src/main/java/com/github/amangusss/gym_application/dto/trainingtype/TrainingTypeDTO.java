package com.github.amangusss.gym_application.dto.trainingtype;

public class TrainingTypeDTO {

    public static class Response {
        public record TrainingType(
                Long id,
                String typeName
        ) {}
    }
}
