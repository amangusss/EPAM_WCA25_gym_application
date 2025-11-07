package com.github.amangusss.gym_application.repository;

import com.github.amangusss.gym_application.entity.CustomUser;
import com.github.amangusss.gym_application.entity.trainee.Trainee;

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

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("TraineeRepository Tests")
class TraineeRepositoryTest {

    private static final Long USER_ID = 1L;
    private static final Long TRAINEE_ID = 1L;
    private static final String FIRST_NAME = "Aman";
    private static final String LAST_NAME = "Nazarkulov";
    private static final String USERNAME = "Aman.Nazarkulov";
    private static final String VALID_PASSWORD = "password123";
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

        CustomUser testUser = CustomUser.builder()
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
        when(traineeRepository.findByUserUsername(USERNAME))
                .thenReturn(Optional.of(testTrainee));

        Optional<Trainee> result = traineeRepository.findByUserUsername(USERNAME);

        assertThat(result).isPresent();
        assertThat(result.get().getUser().getUsername()).isEqualTo(USERNAME);
        assertThat(result.get().getDateOfBirth()).isEqualTo(DATE_OF_BIRTH);
        assertThat(result.get().getAddress()).isEqualTo(ADDRESS);

        verify(traineeRepository, times(1)).findByUserUsername(USERNAME);
    }

    @Test
    @DisplayName("Should return empty Optional when trainee not found by username")
    void shouldReturnEmptyOptionalWhenTraineeNotFoundByUsername() {
        when(traineeRepository.findByUserUsername(NON_EXISTENT_USERNAME))
                .thenReturn(Optional.empty());

        Optional<Trainee> result = traineeRepository.findByUserUsername(NON_EXISTENT_USERNAME);

        assertThat(result).isEmpty();

        verify(traineeRepository, times(1)).findByUserUsername(NON_EXISTENT_USERNAME);
    }

    @Test
    @DisplayName("Should return saved trainee when saving")
    void shouldReturnSavedTraineeWhenSaving() {
        when(traineeRepository.save(ArgumentMatchers.any(Trainee.class)))
                .thenReturn(testTrainee);

        Trainee saved = traineeRepository.save(testTrainee);

        assertThat(saved).isNotNull();
        assertThat(saved.getId()).isEqualTo(TRAINEE_ID);
        assertThat(saved.getUser().getUsername()).isEqualTo(USERNAME);
        assertThat(saved.getAddress()).isEqualTo(ADDRESS);

        verify(traineeRepository, times(1)).save(testTrainee);
    }

    @Test
    @DisplayName("Should call delete when deleting trainee")
    void shouldCallDeleteWhenDeletingTrainee() {
        Mockito.doNothing().when(traineeRepository).delete(testTrainee);

        traineeRepository.delete(testTrainee);

        verify(traineeRepository, times(1)).delete(testTrainee);
    }
}
