package com.github.amangusss.gym_application.exception;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ExceptionTest {

    @Test
    void gymApplicationException_WithMessage_ShouldCreateException() {
        String message = "Test error message";

        ValidationException exception = new ValidationException(message);

        assertEquals(message, exception.getMessage());
        assertNull(exception.getCause());
        assertInstanceOf(GymApplicationException.class, exception);
    }

    @Test
    void gymApplicationException_WithMessageAndCause_ShouldCreateException() {
        String message = "Test error message";
        Throwable cause = new RuntimeException("Root cause");

        DataInitializationException exception = new DataInitializationException(message, cause);

        assertEquals(message, exception.getMessage());
        assertEquals(cause, exception.getCause());
        assertInstanceOf(GymApplicationException.class, exception);
    }

    @Test
    void validationException_WithMessage_ShouldCreateException() {
        String message = "Validation error";

        ValidationException exception = new ValidationException(message);

        assertEquals(message, exception.getMessage());
        assertInstanceOf(GymApplicationException.class, exception);
    }

    @Test
    void traineeNotFoundException_WithMessage_ShouldCreateException() {
        String message = "Trainee not found";

        TraineeNotFoundException exception = new TraineeNotFoundException(message);

        assertEquals(message, exception.getMessage());
        assertInstanceOf(GymApplicationException.class, exception);
    }

    @Test
    void trainerNotFoundException_WithMessage_ShouldCreateException() {
        String message = "Trainer not found";

        TrainerNotFoundException exception = new TrainerNotFoundException(message);

        assertEquals(message, exception.getMessage());
        assertInstanceOf(GymApplicationException.class, exception);
    }

    @Test
    void trainingNotFoundException_WithMessage_ShouldCreateException() {
        String message = "Training not found";

        TrainingNotFoundException exception = new TrainingNotFoundException(message);

        assertEquals(message, exception.getMessage());
        assertInstanceOf(GymApplicationException.class, exception);
    }

    @Test
    void dataInitializationException_WithMessageAndCause_ShouldCreateException() {
        String message = "Data initialization failed";
        Throwable cause = new RuntimeException("File not found");

        DataInitializationException exception = new DataInitializationException(message, cause);

        assertEquals(message, exception.getMessage());
        assertEquals(cause, exception.getCause());
        assertInstanceOf(GymApplicationException.class, exception);
    }

    @Test
    void exceptionInheritance_ShouldBeCorrect() {
        assertEquals(ValidationException.class.getSuperclass(), GymApplicationException.class);
        assertEquals(TraineeNotFoundException.class.getSuperclass(), GymApplicationException.class);
        assertEquals(TrainerNotFoundException.class.getSuperclass(), GymApplicationException.class);
        assertEquals(TrainingNotFoundException.class.getSuperclass(), GymApplicationException.class);
        assertEquals(DataInitializationException.class.getSuperclass(), GymApplicationException.class);
    }
}