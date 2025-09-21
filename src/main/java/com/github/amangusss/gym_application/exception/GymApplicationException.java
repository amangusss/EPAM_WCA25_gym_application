package com.github.amangusss.gym_application.exception;

public abstract class GymApplicationException extends RuntimeException {

    public GymApplicationException(String message) {
        super(message);
    }

    public GymApplicationException(String message, Throwable cause) {
        super(message, cause);
    }
}
