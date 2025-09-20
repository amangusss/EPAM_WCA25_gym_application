package com.github.amangusss.gym_application.entity;

public enum TrainingType {

    FITNESS("fitness"),
    YOGA("yoga"),
    ZUMBA("zumba"),
    STRETCHING("stretching"),
    RESISTANCE("resistance");

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
        throw new IllegalArgumentException("No TrainingType with displayName " + displayName);
    }

    @Override
    public String toString() {
        return displayName;
    }
}
