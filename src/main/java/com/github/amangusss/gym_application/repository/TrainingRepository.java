package com.github.amangusss.gym_application.repository;

import com.github.amangusss.gym_application.entity.training.Training;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TrainingRepository extends JpaRepository<Training, Long> {
}