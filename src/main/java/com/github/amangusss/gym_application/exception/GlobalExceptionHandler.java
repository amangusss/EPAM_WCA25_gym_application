package com.github.amangusss.gym_application.exception;

import com.github.amangusss.gym_application.dto.error.ErrorResponse;

import lombok.extern.slf4j.Slf4j;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.UUID;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationError(MethodArgumentNotValidException ex) {
        String transactionId = UUID.randomUUID().toString();

        StringBuilder errorMessages = new StringBuilder();
        ex.getBindingResult().getFieldErrors().forEach(error -> {
            String fieldName = error.getField();
            String message = error.getDefaultMessage();
            errorMessages.append(fieldName).append(": ").append(message).append("; ");
        });

        String finalMessage = errorMessages.toString();
        if (finalMessage.endsWith("; ")) {
            finalMessage = finalMessage.substring(0, finalMessage.length() - 2);
        }

        log.error("[Transaction: {}] Validation error: {}", transactionId, finalMessage);

        ErrorResponse error = new ErrorResponse("Validation Error", finalMessage);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ErrorResponse> handleAuthenticationError(AuthenticationException ex) {
        String transactionId = UUID.randomUUID().toString();
        log.error("[Transaction: {}] Authentication error: {}", transactionId, ex.getMessage());

        ErrorResponse error = new ErrorResponse("Authentication Error", ex.getMessage());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
    }

    @ExceptionHandler(TraineeNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleTraineeNotFound(TraineeNotFoundException ex) {
        String transactionId = UUID.randomUUID().toString();
        log.error("[Transaction: {}] Trainee not found: {}", transactionId, ex.getMessage());

        ErrorResponse error = new ErrorResponse("Trainee Not Found", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    @ExceptionHandler(TrainerNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleTrainerNotFound(TrainerNotFoundException ex) {
        String transactionId = UUID.randomUUID().toString();
        log.error("[Transaction: {}] Trainer not found: {}", transactionId, ex.getMessage());

        ErrorResponse error = new ErrorResponse("Trainer Not Found", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    @ExceptionHandler(TrainingTypeNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleTrainingTypeNotFound(TrainingTypeNotFoundException ex) {
        String transactionId = UUID.randomUUID().toString();
        log.error("[Transaction: {}] Training type not found: {}", transactionId, ex.getMessage());

        ErrorResponse error = new ErrorResponse("Training Type Not Found", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(ValidationException ex) {
        String transactionId = UUID.randomUUID().toString();
        log.error("[Transaction: {}] Validation error: {}", transactionId, ex.getMessage());

        ErrorResponse error = new ErrorResponse("Validation Error", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgument(IllegalArgumentException ex) {
        String transactionId = UUID.randomUUID().toString();
        log.error("[Transaction: {}] Illegal argument: {}", transactionId, ex.getMessage());

        ErrorResponse error = new ErrorResponse("Bad Request", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ErrorResponse> handleBadCredentials(BadCredentialsException ex) {
        String transactionId = UUID.randomUUID().toString();
        log.error("[Transaction: {}] Bad credentials: {}", transactionId, ex.getMessage());

        ErrorResponse error = new ErrorResponse("Authentication Failed", ex.getMessage());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
    }
}
