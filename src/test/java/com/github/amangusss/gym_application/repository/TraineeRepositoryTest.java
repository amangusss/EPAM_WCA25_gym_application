package com.github.amangusss.gym_application.repository;

import com.github.amangusss.gym_application.entity.User;
import com.github.amangusss.gym_application.entity.trainee.Trainee;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;


@ExtendWith(MockitoExtension.class)
@DisplayName("TraineeRepository Tests")
class TraineeRepositoryTest {

    private static final Long USER_ID = 1L;
    private static final Long TRAINEE_ID = 1L;
    private static final String FIRST_NAME = "Aman";
    private static final String LAST_NAME = "Nazarkulov";
    private static final String USERNAME = "Aman.Nazarkulov";
    private static final String VALID_PASSWORD = "password123";
    private static final String INVALID_PASSWORD = "wrongPassword";
    private static final String NON_EXISTENT_USERNAME = "NonExistent";
    private static final LocalDate DATE_OF_BIRTH = LocalDate.of(2004, 2, 14);
    private static final String ADDRESS = "Isakeev st, 18/10 Block 15";
    private static final boolean IS_ACTIVE = true;

    @Mock
    private TraineeRepository traineeRepository;

    private Trainee testTrainee;

    @BeforeEach
    void setUp() {
        Mockito.reset(traineeRepository);

        User testUser = User.builder()
                .id(USER_ID)
                .firstName(FIRST_NAME)
                .lastName(LAST_NAME)
                .username(USERNAME)
                .password(VALID_PASSWORD)
                .isActive(IS_ACTIVE)
                .build();

        testTrainee = Trainee.builder()
                .id(TRAINEE_ID)
                .user(testUser)
                .dateOfBirth(DATE_OF_BIRTH)
                .address(ADDRESS)
                .build();
    }

    @Test
    @DisplayName("Should return trainee when finding by existing username")
    void shouldReturnTraineeWhenFindingByExistingUsername() {
        Mockito.when(traineeRepository.findByUserUsername(USERNAME))
                .thenReturn(Optional.of(testTrainee));

        Optional<Trainee> result = traineeRepository.findByUserUsername(USERNAME);

        Assertions.assertThat(result).isPresent();
        Assertions.assertThat(result.get().getUser().getUsername()).isEqualTo(USERNAME);
        Assertions.assertThat(result.get().getDateOfBirth()).isEqualTo(DATE_OF_BIRTH);
        Assertions.assertThat(result.get().getAddress()).isEqualTo(ADDRESS);

        Mockito.verify(traineeRepository, Mockito.times(1)).findByUserUsername(USERNAME);
    }

    @Test
    @DisplayName("Should return empty Optional when trainee not found by username")
    void shouldReturnEmptyOptionalWhenTraineeNotFoundByUsername() {
        Mockito.when(traineeRepository.findByUserUsername(NON_EXISTENT_USERNAME))
                .thenReturn(Optional.empty());

        Optional<Trainee> result = traineeRepository.findByUserUsername(NON_EXISTENT_USERNAME);

        Assertions.assertThat(result).isEmpty();

        Mockito.verify(traineeRepository, Mockito.times(1)).findByUserUsername(NON_EXISTENT_USERNAME);
    }

    @Test
    @DisplayName("Should return saved trainee when saving")
    void shouldReturnSavedTraineeWhenSaving() {
        Mockito.when(traineeRepository.save(ArgumentMatchers.any(Trainee.class)))
                .thenReturn(testTrainee);

        Trainee saved = traineeRepository.save(testTrainee);

        Assertions.assertThat(saved).isNotNull();
        Assertions.assertThat(saved.getId()).isEqualTo(TRAINEE_ID);
        Assertions.assertThat(saved.getUser().getUsername()).isEqualTo(USERNAME);
        Assertions.assertThat(saved.getAddress()).isEqualTo(ADDRESS);

        Mockito.verify(traineeRepository, Mockito.times(1)).save(testTrainee);
    }

    @Test
    @DisplayName("Should call delete when deleting trainee")
    void shouldCallDeleteWhenDeletingTrainee() {
        Mockito.doNothing().when(traineeRepository).delete(testTrainee);

        traineeRepository.delete(testTrainee);

        Mockito.verify(traineeRepository, Mockito.times(1)).delete(testTrainee);
    }

    @Test
    @DisplayName("Should return true when trainee exists by username and valid password")
    void shouldReturnTrueWhenTraineeExistsByUsernameAndValidPassword() {
        Mockito.when(traineeRepository.existsByUserUsernameAndUserPassword(USERNAME, VALID_PASSWORD))
                .thenReturn(true);

        boolean exists = traineeRepository.existsByUserUsernameAndUserPassword(USERNAME, VALID_PASSWORD);

        Assertions.assertThat(exists).isTrue();

        Mockito.verify(traineeRepository, Mockito.times(1))
                .existsByUserUsernameAndUserPassword(USERNAME, VALID_PASSWORD);
    }

    @Test
    @DisplayName("Should return false when checking existence with invalid password")
    void shouldReturnFalseWhenCheckingExistenceWithInvalidPassword() {
        Mockito.when(traineeRepository.existsByUserUsernameAndUserPassword(USERNAME, INVALID_PASSWORD))
                .thenReturn(false);

        boolean exists = traineeRepository.existsByUserUsernameAndUserPassword(USERNAME, INVALID_PASSWORD);

        Assertions.assertThat(exists).isFalse();

        Mockito.verify(traineeRepository, Mockito.times(1))
                .existsByUserUsernameAndUserPassword(USERNAME, INVALID_PASSWORD);
    }
}
