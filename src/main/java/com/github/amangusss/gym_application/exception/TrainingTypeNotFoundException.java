package com.github.amangusss.gym_application.exception;

public class TrainingTypeNotFoundException extends GymApplicationException {

    public TrainingTypeNotFoundException(Long id) {
        super("Training type not found with id: " + id);
    }
}
