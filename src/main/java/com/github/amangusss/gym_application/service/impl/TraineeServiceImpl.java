package com.github.amangusss.gym_application.service.impl;

import com.github.amangusss.gym_application.entity.Trainee;
import com.github.amangusss.gym_application.repository.dao.TraineeDAO;
import com.github.amangusss.gym_application.repository.dao.TrainerDAO;
import com.github.amangusss.gym_application.service.TraineeService;
import com.github.amangusss.gym_application.util.PasswordGenerator;
import com.github.amangusss.gym_application.util.UsernameGenerator;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class TraineeServiceImpl implements TraineeService {

    public static final Logger logger = org.slf4j.LoggerFactory.getLogger(TraineeServiceImpl.class);

    private TraineeDAO traineeDAO;
    private TrainerDAO trainerDAO;
    private UsernameGenerator usernameGenerator;
    private PasswordGenerator passwordGenerator;

    @Autowired
    public void setTraineeDAO(TraineeDAO traineeDAO) {
        this.traineeDAO = traineeDAO;
    }

    @Autowired
    public void setTrainerDAO(TrainerDAO trainerDAO) {
        this.trainerDAO = trainerDAO;
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
    public Trainee createTrainee(Trainee trainee) {
        if (trainee == null) {
            throw new IllegalArgumentException("Trainee cannot be null");
        }

        if (trainee.getFirstName() == null || trainee.getFirstName().trim().isEmpty()) {
            throw new IllegalArgumentException("Trainee first name cannot be null or empty");
        }

        if (trainee.getLastName() == null || trainee.getLastName().trim().isEmpty()) {
            throw new IllegalArgumentException("Trainee last name cannot be null or empty");
        }

        logger.info("Creating trainee: {} {}", trainee.getFirstName(), trainee.getLastName());

        generateCredentials(trainee);

        if (!trainee.isActive()) {
            trainee.setActive(true);
        }

        Trainee savedTrainee = traineeDAO.save(trainee);
        logger.info("Trainee created successfully with username: {}", savedTrainee.getUsername());

        return savedTrainee;
    }

    @Override
    public Trainee updateTrainee(Trainee trainee) {
        if (trainee == null) {
            throw new IllegalArgumentException("Trainee cannot be null");
        }

        if (trainee.getId() == null) {
            throw new IllegalArgumentException("Trainee id cannot be null");
        }

        logger.info("Updating trainee: {} {}", trainee.getFirstName(), trainee.getLastName());

        Trainee existingTrainee = traineeDAO.findById(trainee.getId());
        if (existingTrainee == null) {
            throw new IllegalArgumentException("Trainee with id " + trainee.getId() + " not found");
        }

        if (!existingTrainee.getFirstName().equals(trainee.getFirstName())
                || !existingTrainee.getLastName().equals(trainee.getLastName())) {generateCredentials(trainee);
        } else {
            trainee.setUsername(existingTrainee.getUsername());
            trainee.setPassword(existingTrainee.getPassword());
        }

        return traineeDAO.update(trainee);
    }

    @Override
    public boolean deleteTrainee(Long traineeId) {
        if (traineeId == null) {
            throw new IllegalArgumentException("Trainee cannot be null");
        }

        Trainee trainee = traineeDAO.findById(traineeId);
        logger.info("Deleting trainee: {} {}", trainee.getFirstName(), trainee.getLastName());
        return traineeDAO.deleteById(trainee.getId());
    }

    @Override
    public Trainee findTraineeById(Long id) {
        return traineeDAO.findById(id);
    }

    @Override
    public List<Trainee> findAllTrainees() {
        return traineeDAO.findAll();
    }

    private void generateCredentials(Trainee trainee) {
        Set<String> existingUsernames = getAllExistingUsernames();

        String username = usernameGenerator.generateUsername(trainee.getFirstName(), trainee.getLastName(), existingUsernames);
        trainee.setUsername(username);

        String password = passwordGenerator.generatePassword();
        trainee.setPassword(password);
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
