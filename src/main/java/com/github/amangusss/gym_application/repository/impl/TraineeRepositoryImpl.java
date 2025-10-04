package com.github.amangusss.gym_application.repository.impl;

import com.github.amangusss.gym_application.entity.TrainingType;
import com.github.amangusss.gym_application.entity.trainee.Trainee;
import com.github.amangusss.gym_application.entity.trainer.Trainer;
import com.github.amangusss.gym_application.entity.training.Training;
import com.github.amangusss.gym_application.exception.TraineeNotFoundException;
import com.github.amangusss.gym_application.exception.ValidationException;
import com.github.amangusss.gym_application.repository.TraineeRepository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Repository
public class TraineeRepositoryImpl implements TraineeRepository {

    private static final Logger logger = LoggerFactory.getLogger(TraineeRepositoryImpl.class);

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Trainee save(Trainee trainee) {
        logger.debug("Attempting to save trainee with username: {}", trainee.getUsername());
        entityManager.persist(trainee);
        logger.info("Successfully saved trainee with username: {}", trainee.getUsername());
        return trainee;
    }

    @Override
    public boolean existsByUsernameAndPassword(String username, String password) {
        logger.debug("Checking if trainee exists with username: {}", username);

        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Long> criteriaQuery = criteriaBuilder.createQuery(Long.class);
        Root<Trainee> root = criteriaQuery.from(Trainee.class);

        criteriaQuery.select(criteriaBuilder.count(root));
        criteriaQuery.where(
                criteriaBuilder.and(
                    criteriaBuilder.equal(root.get("username"), username),
                    criteriaBuilder.equal(root.get("password"), password)
                )
        );

        TypedQuery<Long> query = entityManager.createQuery(criteriaQuery);
        boolean exists = query.getSingleResult() > 0;

        logger.debug("Trainee with username {} already exists.", username);
        return exists;
    }

    @Override
    public Trainee findByUsername(String username) {
        logger.debug("Finding trainee by username: {}", username);

        try {
            CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
            CriteriaQuery<Trainee> criteriaQuery = criteriaBuilder.createQuery(Trainee.class);
            Root<Trainee> root = criteriaQuery.from(Trainee.class);

            criteriaQuery.select(root);
            criteriaQuery.where(criteriaBuilder.equal(root.get("username"), username));

            TypedQuery<Trainee> query = entityManager.createQuery(criteriaQuery);
            Trainee trainee = query.getSingleResult();

            logger.info("Found trainee with username: {}", username);
            return trainee;
        } catch (NoResultException e) {
            logger.warn("Trainee not found with username: {}", username);
            throw new TraineeNotFoundException("Trainee with username " + username + " not found");
        }
    }

    @Override
    public Trainee updatePasswordByUsername(String username, String oldPassword, String newPassword) {
        logger.debug("Attempting to update password for trainee: {}", username);

        if (!existsByUsernameAndPassword(username, oldPassword)) {
            logger.warn("Failed password update attempt for trainee: {} - invalid credentials", username);
            throw new ValidationException("Invalid username or password");
        }

        Trainee trainee = findByUsername(username);
        trainee.setPassword(newPassword);
        Trainee updatedTrainee = entityManager.merge(trainee);

        logger.info("Successfully updated password for trainee: {}", username);
        return updatedTrainee;
    }

    @Override
    public Trainee update(Trainee trainee) {
        logger.debug("Attempting to update trainee profile: {}", trainee.getUsername());

        if (trainee.getUsername() == null) {
            logger.error("Update failed - trainee or username is null");
            throw new ValidationException("Trainee and username cannot be null");
        }

        Trainee existingTrainee = findByUsername(trainee.getUsername());

        existingTrainee.setFirstName(trainee.getFirstName());
        existingTrainee.setLastName(trainee.getLastName());
        existingTrainee.setDateOfBirth(trainee.getDateOfBirth());
        existingTrainee.setAddress(trainee.getAddress());

        Trainee updatedTrainee = entityManager.merge(existingTrainee);

        logger.info("Successfully updated trainee profile: {}", trainee.getUsername());
        return updatedTrainee;
    }

    @Override
    public Trainee updateActiveStatusByUsername(String username, boolean isActive) {
        logger.debug("Attempting to update active status for trainee: {} to {}", username, isActive);

        Trainee trainee = findByUsername(username);

        if (trainee.isActive() == isActive) {
            String status = isActive ? "active" : "inactive";
            logger.error("Cannot change status - trainee {} is already {}", username, status);
            throw new ValidationException("Trainee is already " + status);
        }

        trainee.setActive(isActive);
        entityManager.merge(trainee);
        logger.info("Successfully updated active status for trainee: {} to {}", username, isActive);

        return trainee;
    }

    @Override
    public void deleteByUsername(String username) {
        logger.debug("Attempting to delete trainee: {}", username);

        Trainee trainee = findByUsername(username);
        entityManager.remove(trainee);

        logger.info("Successfully deleted trainee: {} with cascade deletion of trainings", username);
    }

    @Override
    public List<Training> findTrainingsByUsername(String username, LocalDate fromDate, LocalDate toDate,
                                                   String trainerName, TrainingType trainingType) {
        logger.debug("Finding trainings for trainee: {} with criteria - from: {}, to: {}, trainerName: {}, type: {}",
                username, fromDate, toDate, trainerName, trainingType);

        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Training> criteriaQuery = criteriaBuilder.createQuery(Training.class);
        Root<Training> root = criteriaQuery.from(Training.class);

        Join<Training, Trainee> traineeJoin = root.join("trainee");
        Join<Training, Trainer> trainerJoin = root.join("trainer");

        List<Predicate> predicates = new ArrayList<>();
        predicates.add(criteriaBuilder.equal(traineeJoin.get("username"), username));

        if (fromDate != null) {
            predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("trainingDate"), fromDate));
        }

        if (toDate != null) {
            predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("trainingDate"), toDate));
        }

        if (trainerName != null && !trainerName.trim().isEmpty()) {
            Predicate trainerNamePredicate = buildTrainerNamePredicate(criteriaBuilder, trainerJoin, trainerName);
            predicates.add(trainerNamePredicate);
        }

        if (trainingType != null) {
            predicates.add(criteriaBuilder.equal(root.get("trainingType"), trainingType));
        }

        criteriaQuery.select(root);
        criteriaQuery.where(predicates.toArray(new Predicate[0]));
        criteriaQuery.orderBy(criteriaBuilder.desc(root.get("trainingDate")));

        List<Training> trainings = entityManager.createQuery(criteriaQuery).getResultList();

        logger.info("Found {} trainings for trainee: {}", trainings.size(), username);
        return trainings;
    }

    @Override
    public List<Trainer> findTrainersNotAssignedOnTraineeByUsername(String username) {
        logger.debug("Finding trainers not assigned to trainee: {}", username);

        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Trainer> criteriaQuery = criteriaBuilder.createQuery(Trainer.class);
        Root<Trainer> trainerRoot = criteriaQuery.from(Trainer.class);

        Subquery<Trainer> subquery = criteriaQuery.subquery(Trainer.class);
        Root<Trainee> traineeRoot = subquery.from(Trainee.class);
        Join<Trainee, Trainer> trainerJoin = traineeRoot.join("trainers");

        subquery.select(trainerJoin);
        subquery.where(criteriaBuilder.equal(traineeRoot.get("username"), username));

        criteriaQuery.select(trainerRoot);
        criteriaQuery.where(criteriaBuilder.not(trainerRoot.in(subquery)));
        criteriaQuery.orderBy(
                criteriaBuilder.asc(trainerRoot.get("lastName")),
                criteriaBuilder.asc(trainerRoot.get("firstName"))
        );

        List<Trainer> trainers = entityManager.createQuery(criteriaQuery).getResultList();

        logger.info("Found {} trainers not assigned to trainee: {}", trainers.size(), username);
        return trainers;
    }

    @Override
    public Trainee updateTrainersListByUsername(String username, Set<Trainer> trainers) {
        logger.debug("Attempting to update trainers list for trainee: {}", username);

        if (trainers == null) {
            logger.error("Update failed - trainers set is null");
            throw new ValidationException("Trainers set cannot be null");
        }

        Trainee trainee = findByUsername(username);
        trainee.setTrainers(trainers);
        Trainee updatedTrainee = entityManager.merge(trainee);

        logger.info("Successfully updated trainers list for trainee: {}", username);
        return updatedTrainee;
    }

    private Predicate buildTrainerNamePredicate(CriteriaBuilder criteriaBuilder,
                                                 Join<Training, Trainer> trainerJoin,
                                                 String trainerName) {
        String[] nameParts = trainerName.trim().split("\\s+", 2);

        if (nameParts.length == 2) {
            String firstName = nameParts[0].toLowerCase();
            String lastName = nameParts[1].toLowerCase();

            return criteriaBuilder.and(
                    criteriaBuilder.like(criteriaBuilder.lower(trainerJoin.get("firstName")), "%" + firstName + "%"),
                    criteriaBuilder.like(criteriaBuilder.lower(trainerJoin.get("lastName")), "%" + lastName + "%")
            );
        } else {
            String namePart = nameParts[0].toLowerCase();
            Predicate firstNameMatch = criteriaBuilder.like(
                    criteriaBuilder.lower(trainerJoin.get("firstName")), "%" + namePart + "%");
            Predicate lastNameMatch = criteriaBuilder.like(
                    criteriaBuilder.lower(trainerJoin.get("lastName")), "%" + namePart + "%");

            return criteriaBuilder.or(firstNameMatch, lastNameMatch);
        }
    }
}