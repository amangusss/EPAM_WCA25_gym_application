package com.github.amangusss.gym_application.entity.trainer;

import com.github.amangusss.gym_application.entity.TrainingType;
import com.github.amangusss.gym_application.entity.trainee.Trainee;

import java.util.HashSet;
import java.util.Set;

public class TrainerBuilder {
    
    private String firstName;
    private String lastName;
    private TrainingType specialization;
    private Set<Trainee> traineeId;
    private boolean isActive = true;
    private String username;
    private String password;
    
    public TrainerBuilder() {
        this.traineeId = new HashSet<>();
    }
    
    public TrainerBuilder firstName(String firstName) {
        this.firstName = firstName;
        return this;
    }
    
    public TrainerBuilder lastName(String lastName) {
        this.lastName = lastName;
        return this;
    }
    
    public TrainerBuilder specialization(TrainingType specialization) {
        this.specialization = specialization;
        return this;
    }
    
    public TrainerBuilder traineeId(Set<Trainee> traineeId) {
        this.traineeId = traineeId != null ? new HashSet<>(traineeId) : new HashSet<>();
        return this;
    }
    
    public TrainerBuilder addTrainee(Trainee trainee) {
        if (this.traineeId == null) {
            this.traineeId = new HashSet<>();
        }
        this.traineeId.add(trainee);
        return this;
    }
    
    public TrainerBuilder isActive(boolean isActive) {
        this.isActive = isActive;
        return this;
    }
    
    public TrainerBuilder username(String username) {
        this.username = username;
        return this;
    }
    
    public TrainerBuilder password(String password) {
        this.password = password;
        return this;
    }
    
    public Trainer build() {
        Trainer trainer = new Trainer();
        trainer.setFirstName(firstName);
        trainer.setLastName(lastName);
        trainer.setSpecialization(specialization);
        trainer.setTraineeId(traineeId);
        trainer.setActive(isActive);
        trainer.setUsername(username);
        trainer.setPassword(password);
        return trainer;
    }
    
    public static TrainerBuilder builder() {
        return new TrainerBuilder();
    }
}
