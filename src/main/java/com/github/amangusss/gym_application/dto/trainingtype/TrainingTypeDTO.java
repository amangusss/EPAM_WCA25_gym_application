package com.github.amangusss.gym_application.dto.trainingtype;

public class TrainingTypeDTO {

    private TrainingTypeDTO() {
        throw new UnsupportedOperationException("Utility class");
    }

    public static class Response {
        public record TrainingType(
                Long id,
                String typeName
        ) {}
    }
}
