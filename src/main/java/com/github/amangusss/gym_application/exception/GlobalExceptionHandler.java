package com.github.amangusss.gym_application.exception;

import lombok.extern.slf4j.Slf4j;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        String transactionId = UUID.randomUUID().toString();
        Map<String, String> errors = new HashMap<>();

        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });

        log.error("[Transaction: {}] Validation error: {}", transactionId, errors);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<Map<String, String>> handleAuthenticationException(AuthenticationException ex) {
        String transactionId = UUID.randomUUID().toString();
        Map<String, String> error = new HashMap<>();
        error.put("error", ex.getMessage());

        log.error("[Transaction: {}] Authentication error: {}", transactionId, ex.getMessage());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
    }

    @ExceptionHandler(TraineeNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleTraineeNotFoundException(TraineeNotFoundException ex) {
        String transactionId = UUID.randomUUID().toString();
        Map<String, String> error = new HashMap<>();
        error.put("error", ex.getMessage());

        log.error("[Transaction: {}] Trainee not found: {}", transactionId, ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    @ExceptionHandler(TrainerNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleTrainerNotFoundException(TrainerNotFoundException ex) {
        String transactionId = UUID.randomUUID().toString();
        Map<String, String> error = new HashMap<>();
        error.put("error", ex.getMessage());

        log.error("[Transaction: {}] Trainer not found: {}", transactionId, ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<Map<String, String>> handleValidationException(ValidationException ex) {
        String transactionId = UUID.randomUUID().toString();
        Map<String, String> error = new HashMap<>();
        error.put("error", ex.getMessage());

        log.error("[Transaction: {}] Validation error: {}", transactionId, ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, String>> handleIllegalArgumentException(IllegalArgumentException ex) {
        String transactionId = UUID.randomUUID().toString();
        Map<String, String> error = new HashMap<>();
        error.put("error", ex.getMessage());

        log.error("[Transaction: {}] Illegal argument: {}", transactionId, ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> handleGenericException(Exception ex) {
        String transactionId = UUID.randomUUID().toString();
        Map<String, String> error = new HashMap<>();
        error.put("error", "Internal server error");
        error.put("details", ex.getMessage());

        log.error("[Transaction: {}] Unexpected error: {}", transactionId, ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }
}
