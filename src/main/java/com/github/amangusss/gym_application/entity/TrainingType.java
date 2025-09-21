package com.github.amangusss.gym_application.entity;

import com.github.amangusss.gym_application.exception.ValidationException;
import com.github.amangusss.gym_application.util.constants.ConfigConstants;

public enum TrainingType {

    FITNESS(ConfigConstants.TRAINING_TYPE_FITNESS),
    YOGA(ConfigConstants.TRAINING_TYPE_YOGA),
    ZUMBA(ConfigConstants.TRAINING_TYPE_ZUMBA),
    STRETCHING(ConfigConstants.TRAINING_TYPE_STRETCHING),
    RESISTANCE(ConfigConstants.TRAINING_TYPE_RESISTANCE);

    private final String displayName;

    TrainingType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public static TrainingType fromDisplayName(String displayName) {
        for (TrainingType type : values()) {
            if (type.getDisplayName().equalsIgnoreCase(displayName)) {
                return type;
            }
        }
        throw new ValidationException(ConfigConstants.NO_TRAINING_TYPE + displayName);
    }

    @Override
    public String toString() {
        return displayName;
    }
}
