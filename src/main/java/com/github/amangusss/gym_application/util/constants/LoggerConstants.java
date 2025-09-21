package com.github.amangusss.gym_application.util.constants;

public final class LoggerConstants {

    private LoggerConstants() {
        throw new UnsupportedOperationException("LoggerConstants class cannot be instantiated");
    }

    public static final String APP_STARTING = "Starting Gym application";
    public static final String APP_CONTEXT_INITIALIZED = "Application context initialized";
    public static final String APP_FINISHED = "Application finished";
    public static final String APP_FAILED = "Application failed";
    
    public static final String CONFIG_INITIALIZED = "Gym application configuration initialized";
    public static final String FACADE_INITIALIZED = "Gym facade initialized with all services";

    public static final String DEMO_STARTED = "Demo application started";
    public static final String DEMO_TRAINEE_STARTED = "Demo trainee operations started";
    public static final String DEMO_TRAINER_STARTED = "Demo trainer operations started";
    public static final String DEMO_TRAINING_STARTED = "Demo training operations started";
    
    public static final String TRAINEE_CREATING = "Creating trainee: {} {}";
    public static final String TRAINEE_CREATED = "Trainee created successfully with username: {}";
    public static final String TRAINEE_UPDATING = "Updating trainee: {} {}";
    public static final String TRAINEE_DELETING = "Deleting trainee: {} {}";
    
    public static final String TRAINER_CREATING = "Creating trainer: {} {}";
    public static final String TRAINER_CREATED = "Trainer created successfully with username: {}";
    public static final String TRAINER_UPDATING = "Updating trainer: {} {}";
    public static final String TRAINEE_CREATED_DETAILS = "Created trainee: {} (username: {}, password: {})";
    public static final String TRAINEE_UPDATED_ADDRESS = "Updated trainee address: {}";
    public static final String TRAINEES_FOUND = "Found {} trainees";
    
    public static final String TRAINER_CREATED_DETAILS = "Created trainer: {} (username: {}, password: {}, specialization: {})";
    public static final String TRAINERS_FOUND = "Found {} trainers";
    
    public static final String TRAINING_CREATING = "Creating training: {}";
    public static final String TRAINING_CREATED = "Training created successfully with id: {}";
    public static final String TRAINING_CREATED_DETAILS = "Created training: {} (duration: {} min, date: {})";
    public static final String TRAININGS_FOUND = "Found {} trainings";
    
    public static final String DEBUG_CREATING_TRAINEE = "Creating trainee with firstName: {}, lastName: {}, dateOfBirth: {}, address: {}";
    public static final String DEBUG_BUILT_TRAINEE = "Built trainee object: {}";
    public static final String DEBUG_TRAINEE_CREATED = "Successfully created trainee with ID: {}";
    
    public static final String DATA_INIT_STARTING = "Initializing data";
    public static final String DATA_INIT_COMPLETED = "Data initialization completed! Loaded: {} trainees, {} trainers, {} trainings";
    public static final String DATA_INIT_FAILED = "Failed to initialize data";
    public static final String LOADING_TRAINEES = "Loading trainees from file: {}";
    public static final String LOADING_TRAINERS = "Loading trainers from file: {}";
    public static final String LOADING_TRAININGS = "Loading trainings from file: {}";
    public static final String LOADED_ENTITIES = "Loaded {} {} entities";
    public static final String INVALID_LINE = "Invalid {} line: {}";
    public static final String FAILED_PARSE_LINE_TWO_ARGUMENTS = "Failed to parse {} line: {}";
    public static final String FAILED_PARSE_LINE_THREE_ARGUMENTS = "Failed to parse {} line {}: {}";
    public static final String FAILED_TO_LOAD_FROM_FILE = "Failed to load {} from file: {}" ;
    public static final String FILE_NOT_FOUND = "{} file not found: {}. Skipping initialization for {}";

    public static final String PASSWORD_GENERATED = "Generated password: {}";
    
    public static final String USERNAME_EXISTS = "Username {} already exists. Trying username {}";
    public static final String USERNAME_GENERATED = "Generated username: {}";
    
    public static final String ENTITY_SAVED = "Saved entity with id: {}";
    public static final String ENTITY_FOUND = "Find by id {}: {}";
    public static final String ENTITY_FIND_ALL = "Find all: {}";
    public static final String ENTITY_UPDATED = "Updated entity with id: {}";
    public static final String ENTITY_DELETED = "Deleted entity with id {} : {} ";

    public static final String DAO_CREATED = "{} created";
    public static final String DAO_SAVING = "Saving {}: {}";
    public static final String DAO_SAVED = "{} saved successfully with id: {}";
    public static final String DAO_UPDATING = "Updating {}: {}";
    public static final String DAO_UPDATED = "{} updated successfully with id: {}";
    public static final String DAO_DELETING = "Deleting {} with id: {}";
    public static final String DAO_DELETED = "{} with id {} deleted successfully";
    public static final String DAO_NOT_FOUND = "{} with id {} not found";
    public static final String DAO_FINDING = "Finding {} with id: {}";
    public static final String DAO_FOUND = "{} found successfully with id: {}";
    public static final String DAO_FINDING_ALL = "Finding all {}";
    public static final String DAO_FOUND_ALL = "Found {} {}";
    public static final String DAO_FINDING_BY_USERNAME = "Finding {} with username: {}";
    public static final String DAO_FOUND_BY_USERNAME = "{} found successfully with username: {}";
    public static final String DAO_NOT_FOUND_BY_USERNAME = "{} with username {} not found";
    public static final String DAO_FINDING_ACTIVE = "Finding all active {}";
    public static final String DAO_FOUND_ACTIVE = "Found {} active {}";
    public static final String DAO_EXISTS_BY_USERNAME = "{} with username {} exists: {}";
    public static final String DAO_FINDING_BY_SPECIALIZATION = "Finding {} by specialization: {}";
    public static final String DAO_FOUND_BY_SPECIALIZATION = "Found {} {} with specialization {}";
    public static final String DAO_FINDING_BY_TRAINER = "Finding {} by trainer id: {}";
    public static final String DAO_FOUND_BY_TRAINER = "Found {} {} for trainer {}";
    public static final String DAO_FINDING_BY_TRAINEE = "Finding {} by trainee id: {}";
    public static final String DAO_FOUND_BY_TRAINEE = "Found {} {} for trainee {}";
    public static final String DAO_FINDING_BY_TYPE = "Finding {} by type: {}";
    public static final String DAO_FOUND_BY_TYPE = "Found {} {} with type {}";
    public static final String DAO_FINDING_BY_DATE_RANGE = "Finding {} by date range: {} to {}";
    public static final String DAO_FOUND_BY_DATE_RANGE = "Found {} {} in date range {} to {}";
    
    public static final String DEMO_DELETE_STARTED = "Demo delete operations started";
    public static final String DEMO_TRAINEE_CREATED_FOR_DELETION = "Created trainee for deletion: {} {}";
    public static final String DEMO_TRAINEE_DELETED_SUCCESS = "Successfully deleted trainee with ID: {}";
    public static final String DEMO_TRAINEE_DELETE_FAILED = "Failed to delete trainee with ID: {}";
    public static final String DEMO_TRAINEE_CONFIRMED_DELETED = "Confirmed: trainee with ID {} no longer exists";
    public static final String DEMO_TRAINEE_STILL_EXISTS = "Warning: trainee with ID {} still exists after deletion";
    public static final String DEMO_FINAL_STATISTICS = "Final statistics - Trainees: {}, Trainers: {}, Trainings: {}";
    
    public static final String DEMO_FOUND_TRAINEE_BY_ID = "Found trainee by ID: {} {}";
    public static final String DEMO_FOUND_TRAINER_BY_ID = "Found trainer by ID: {} {} (specialization: {})";
    public static final String DEMO_FOUND_TRAINING_BY_ID = "Found training by ID: {} (duration: {} min, date: {})";
    public static final String DEMO_UPDATED_TRAINER_SPECIALIZATION = "Updated trainer specialization to: {}";
}
