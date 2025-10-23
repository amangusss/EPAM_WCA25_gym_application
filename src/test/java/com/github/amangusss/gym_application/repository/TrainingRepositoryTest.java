package com.github.amangusss.gym_application.repository;

import com.github.amangusss.gym_application.entity.TrainingType;
import com.github.amangusss.gym_application.entity.User;
import com.github.amangusss.gym_application.entity.trainee.Trainee;
import com.github.amangusss.gym_application.entity.trainer.Trainer;
import com.github.amangusss.gym_application.entity.training.Training;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("TrainingRepository Tests")
class TrainingRepositoryTest {

    @Mock
    private TrainingRepository trainingRepository;

    private Training testTraining;

    @BeforeEach
    void setUp() {
        TrainingType trainingType = TrainingType.builder()
                .id(1L)
                .typeName("Yoga")
                .build();

        User traineeUser = User.builder()
                .id(1L)
                .firstName("John")
                .lastName("Doe")
                .username("John.Doe")
                .password("password123")
                .isActive(true)
                .build();

        Trainee testTrainee = Trainee.builder()
                .id(1L)
                .user(traineeUser)
                .dateOfBirth(LocalDate.of(1990, 5, 15))
                .address("123 Main St")
                .build();

        User trainerUser = User.builder()
                .id(2L)
                .firstName("Jane")
                .lastName("Smith")
                .username("Jane.Smith")
                .password("password123")
                .isActive(true)
                .build();

        Trainer testTrainer = Trainer.builder()
                .id(1L)
                .user(trainerUser)
                .specialization(trainingType)
                .build();

        testTraining = Training.builder()
                .id(1L)
                .trainee(testTrainee)
                .trainer(testTrainer)
                .trainingName("Morning Yoga")
                .trainingType(trainingType)
                .trainingDate(LocalDate.now())
                .trainingDuration(60)
                .build();
    }

    @Test
    @DisplayName("Should save training successfully")
    void save_ShouldReturnSavedTraining() {
        when(trainingRepository.save(any(Training.class)))
                .thenReturn(testTraining);

        Training saved = trainingRepository.save(testTraining);

        assertThat(saved).isNotNull();
        assertThat(saved.getId()).isEqualTo(1L);
        assertThat(saved.getTrainingName()).isEqualTo("Morning Yoga");
        assertThat(saved.getTrainingDuration()).isEqualTo(60);

        verify(trainingRepository).save(testTraining);
    }

    @Test
    @DisplayName("Should find all trainings")
    void findAll_ShouldReturnListOfTrainings() {
        List<Training> trainingList = List.of(testTraining);
        when(trainingRepository.findAll()).thenReturn(trainingList);

        List<Training> result = trainingRepository.findAll();

        assertThat(result).isNotEmpty();
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getTrainingName()).isEqualTo("Morning Yoga");

        verify(trainingRepository).findAll();
    }

    @Test
    @DisplayName("Should find training by ID")
    void findById_ShouldReturnTraining() {
        when(trainingRepository.findById(1L))
                .thenReturn(Optional.of(testTraining));

        Optional<Training> result = trainingRepository.findById(1L);

        assertThat(result).isPresent();
        assertThat(result.get().getTrainingName()).isEqualTo("Morning Yoga");

        verify(trainingRepository).findById(1L);
    }

    @Test
    @DisplayName("Should return empty Optional when training not found")
    void findById_WhenNotFound_ShouldReturnEmpty() {
        when(trainingRepository.findById(999L))
                .thenReturn(Optional.empty());

        Optional<Training> result = trainingRepository.findById(999L);

        assertThat(result).isEmpty();
        verify(trainingRepository).findById(999L);
    }

    @Test
    @DisplayName("Should check if training exists by ID")
    void existsById_ShouldReturnTrue() {
        when(trainingRepository.existsById(1L)).thenReturn(true);

        boolean exists = trainingRepository.existsById(1L);

        assertThat(exists).isTrue();
        verify(trainingRepository).existsById(1L);
    }

    @Test
    @DisplayName("Should delete training")
    void delete_ShouldCallDelete() {
        doNothing().when(trainingRepository).delete(testTraining);

        trainingRepository.delete(testTraining);

        verify(trainingRepository, times(1)).delete(testTraining);
    }
}
