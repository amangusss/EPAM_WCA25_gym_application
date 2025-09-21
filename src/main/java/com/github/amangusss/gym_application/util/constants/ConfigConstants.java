package com.github.amangusss.gym_application.util.constants;

public final class ConfigConstants {

    private ConfigConstants() {
        throw new UnsupportedOperationException("ConfigConstants class cannot be instantiated");
    }

    public static final String BEAN_GYM_FACADE = "gymFacade";
    public static final String BEAN_DATA_INITIALIZER = "dataInitializer";
    public static final String BEAN_PASSWORD_GENERATOR = "passwordGenerator";
    public static final String BEAN_USERNAME_GENERATOR = "usernameGenerator";
    public static final String BEAN_TRAINING_STORAGE = "trainingStorage";
    public static final String BEAN_TRAINER_STORAGE = "trainerStorage";
    public static final String BEAN_TRAINEE_STORAGE = "traineeStorage";
    
    public static final String PROP_TRAINEE_FILE = "data.trainee.file";
    public static final String PROP_TRAINER_FILE = "data.trainer.file";
    public static final String PROP_TRAINING_FILE = "data.training.file";
    
    public static final String PROP_PASSWORD_CHARACTERS = "password.characters";
    public static final String PROP_PASSWORD_LENGTH = "password.length";
    
    public static final String USERNAME_SEPARATOR = ".";

    public static final String TRAINING_TYPE_FITNESS = "fitness";
    public static final String TRAINING_TYPE_YOGA = "yoga";
    public static final String TRAINING_TYPE_ZUMBA = "zumba";
    public static final String TRAINING_TYPE_STRETCHING = "stretching";
    public static final String TRAINING_TYPE_RESISTANCE = "resistance";
    public static final String NO_TRAINING_TYPE = "No TrainingType with displayName ";

    public static final int SYSTEM_EXIT_ERROR = 1;
}
