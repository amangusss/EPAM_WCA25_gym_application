package com.github.amangusss.gym_application.service;

import com.github.amangusss.gym_application.entity.TrainingType;
import com.github.amangusss.gym_application.entity.User;
import com.github.amangusss.gym_application.entity.trainer.Trainer;
import com.github.amangusss.gym_application.exception.AuthenticationException;
import com.github.amangusss.gym_application.repository.TrainerRepository;
import com.github.amangusss.gym_application.repository.UserRepository;
import com.github.amangusss.gym_application.service.impl.TrainerServiceImpl;
import com.github.amangusss.gym_application.util.credentials.PasswordGenerator;
import com.github.amangusss.gym_application.util.credentials.UsernameGenerator;
import com.github.amangusss.gym_application.validation.trainer.TrainerEntityValidation;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("TrainerService Tests")
class TrainerServiceTest {

    @Mock
    private TrainerRepository trainerRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private UsernameGenerator usernameGenerator;

    @Mock
    private PasswordGenerator passwordGenerator;

    @Mock
    private TrainerEntityValidation trainerEntityValidation;

    @InjectMocks
    private TrainerServiceImpl trainerService;

    private Trainer testTrainer;

    @BeforeEach
    void setUp() {
        TrainingType testTrainingType = TrainingType.builder()
                .id(1L)
                .typeName("Yoga")
                .build();

        User testUser = User.builder()
                .id(1L)
                .firstName("Aman")
                .lastName("Nazarkulov")
                .username("Aman.Nazarkulov")
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
    @DisplayName("Should create trainer with generated credentials")
    void createTrainer_ShouldGenerateCredentialsAndSave() {
        when(usernameGenerator.generateUsername(eq("Aman"), eq("Nazarkulov"), any()))
                .thenReturn("Aman.Nazarkulov");
        when(passwordGenerator.generatePassword()).thenReturn("password123");
        when(userRepository.existsByUsername(anyString())).thenReturn(false);
        doNothing().when(trainerEntityValidation).validateTrainerForCreationOrUpdate(any());
        when(trainerRepository.save(any(Trainer.class))).thenReturn(testTrainer);

        Trainer result = trainerService.createTrainer(testTrainer);

        assertThat(result).isNotNull();
        assertThat(result.getUser().getUsername()).isEqualTo("Aman.Nazarkulov");
        verify(trainerRepository).save(any(Trainer.class));
    }

    @Test
    @DisplayName("Should find trainer by username with valid password")
    void findTrainerByUsername_WithValidPassword_ShouldReturnTrainer() {
        when(trainerRepository.existsByUserUsernameAndUserPassword("Aman.Nazarkulov", "password123"))
                .thenReturn(true);
        when(trainerRepository.findByUserUsername("Aman.Nazarkulov"))
                .thenReturn(Optional.of(testTrainer));

        Trainer result = trainerService.findTrainerByUsername("Aman.Nazarkulov", "password123");

        assertThat(result).isNotNull();
        assertThat(result.getUser().getUsername()).isEqualTo("Aman.Nazarkulov");
        verify(trainerRepository).findByUserUsername("Aman.Nazarkulov");
    }

    @Test
    @DisplayName("Should throw exception when password is invalid")
    void findTrainerByUsername_WithInvalidPassword_ShouldThrowException() {
        when(trainerRepository.existsByUserUsernameAndUserPassword("Aman.Nazarkulov", "wrongPassword"))
                .thenReturn(false);

        assertThatThrownBy(() -> trainerService.findTrainerByUsername("Aman.Nazarkulov", "wrongPassword"))
                .isInstanceOf(AuthenticationException.class)
                .hasMessageContaining("Invalid credentials");
    }

    @Test
    @DisplayName("Should update trainer successfully")
    void updateTrainer_ShouldUpdateAndReturnTrainer() {
        TrainingType updatedType = TrainingType.builder()
                .id(2L)
                .typeName("Fitness")
                .build();

        Trainer updateData = Trainer.builder()
                .user(User.builder()
                        .firstName("Aman")
                        .lastName("Nazarkulov")
                        .isActive(true)
                        .build())
                .specialization(updatedType)
                .build();

        when(trainerRepository.existsByUserUsernameAndUserPassword("Aman.Nazarkulov", "password123"))
                .thenReturn(true);
        when(trainerRepository.findByUserUsername("Aman.Nazarkulov"))
                .thenReturn(Optional.of(testTrainer));
        doNothing().when(trainerEntityValidation).validateTrainerForCreationOrUpdate(any());
        when(trainerRepository.save(any(Trainer.class))).thenReturn(testTrainer);

        Trainer result = trainerService.updateTrainer("Aman.Nazarkulov", "password123", updateData);

        assertThat(result).isNotNull();
        verify(trainerRepository).save(any(Trainer.class));
    }

    @Test
    @DisplayName("Should activate trainer successfully")
    void activateTrainer_ShouldSetActiveStatusToTrue() {
        when(trainerRepository.existsByUserUsernameAndUserPassword("Aman.Nazarkulov", "password123"))
                .thenReturn(true);
        when(trainerRepository.findByUserUsername("Aman.Nazarkulov"))
                .thenReturn(Optional.of(testTrainer));
        when(trainerRepository.save(any(Trainer.class))).thenReturn(testTrainer);

        Trainer result = trainerService.activateTrainer("Aman.Nazarkulov", "password123");

        assertThat(result).isNotNull();
        verify(trainerRepository).save(testTrainer);
    }

    @Test
    @DisplayName("Should deactivate trainer successfully")
    void deactivateTrainer_ShouldSetActiveStatusToFalse() {
        when(trainerRepository.existsByUserUsernameAndUserPassword("Aman.Nazarkulov", "password123"))
                .thenReturn(true);
        when(trainerRepository.findByUserUsername("Aman.Nazarkulov"))
                .thenReturn(Optional.of(testTrainer));
        when(trainerRepository.save(any(Trainer.class))).thenReturn(testTrainer);

        Trainer result = trainerService.deactivateTrainer("Aman.Nazarkulov", "password123");

        assertThat(result).isNotNull();
        verify(trainerRepository).save(testTrainer);
    }
}
