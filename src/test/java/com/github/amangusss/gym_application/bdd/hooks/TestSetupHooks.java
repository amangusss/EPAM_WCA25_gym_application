package com.github.amangusss.gym_application.bdd.hooks;

import com.github.amangusss.gym_application.bdd.context.SharedTestContext;
import com.github.amangusss.gym_application.repository.*;

import io.cucumber.java.After;
import io.cucumber.java.Before;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = lombok.AccessLevel.PRIVATE, makeFinal = true)
@SuppressWarnings("unused")
public class TestSetupHooks {

    SharedTestContext sharedTestContext;
    
    @Autowired
    TrainingRepository trainingRepository;
    
    @Autowired
    TraineeRepository traineeRepository;
    
    @Autowired
    TrainerRepository trainerRepository;

    @Autowired
    UserRepository userRepository;
    
    @Autowired
    LoginAttemptRepository loginAttemptRepository;

    @Autowired(required = false)
    CacheManager cacheManager;

    @Before
    @Transactional
    public void setUp() {
        log.info("Starting new test scenario - cleaning up database and cache");
        cleanupDatabase();
        clearCaches();
        sharedTestContext.reset();
    }

    @After
    @Transactional
    public void tearDown() {
        log.info("Cleaning up after test scenario");
        cleanupDatabase();
        clearCaches();
        sharedTestContext.reset();
    }

    private void cleanupDatabase() {
        try {
            trainingRepository.deleteAll();
            trainingRepository.flush();

            traineeRepository.findAll().forEach(trainee -> {
                trainee.getTrainers().clear();
            });
            traineeRepository.flush();

            traineeRepository.deleteAll();
            traineeRepository.flush();

            trainerRepository.deleteAll();
            trainerRepository.flush();

            loginAttemptRepository.deleteAll();
            loginAttemptRepository.flush();

            userRepository.deleteAll();
            userRepository.flush();

            log.debug("Database cleanup completed successfully");
        } catch (Exception e) {
            log.warn("Error during database cleanup: {}", e.getMessage());
        }
    }

    private void clearCaches() {
        try {
            if (cacheManager != null) {
                cacheManager.getCacheNames().forEach(cacheName -> {
                    var cache = cacheManager.getCache(cacheName);
                    if (cache != null) {
                        cache.clear();
                        log.debug("Cleared cache: {}", cacheName);
                    }
                });
            }
        } catch (Exception e) {
            log.warn("Error clearing caches: {}", e.getMessage());
        }
    }
}