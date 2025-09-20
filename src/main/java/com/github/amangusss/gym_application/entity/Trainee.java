package com.github.amangusss.gym_application.entity;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

public class Trainee extends User {

    private LocalDate dateOfBirth;
    private String address;
    private Set<Long> trainerIds;

    public Trainee() {
        super();
        this.trainerIds = new HashSet<>();
    }

    public Trainee(String firstName, String lastName) {
        super(firstName, lastName);
        this.trainerIds = new HashSet<>();
    }

    public Trainee(String firstName, String lastName, LocalDate dateOfBirth, String address) {
        this(firstName, lastName);
        this.dateOfBirth = dateOfBirth;
        this.address = address;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public Set<Long> getTrainerIds() {
        return trainerIds;
    }

    public void setTrainerIds(Set<Long> trainerIds) {
        this.trainerIds = trainerIds;
    }

    @Override
    public String toString() {
        return "Trainee{" +
                "id=" + getId() +
                ", firstName='" + getFirstName() + '\'' +
                ", lastName='" + getLastName() + '\'' +
                ", username='" + getUsername() + '\'' +
                ", isActive=" + isActive() +
                ", dateOfBirth=" + dateOfBirth +
                ", address='" + address + '\'' +
                '}';
    }
}
