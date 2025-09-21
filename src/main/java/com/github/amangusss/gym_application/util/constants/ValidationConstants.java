package com.github.amangusss.gym_application.util.constants;

public final class ValidationConstants {

    private ValidationConstants() {
        throw new UnsupportedOperationException("ValidationConstants class cannot be instantiated");
    }

    public static final String TRAINEE_NULL = "Trainee cannot be null";
    public static final String TRAINEE_FIRST_NAME_NULL = "Trainee first name cannot be null or empty";
    public static final String TRAINEE_LAST_NAME_NULL = "Trainee last name cannot be null or empty";
    public static final String TRAINEE_ID_NULL = "Trainee id cannot be null";

    public static final String TRAINER_NULL = "Trainer cannot be null";
    public static final String TRAINER_FIRST_NAME_NULL = "Trainer first name cannot be null or empty";
    public static final String TRAINER_LAST_NAME_NULL = "Trainer last name cannot be null or empty";
    public static final String TRAINER_ID_NULL = "Trainer id cannot be null";
    public static final String TRAINER_SPECIALIZATION_NULL = "Trainer specialization cannot be null";

    public static final String TRAINING_NULL = "Training cannot be null";
    public static final String TRAINING_NAME_NULL = "Training name cannot be null or empty";
    public static final String TRAINING_TYPE_NULL = "Training type cannot be null";
    public static final String TRAINING_DATE_NULL = "Training date cannot be null";
    public static final String TRAINING_DURATION_INVALID = "Training duration must be positive";
    public static final String TRAINER_ID_REQUIRED = "Trainer id is required";
    public static final String TRAINEE_ID_REQUIRED = "Trainee id is required";
    public static final String TRAINEE_NOT_ACTIVE = "Trainee with id %s is not active";
    public static final String TRAINER_NOT_ACTIVE = "Trainer with id %s is not active";
    
    public static final String FIRST_NAME_NULL = "First name must not be null";
    public static final String LAST_NAME_NULL = "Last name must not be null";

    public static final String ENTITY_MUST_NOT_BE_NULL = "Entity must not be null";
    public static final String ENTITY_NOT_FOUND_BY_ID = "Entity with id %s not found";
    public static final String START_DATE_AFTER_END_DATE = "startDate cannot be after endDate";
    
    public static final String TRAINEE_NOT_FOUND_BY_ID = "Trainee not found with id: %s";
    public static final String TRAINER_NOT_FOUND_BY_ID = "Trainer not found with id: %s";
    public static final String TRAINING_NOT_FOUND_BY_ID = "Training not found with id: %s";

    public static final String DATA_INITIALIZATION_FAILED = "Failed to initialize data from file: %s";
}
