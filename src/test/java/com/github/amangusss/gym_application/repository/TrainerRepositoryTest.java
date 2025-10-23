package com.github.amangusss.gym_application.repository;

import com.github.amangusss.gym_application.entity.TrainingType;
import com.github.amangusss.gym_application.entity.User;
import com.github.amangusss.gym_application.entity.trainer.Trainer;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("TrainerRepository Tests")
class TrainerRepositoryTest {

    @Mock
    private TrainerRepository trainerRepository;

    private Trainer testTrainer;

    @BeforeEach
    void setUp() {
        TrainingType testTrainingType = TrainingType.builder()
                .id(1L)
                .typeName("Yoga")
                .build();

        User testUser = User.builder()
                .id(1L)
                .firstName("Dastan")
                .lastName("Ibraimov")
                .username("Dastan.Ibraimov")
                .password("password123")
                .isActive(true)
                .build();

        testTrainer = Trainer.builder()
                .id(1L)
                .user(testUser)
                .specialization(testTrainingType)
                .build();
    }

    @Test
    @DisplayName("Should find trainer by username")
    void findByUserUsername_ShouldReturnTrainer() {
        when(trainerRepository.findByUserUsername("Dastan.Ibraimov"))
                .thenReturn(Optional.of(testTrainer));

        Optional<Trainer> result = trainerRepository.findByUserUsername("Dastan.Ibraimov");

        assertThat(result).isPresent();
        assertThat(result.get().getUser().getUsername()).isEqualTo("Dastan.Ibraimov");
        assertThat(result.get().getUser().getFirstName()).isEqualTo("Dastan");

        verify(trainerRepository, times(1)).findByUserUsername("Dastan.Ibraimov");
    }

    @Test
    @DisplayName("Should return empty Optional when trainer not found")
    void findByUserUsername_WhenNotFound_ShouldReturnEmpty() {
        when(trainerRepository.findByUserUsername("NonExistent"))
                .thenReturn(Optional.empty());

        Optional<Trainer> result = trainerRepository.findByUserUsername("NonExistent");

        assertThat(result).isEmpty();
        verify(trainerRepository).findByUserUsername("NonExistent");
    }

    @Test
    @DisplayName("Should save trainer successfully")
    void save_ShouldReturnSavedTrainer() {
        when(trainerRepository.save(any(Trainer.class)))
                .thenReturn(testTrainer);

        Trainer saved = trainerRepository.save(testTrainer);

        assertThat(saved).isNotNull();
        assertThat(saved.getId()).isEqualTo(1L);
        assertThat(saved.getUser().getUsername()).isEqualTo("Dastan.Ibraimov");
        verify(trainerRepository).save(testTrainer);
    }

    @Test
    @DisplayName("Should check existence by username and password")
    void existsByUserUsernameAndUserPassword_ShouldReturnTrue() {
        when(trainerRepository.existsByUserUsernameAndUserPassword("Dastan.Ibraimov", "password123"))
                .thenReturn(true);

        boolean exists = trainerRepository.existsByUserUsernameAndUserPassword("Dastan.Ibraimov", "password123");

        assertThat(exists).isTrue();
        verify(trainerRepository).existsByUserUsernameAndUserPassword("Dastan.Ibraimov", "password123");
    }

    @Test
    @DisplayName("Should return false with wrong password")
    void existsByUserUsernameAndUserPassword_WithWrongPassword_ShouldReturnFalse() {
        when(trainerRepository.existsByUserUsernameAndUserPassword("Dastan.Ibraimov", "wrongPassword"))
                .thenReturn(false);

        boolean exists = trainerRepository.existsByUserUsernameAndUserPassword("Dastan.Ibraimov", "wrongPassword");

        assertThat(exists).isFalse();
        verify(trainerRepository).existsByUserUsernameAndUserPassword("Dastan.Ibraimov", "wrongPassword");
    }
}
