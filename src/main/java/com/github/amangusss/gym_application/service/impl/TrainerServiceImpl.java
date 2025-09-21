package com.github.amangusss.gym_application.service.impl;

import com.github.amangusss.gym_application.entity.trainer.Trainer;
import com.github.amangusss.gym_application.exception.TrainerNotFoundException;
import com.github.amangusss.gym_application.exception.ValidationException;
import com.github.amangusss.gym_application.repository.TraineeDAO;
import com.github.amangusss.gym_application.repository.TrainerDAO;
import com.github.amangusss.gym_application.service.TrainerService;
import com.github.amangusss.gym_application.util.PasswordGenerator;
import com.github.amangusss.gym_application.util.UsernameGenerator;
import com.github.amangusss.gym_application.util.constants.LoggerConstants;
import com.github.amangusss.gym_application.util.constants.ValidationConstants;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class TrainerServiceImpl implements TrainerService {

    public static final Logger logger = LoggerFactory.getLogger(TrainerServiceImpl.class);

    private TrainerDAO trainerDAO;
    private TraineeDAO traineeDAO;
    private UsernameGenerator usernameGenerator;
    private PasswordGenerator passwordGenerator;

    @Autowired
    public void setTrainerDAO(TrainerDAO trainerDAO) {
        this.trainerDAO = trainerDAO;
    }

    @Autowired
    public void setTraineeDAO(TraineeDAO traineeDAO) {
        this.traineeDAO = traineeDAO;
    }

    @Autowired
    public void setUsernameGenerator(UsernameGenerator usernameGenerator) {
        this.usernameGenerator = usernameGenerator;
    }

    @Autowired
    public void setPasswordGenerator(PasswordGenerator passwordGenerator) {
        this.passwordGenerator = passwordGenerator;
    }

    @Override
    public Trainer createTrainer(Trainer trainer) {
        if (trainer == null) {
            throw new ValidationException(ValidationConstants.TRAINER_NULL);
        }

        if (trainer.getFirstName() == null || trainer.getFirstName().trim().isEmpty()) {
            throw new ValidationException(ValidationConstants.TRAINER_FIRST_NAME_NULL);
        }

        if (trainer.getLastName() == null || trainer.getLastName().trim().isEmpty()) {
            throw new ValidationException(ValidationConstants.TRAINER_LAST_NAME_NULL);
        }

        if (trainer.getSpecialization() == null) {
            throw new ValidationException(ValidationConstants.TRAINER_SPECIALIZATION_NULL);
        }

        logger.info(LoggerConstants.TRAINER_CREATING, trainer.getFirstName(), trainer.getLastName());

        generateCredentials(trainer);

        if (!trainer.isActive()) {
            trainer.setActive(true);
        }

        Trainer savedTrainer = trainerDAO.save(trainer);
        logger.info(LoggerConstants.TRAINER_CREATED, savedTrainer.getUsername());
        return savedTrainer;
    }

    @Override
    public Trainer updateTrainer(Trainer trainer) {
        if (trainer == null) {
            throw new ValidationException(ValidationConstants.TRAINER_NULL);
        }

        if (trainer.getId() == null) {
            throw new ValidationException(ValidationConstants.TRAINER_ID_NULL);
        }

        logger.info(LoggerConstants.TRAINER_UPDATING, trainer.getFirstName(), trainer.getLastName());

        Trainer existingTrainer = trainerDAO.findById(trainer.getId());
        if (existingTrainer == null) {
            throw new TrainerNotFoundException(String.format(ValidationConstants.TRAINER_NOT_FOUND_BY_ID, trainer.getId()));
        }

        if (!existingTrainer.getFirstName().equals(trainer.getFirstName())
                || !existingTrainer.getLastName().equals(trainer.getLastName())) {
            generateCredentials(trainer);
        } else {
            trainer.setUsername(existingTrainer.getUsername());
            trainer.setPassword(existingTrainer.getPassword());
        }

        return trainerDAO.update(trainer);
    }

    @Override
    public Trainer findTrainerById(Long id) {
        return trainerDAO.findById(id);
    }

    @Override
    public List<Trainer> findAllTrainers() {
        return trainerDAO.findAll();
    }

    private void generateCredentials(Trainer trainer) {
        Set<String> existingUsernames = getAllExistingUsernames();

        String username = usernameGenerator.generateUsername(
                trainer.getFirstName(), trainer.getLastName(), existingUsernames);
        trainer.setUsername(username);

        String password = passwordGenerator.generatePassword();
        trainer.setPassword(password);
    }

    private Set<String> getAllExistingUsernames() {
        Set<String> usernames = new HashSet<>();

        traineeDAO.findAll().forEach(t -> {
            if (t.getUsername() != null) {
                usernames.add(t.getUsername());
            }
        });

        trainerDAO.findAll().forEach(t -> {
            if (t.getUsername() != null) {
                usernames.add(t.getUsername());
            }
        });

        return usernames;
    }
}
