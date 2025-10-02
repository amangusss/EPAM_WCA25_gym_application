package com.github.amangusss.gym_application.entity.training;

import com.github.amangusss.gym_application.entity.TrainingType;

import java.time.LocalDate;

public class TrainingBuilder {

    private Long id;
    private Long trainerId;
    private Long traineeId;
    private String trainingName;
    private TrainingType trainingType;
    private LocalDate trainingDate;
    private Integer trainingDuration;

    public TrainingBuilder() {}

    public TrainingBuilder setId(Long id) {
        this.id = id;
        return this;
    }

    public TrainingBuilder trainerId(Long trainerId) {
        this.trainerId = trainerId;
        return this;
    }

    public TrainingBuilder traineeId(Long traineeId) {
        this.traineeId = traineeId;
        return this;
    }

    public TrainingBuilder trainingName(String trainingName) {
        this.trainingName = trainingName;
        return this;
    }

    public TrainingBuilder trainingType(TrainingType trainingType) {
        this.trainingType = trainingType;
        return this;
    }

    public TrainingBuilder trainingDate(LocalDate trainingDate) {
        this.trainingDate = trainingDate;
        return this;
    }

    public TrainingBuilder trainingDuration(Integer trainingDuration) {
        this.trainingDuration = trainingDuration;
        return this;
    }

    public Training build() {
        Training training = new Training();
        training.setId(id);
        training.setTrainerId(trainerId);
        training.setTraineeId(traineeId);
        training.setTrainingName(trainingName);
        training.setTrainingType(trainingType);
        training.setTrainingDate(trainingDate);
        training.setTrainingDuration(trainingDuration);
        return training;
    }

    public static TrainingBuilder builder() {
        return new TrainingBuilder();
    }
}
