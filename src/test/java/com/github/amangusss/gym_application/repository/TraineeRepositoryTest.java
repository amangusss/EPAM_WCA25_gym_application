package com.github.amangusss.gym_application.repository;

import com.github.amangusss.gym_application.entity.User;
import com.github.amangusss.gym_application.entity.trainee.Trainee;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("TraineeRepository Tests")
class TraineeRepositoryTest {

    @Mock
    private TraineeRepository traineeRepository;

    private Trainee testTrainee;

    @BeforeEach
    void setUp() {
        User testUser = User.builder()
                .id(1L)
                .firstName("Aman")
                .lastName("Nazarkulov")
                .username("Aman.Nazarkulov")
                .password("password123")
                .isActive(true)
                .build();

        testTrainee = Trainee.builder()
                .id(1L)
                .user(testUser)
                .dateOfBirth(LocalDate.of(2004, 2, 14))
                .address("Isakeev st, 18/10 Block 15")
                .build();
    }

    @Test
    @DisplayName("Should find trainee by username")
    void findByUserUsername_ShouldReturnTrainee() {
        when(traineeRepository.findByUserUsername("Aman.Nazarkulov"))
                .thenReturn(Optional.of(testTrainee));

        Optional<Trainee> result = traineeRepository.findByUserUsername("Aman.Nazarkulov");

        assertThat(result).isPresent();
        assertThat(result.get().getUser().getUsername()).isEqualTo("Aman.Nazarkulov");
        assertThat(result.get().getDateOfBirth()).isEqualTo(LocalDate.of(2004, 2, 14));
        assertThat(result.get().getAddress()).isEqualTo("Isakeev st, 18/10 Block 15");

        verify(traineeRepository).findByUserUsername("Aman.Nazarkulov");
    }

    @Test
    @DisplayName("Should return empty Optional when trainee not found")
    void findByUserUsername_WhenNotFound_ShouldReturnEmpty() {
        when(traineeRepository.findByUserUsername("NonExistent"))
                .thenReturn(Optional.empty());

        Optional<Trainee> result = traineeRepository.findByUserUsername("NonExistent");

        assertThat(result).isEmpty();
        verify(traineeRepository).findByUserUsername("NonExistent");
    }

    @Test
    @DisplayName("Should save trainee successfully")
    void save_ShouldReturnSavedTrainee() {
        when(traineeRepository.save(any(Trainee.class)))
                .thenReturn(testTrainee);

        Trainee saved = traineeRepository.save(testTrainee);

        assertThat(saved).isNotNull();
        assertThat(saved.getId()).isEqualTo(1L);
        assertThat(saved.getUser().getUsername()).isEqualTo("Aman.Nazarkulov");
        assertThat(saved.getAddress()).isEqualTo("Isakeev st, 18/10 Block 15");

        verify(traineeRepository).save(testTrainee);
    }

    @Test
    @DisplayName("Should delete trainee")
    void delete_ShouldCallDelete() {
        doNothing().when(traineeRepository).delete(testTrainee);

        traineeRepository.delete(testTrainee);

        verify(traineeRepository, times(1)).delete(testTrainee);
    }

    @Test
    @DisplayName("Should check existence by username and password")
    void existsByUserUsernameAndUserPassword_ShouldReturnTrue() {
        when(traineeRepository.existsByUserUsernameAndUserPassword("Aman.Nazarkulov", "password123"))
                .thenReturn(true);

        boolean exists = traineeRepository.existsByUserUsernameAndUserPassword("Aman.Nazarkulov", "password123");

        assertThat(exists).isTrue();
        verify(traineeRepository).existsByUserUsernameAndUserPassword("Aman.Nazarkulov", "password123");
    }

    @Test
    @DisplayName("Should return false with wrong password")
    void existsByUserUsernameAndUserPassword_WithWrongPassword_ShouldReturnFalse() {
        when(traineeRepository.existsByUserUsernameAndUserPassword("Aman.Nazarkulov", "wrongPassword"))
                .thenReturn(false);

        boolean exists = traineeRepository.existsByUserUsernameAndUserPassword("Aman.Nazarkulov", "wrongPassword");

        assertThat(exists).isFalse();
        verify(traineeRepository).existsByUserUsernameAndUserPassword("Aman.Nazarkulov", "wrongPassword");
    }
}
