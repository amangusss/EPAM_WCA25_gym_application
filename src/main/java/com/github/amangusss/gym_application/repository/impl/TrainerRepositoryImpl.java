package com.github.amangusss.gym_application.repository.impl;

import com.github.amangusss.gym_application.entity.trainee.Trainee;
import com.github.amangusss.gym_application.entity.trainer.Trainer;
import com.github.amangusss.gym_application.entity.training.Training;
import com.github.amangusss.gym_application.exception.TrainerNotFoundException;
import com.github.amangusss.gym_application.exception.ValidationException;
import com.github.amangusss.gym_application.repository.TrainerRepository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Repository
public class TrainerRepositoryImpl implements TrainerRepository {

    private static final Logger logger = LoggerFactory.getLogger(TrainerRepositoryImpl.class);

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Trainer save(Trainer trainer) {
        logger.debug("Attempting to save trainer with username: {}", trainer.getUsername());
        entityManager.persist(trainer);
        logger.info("Successfully saved trainer with username: {}", trainer.getUsername());
        return trainer;
    }

    @Override
    public boolean existsByUsernameAndPassword(String username, String password) {
        logger.debug("Checking if trainer exists with username: {}", username);

        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Long> criteriaQuery = criteriaBuilder.createQuery(Long.class);
        Root<Trainer> root = criteriaQuery.from(Trainer.class);

        criteriaQuery.select(criteriaBuilder.count(root));
        criteriaQuery.where(
                criteriaBuilder.and(
                        criteriaBuilder.equal(root.get("username"), username),
                        criteriaBuilder.equal(root.get("password"), password)
                )
        );

        TypedQuery<Long> query = entityManager.createQuery(criteriaQuery);
        boolean exists = query.getSingleResult() > 0;

        logger.debug("Trainer with username {} already exists.", username);
        return exists;
    }

    @Override
    public Trainer findByUsername(String username) {
        logger.debug("Finding trainer by username: {}", username);

        try {
            CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
            CriteriaQuery<Trainer> criteriaQuery = criteriaBuilder.createQuery(Trainer.class);
            Root<Trainer> root = criteriaQuery.from(Trainer.class);

            criteriaQuery.select(root);
            criteriaQuery.where(criteriaBuilder.equal(root.get("username"), username));

            TypedQuery<Trainer> query = entityManager.createQuery(criteriaQuery);
            Trainer trainer = query.getSingleResult();

            logger.debug("Found trainer with username: {}", username);
            return trainer;
        } catch (NoResultException e) {
            logger.error("No trainer found with username: {}", username);
            throw new TrainerNotFoundException("Trainer with username " + username + " not found.");
        }
    }

    @Override
    public Trainer updatePasswordByUsername(String username, String oldPassword, String newPassword) {
        logger.debug("Attempting to update password for trainer: {}", username);

        if (!existsByUsernameAndPassword(username, oldPassword)) {
            logger.warn("Failed password update attempt for trainer: {} - invalid credentials", username);
            throw new ValidationException("Invalid username or password.");
        }

        Trainer trainer = findByUsername(username);
        trainer.setPassword(newPassword);
        Trainer updatedTrainer = entityManager.merge(trainer);

        logger.info("Successfully updated password for trainer: {}", username);
        return updatedTrainer;

    }

    @Override
    public Trainer update(Trainer trainer) {
        logger.debug("Attempting to update trainer profile: {}", trainer.getUsername());

        if (trainer.getUsername() == null) {
            logger.error("Failed to update trainer profile: username is null");
            throw new ValidationException("Username cannot be null.");
        }

        Trainer existingTrainer = findByUsername(trainer.getUsername());

        existingTrainer.setFirstName(trainer.getFirstName());
        existingTrainer.setLastName(trainer.getLastName());
        existingTrainer.setSpecialization(trainer.getSpecialization());

        Trainer updatedTrainer = entityManager.merge(existingTrainer);
        logger.info("Successfully updated trainer profile: {}", trainer.getUsername());
        return updatedTrainer;
    }

    @Override
    public Trainer updateActiveStatusByUsername(String username, boolean isActive) {
        logger.debug("Attempting to update active status for trainer: {} to {}", username, isActive);

        Trainer trainer = findByUsername(username);

        //TODO вынести
        if (trainer.isActive() == isActive) {
            String status = isActive ? "active" : "inactive";
            logger.error("Cannot change status - trainer {} is already {}", username, status);
            throw new ValidationException("Trainer is already " + status);
        }

        trainer.setActive(isActive);
        entityManager.merge(trainer);
        logger.info("Successfully updated active status for trainer: {} to {}", username, isActive);

        return trainer;
    }

    @Override
    public List<Training> findTrainingsByUsername(String username, LocalDate fromDate, LocalDate toDate, String traineeName) {
        logger.debug("Finding trainings for trainer: {} with criteria - from: {}, to: {}, traineeName: {}",
                username, fromDate, toDate, traineeName);

        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Training> criteriaQuery = criteriaBuilder.createQuery(Training.class);
        Root<Training> root = criteriaQuery.from(Training.class);

        Join<Training, Trainer> trainerJoin = root.join("trainer");
        Join<Training, Trainee> traineeJoin = root.join("trainee");

        List<Predicate> predicates = new ArrayList<>();
        predicates.add(criteriaBuilder.equal(trainerJoin.get("username"), username));

        if (fromDate != null) {
            predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("trainingDate"), fromDate));
        }

        if (toDate != null) {
            predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("trainingDate"), toDate));
        }

        if (traineeName != null && !traineeName.trim().isEmpty()) {
            Predicate traineeNamePredicate = buildTraineeNamePredicate(criteriaBuilder, traineeJoin, traineeName);
            predicates.add(traineeNamePredicate);
        }

        criteriaQuery.select(root);
        criteriaQuery.where(predicates.toArray(new Predicate[0]));
        criteriaQuery.orderBy(criteriaBuilder.desc(root.get("trainingDate")));

        List<Training> trainings = entityManager.createQuery(criteriaQuery).getResultList();

        logger.info("Found {} trainings for trainer: {}", trainings.size(), username);
        return trainings;
    }

    private Predicate buildTraineeNamePredicate(CriteriaBuilder criteriaBuilder,
                                                Join<Training, Trainee> traineeJoin,
                                                String traineeName) {
        String[] nameParts = traineeName.trim().split("\\s+", 2);

        if (nameParts.length == 2) {
            String firstName = nameParts[0].toLowerCase();
            String lastName = nameParts[1].toLowerCase();

            return criteriaBuilder.and(
                    criteriaBuilder.like(criteriaBuilder.lower(traineeJoin.get("firstName")), "%" + firstName + "%"),
                    criteriaBuilder.like(criteriaBuilder.lower(traineeJoin.get("lastName")), "%" + lastName + "%")
            );
        } else {
            String namePart = nameParts[0].toLowerCase();
            Predicate firstNameMatch = criteriaBuilder.like(
                    criteriaBuilder.lower(traineeJoin.get("firstName")), "%" + namePart + "%");
            Predicate lastNameMatch = criteriaBuilder.like(
                    criteriaBuilder.lower(traineeJoin.get("lastName")), "%" + namePart + "%");

            return criteriaBuilder.or(firstNameMatch, lastNameMatch);
        }
    }

}