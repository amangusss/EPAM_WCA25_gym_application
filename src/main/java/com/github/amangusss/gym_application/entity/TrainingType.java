package com.github.amangusss.gym_application.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum TrainingType {

    FITNESS("fitness"),
    YOGA("yoga"),
    ZUMBA("zumba"),
    STRETCHING("stretching"),
    RESISTANCE("resistance");

    private final String displayName;

    @Override
    public String toString() {
        return displayName;
    }
}
