package com.github.amangusss.gym_application.entity.trainer;

import com.github.amangusss.gym_application.entity.TrainingType;
import com.github.amangusss.gym_application.entity.User;
import com.github.amangusss.gym_application.entity.trainee.Trainee;

import java.util.HashSet;
import java.util.Set;

public class Trainer extends User {

    private TrainingType specialization;
    private Set<Trainee> traineeId;

    public Trainer() {
        super();
        this.traineeId = new HashSet<>();
    }

    public Trainer(String firstName, String lastName, TrainingType specialization) {
        super(firstName, lastName);
        this.specialization = specialization;
        this.traineeId = new HashSet<>();
    }

    public Set<Trainee> getTraineeId() {
        return traineeId;
    }

    public void setTraineeId(Set<Trainee> traineeId) {
        this.traineeId = traineeId;
    }

    public TrainingType getSpecialization() {
        return specialization;
    }

    public void setSpecialization(TrainingType specialization) {
        this.specialization = specialization;
    }

    @Override
    public String toString() {
        return "Trainer{" +
                "id=" + getId() +
                ", firstName='" + getFirstName() + '\'' +
                ", lastName='" + getLastName() + '\'' +
                ", username='" + getUsername() + '\'' +
                ", isActive=" + isActive() +
                ", specialization=" + specialization +
                '}';
    }
}
