package com.github.amangusss.gym_application.entity.trainee;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

public class TraineeBuilder {
    
    private String firstName;
    private String lastName;
    private LocalDate dateOfBirth;
    private String address;
    private Set<Long> trainerIds;
    private boolean isActive = true;
    private String username;
    private String password;
    
    public TraineeBuilder() {
        this.trainerIds = new HashSet<>();
    }
    
    public TraineeBuilder firstName(String firstName) {
        this.firstName = firstName;
        return this;
    }
    
    public TraineeBuilder lastName(String lastName) {
        this.lastName = lastName;
        return this;
    }
    
    public TraineeBuilder dateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
        return this;
    }
    
    public TraineeBuilder address(String address) {
        this.address = address;
        return this;
    }
    
    public TraineeBuilder trainerIds(Set<Long> trainerIds) {
        this.trainerIds = trainerIds != null ? new HashSet<>(trainerIds) : new HashSet<>();
        return this;
    }
    
    public TraineeBuilder addTrainerId(Long trainerId) {
        if (this.trainerIds == null) {
            this.trainerIds = new HashSet<>();
        }
        this.trainerIds.add(trainerId);
        return this;
    }
    
    public TraineeBuilder isActive(boolean isActive) {
        this.isActive = isActive;
        return this;
    }
    
    public TraineeBuilder username(String username) {
        this.username = username;
        return this;
    }
    
    public TraineeBuilder password(String password) {
        this.password = password;
        return this;
    }
    
    public Trainee build() {
        Trainee trainee = new Trainee();
        trainee.setFirstName(firstName);
        trainee.setLastName(lastName);
        trainee.setDateOfBirth(dateOfBirth);
        trainee.setAddress(address);
        trainee.setTrainerIds(trainerIds);
        trainee.setActive(isActive);
        trainee.setUsername(username);
        trainee.setPassword(password);
        return trainee;
    }
    
    public static TraineeBuilder builder() {
        return new TraineeBuilder();
    }
}
