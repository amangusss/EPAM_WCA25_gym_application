package com.github.amangusss.gym_application.entity.trainee;

import com.github.amangusss.gym_application.entity.User;
import com.github.amangusss.gym_application.entity.trainer.Trainer;
import com.github.amangusss.gym_application.entity.training.Training;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Column;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinTable;
import jakarta.persistence.JoinColumn;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Entity
@Table(name = "trainees")
public class Trainee extends User {

    @Column(name = "date_of_birth")
    private LocalDate dateOfBirth;

    @Column(name = "address", nullable = false)
    private String address;

    @Builder.Default
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "trainee_trainer",
            joinColumns = @JoinColumn(name = "trainee_id"),
            inverseJoinColumns = @JoinColumn(name = "trainer_id")
    )
    @ToString.Exclude
    private Set<Trainer> trainers = new HashSet<>();

    @Builder.Default
    @OneToMany(mappedBy = "trainee",
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.LAZY)
    @ToString.Exclude
    private Set<Training> trainings = new HashSet<>();

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Trainee trainee = (Trainee) o;
        return getId() != null && Objects.equals(getId(), trainee.getId());
    }

    @Override
    public final int hashCode() {
        return getClass().hashCode();
    }
}
