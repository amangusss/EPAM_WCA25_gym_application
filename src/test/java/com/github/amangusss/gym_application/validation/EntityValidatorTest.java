package com.github.amangusss.gym_application.validation;

import com.github.amangusss.gym_application.entity.CustomUser;
import com.github.amangusss.gym_application.entity.TrainingType;
import com.github.amangusss.gym_application.entity.trainee.Trainee;
import com.github.amangusss.gym_application.entity.trainer.Trainer;
import com.github.amangusss.gym_application.entity.training.Training;
import com.github.amangusss.gym_application.exception.ValidationException;
import com.github.amangusss.gym_application.validation.entity.impl.EntityValidatorImpl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("EntityValidator Tests")
class EntityValidatorTest {

    @Mock
    private PasswordEncoder passwordEncoder;

    private EntityValidatorImpl entityValidator;

    private CustomUser activeUser;
    private CustomUser inactiveUser;
    private TrainingType trainingType;

    @BeforeEach
    void setUp() {
        entityValidator = new EntityValidatorImpl(passwordEncoder);

        activeUser = CustomUser.builder()
                .id(1L)
                .username("John.Doe")
                .password("encodedPassword")
                .firstName("John")
                .lastName("Doe")
                .isActive(true)
                .build();

        inactiveUser = CustomUser.builder()
                .id(2L)
                .username("Jane.Doe")
                .password("encodedPassword")
                .firstName("Jane")
                .lastName("Doe")
                .isActive(false)
                .build();

        trainingType = TrainingType.builder()
                .id(1L)
                .typeName("FITNESS")
                .build();
    }

    @Nested
    @DisplayName("Password Change Validation Tests")
    class PasswordChangeValidationTests {

        @Test
        @DisplayName("Should validate password change successfully")
        void shouldValidatePasswordChangeSuccessfully() {
            when(passwordEncoder.matches("oldPassword", "hashedOldPassword")).thenReturn(true);

            assertThatNoException().isThrownBy(() ->
                    entityValidator.validatePasswordChange("oldPassword", "newPassword", "hashedOldPassword"));
        }

        @Test
        @DisplayName("Should throw exception when old password doesn't match")
        void shouldThrowExceptionWhenOldPasswordDoesntMatch() {
            when(passwordEncoder.matches("wrongOldPassword", "hashedOldPassword")).thenReturn(false);

            assertThatThrownBy(() ->
                    entityValidator.validatePasswordChange("wrongOldPassword", "newPassword", "hashedOldPassword"))
                    .isInstanceOf(ValidationException.class)
                    .hasMessageContaining("Invalid old password");
        }

        @Test
        @DisplayName("Should throw exception when new password is same as old")
        void shouldThrowExceptionWhenNewPasswordIsSameAsOld() {
            when(passwordEncoder.matches("samePassword", "hashedOldPassword")).thenReturn(true);

            assertThatThrownBy(() ->
                    entityValidator.validatePasswordChange("samePassword", "samePassword", "hashedOldPassword"))
                    .isInstanceOf(ValidationException.class)
                    .hasMessageContaining("different from old password");
        }
    }

    @Nested
    @DisplayName("Date Range Validation Tests")
    class DateRangeValidationTests {

        @Test
        @DisplayName("Should validate date range successfully")
        void shouldValidateDateRangeSuccessfully() {
            LocalDate fromDate = LocalDate.of(2025, 1, 1);
            LocalDate toDate = LocalDate.of(2025, 12, 31);

            assertThatNoException().isThrownBy(() ->
                    entityValidator.validateDateRange(fromDate, toDate));
        }

        @Test
        @DisplayName("Should validate when dates are null")
        void shouldValidateWhenDatesAreNull() {
            assertThatNoException().isThrownBy(() ->
                    entityValidator.validateDateRange(null, null));
        }

        @Test
        @DisplayName("Should throw exception when fromDate is after toDate")
        void shouldThrowExceptionWhenFromDateIsAfterToDate() {
            LocalDate fromDate = LocalDate.of(2025, 12, 31);
            LocalDate toDate = LocalDate.of(2025, 1, 1);

            assertThatThrownBy(() ->
                    entityValidator.validateDateRange(fromDate, toDate))
                    .isInstanceOf(ValidationException.class)
                    .hasMessageContaining("cannot be after");
        }
    }

    @Nested
    @DisplayName("Trainee Validation Tests")
    class TraineeValidationTests {

        @Test
        @DisplayName("Should validate trainee successfully")
        void shouldValidateTraineeSuccessfully() {
            Trainee trainee = Trainee.builder()
                    .id(1L)
                    .user(activeUser)
                    .build();

            assertThatNoException().isThrownBy(() ->
                    entityValidator.validateTrainee(trainee));
        }

        @Test
        @DisplayName("Should throw exception when trainee is null")
        void shouldThrowExceptionWhenTraineeIsNull() {
            assertThatThrownBy(() ->
                    entityValidator.validateTrainee(null))
                    .isInstanceOf(ValidationException.class)
                    .hasMessageContaining("cannot be null");
        }

        @Test
        @DisplayName("Should throw exception when trainee user is null")
        void shouldThrowExceptionWhenTraineeUserIsNull() {
            Trainee trainee = Trainee.builder()
                    .id(1L)
                    .user(null)
                    .build();

            assertThatThrownBy(() ->
                    entityValidator.validateTrainee(trainee))
                    .isInstanceOf(ValidationException.class)
                    .hasMessageContaining("user cannot be null");
        }

        @Test
        @DisplayName("Should throw exception when trainee is not active")
        void shouldThrowExceptionWhenTraineeIsNotActive() {
            Trainee trainee = Trainee.builder()
                    .id(1L)
                    .user(inactiveUser)
                    .build();

            assertThatThrownBy(() ->
                    entityValidator.validateTrainee(trainee))
                    .isInstanceOf(ValidationException.class)
                    .hasMessageContaining("not active");
        }
    }

    @Nested
    @DisplayName("Trainer Validation Tests")
    class TrainerValidationTests {

        @Test
        @DisplayName("Should validate trainer successfully")
        void shouldValidateTrainerSuccessfully() {
            Trainer trainer = Trainer.builder()
                    .id(1L)
                    .user(activeUser)
                    .specialization(trainingType)
                    .build();

            assertThatNoException().isThrownBy(() ->
                    entityValidator.validateTrainer(trainer));
        }

        @Test
        @DisplayName("Should throw exception when trainer is null")
        void shouldThrowExceptionWhenTrainerIsNull() {
            assertThatThrownBy(() ->
                    entityValidator.validateTrainer(null))
                    .isInstanceOf(ValidationException.class)
                    .hasMessageContaining("cannot be null");
        }

        @Test
        @DisplayName("Should throw exception when trainer specialization is null")
        void shouldThrowExceptionWhenTrainerSpecializationIsNull() {
            Trainer trainer = Trainer.builder()
                    .id(1L)
                    .user(activeUser)
                    .specialization(null)
                    .build();

            assertThatThrownBy(() ->
                    entityValidator.validateTrainer(trainer))
                    .isInstanceOf(ValidationException.class)
                    .hasMessageContaining("specialization cannot be null");
        }
    }

    @Nested
    @DisplayName("Training Validation Tests")
    class TrainingValidationTests {

        @Test
        @DisplayName("Should validate training successfully")
        void shouldValidateTrainingSuccessfully() {
            Trainee trainee = Trainee.builder().id(1L).user(activeUser).build();
            Trainer trainer = Trainer.builder().id(1L).user(activeUser).specialization(trainingType).build();

            Training training = Training.builder()
                    .id(1L)
                    .trainee(trainee)
                    .trainer(trainer)
                    .trainingName("Morning Workout")
                    .trainingType(trainingType)
                    .trainingDate(LocalDate.now())
                    .trainingDuration(60.0)
                    .build();

            assertThatNoException().isThrownBy(() ->
                    entityValidator.validateTraining(training));
        }

        @Test
        @DisplayName("Should throw exception when training is null")
        void shouldThrowExceptionWhenTrainingIsNull() {
            assertThatThrownBy(() ->
                    entityValidator.validateTraining(null))
                    .isInstanceOf(ValidationException.class)
                    .hasMessageContaining("cannot be null");
        }

        @Test
        @DisplayName("Should throw exception when training name is blank")
        void shouldThrowExceptionWhenTrainingNameIsBlank() {
            Trainee trainee = Trainee.builder().id(1L).user(activeUser).build();
            Trainer trainer = Trainer.builder().id(1L).user(activeUser).specialization(trainingType).build();

            Training training = Training.builder()
                    .id(1L)
                    .trainee(trainee)
                    .trainer(trainer)
                    .trainingName("   ")
                    .trainingType(trainingType)
                    .trainingDate(LocalDate.now())
                    .trainingDuration(60.0)
                    .build();

            assertThatThrownBy(() ->
                    entityValidator.validateTraining(training))
                    .isInstanceOf(ValidationException.class)
                    .hasMessageContaining("name cannot be blank");
        }

        @Test
        @DisplayName("Should throw exception when training duration is zero or negative")
        void shouldThrowExceptionWhenTrainingDurationIsInvalid() {
            Trainee trainee = Trainee.builder().id(1L).user(activeUser).build();
            Trainer trainer = Trainer.builder().id(1L).user(activeUser).specialization(trainingType).build();

            Training training = Training.builder()
                    .id(1L)
                    .trainee(trainee)
                    .trainer(trainer)
                    .trainingName("Workout")
                    .trainingType(trainingType)
                    .trainingDate(LocalDate.now())
                    .trainingDuration(0.0)
                    .build();

            assertThatThrownBy(() ->
                    entityValidator.validateTraining(training))
                    .isInstanceOf(ValidationException.class)
                    .hasMessageContaining("duration must be positive");
        }
    }

    @Nested
    @DisplayName("Trainee For Creation Validation Tests")
    class TraineeForCreationValidationTests {

        @Test
        @DisplayName("Should validate trainee for creation successfully")
        void shouldValidateTraineeForCreationSuccessfully() {
            Trainee trainee = Trainee.builder()
                    .user(activeUser)
                    .address("123 Main St")
                    .build();

            assertThatNoException().isThrownBy(() ->
                    entityValidator.validateTraineeForCreation(trainee));
        }

        @Test
        @DisplayName("Should throw exception when address is blank")
        void shouldThrowExceptionWhenAddressIsBlank() {
            Trainee trainee = Trainee.builder()
                    .user(activeUser)
                    .address("")
                    .build();

            assertThatThrownBy(() ->
                    entityValidator.validateTraineeForCreation(trainee))
                    .isInstanceOf(ValidationException.class)
                    .hasMessageContaining("Address is required");
        }
    }

    @Nested
    @DisplayName("Trainer For Creation Validation Tests")
    class TrainerForCreationValidationTests {

        @Test
        @DisplayName("Should validate trainer for creation successfully")
        void shouldValidateTrainerForCreationSuccessfully() {
            Trainer trainer = Trainer.builder()
                    .user(activeUser)
                    .specialization(trainingType)
                    .build();

            assertThatNoException().isThrownBy(() ->
                    entityValidator.validateTrainerForCreation(trainer));
        }

        @Test
        @DisplayName("Should throw exception when specialization is null for creation")
        void shouldThrowExceptionWhenSpecializationIsNullForCreation() {
            Trainer trainer = Trainer.builder()
                    .user(activeUser)
                    .specialization(null)
                    .build();

            assertThatThrownBy(() ->
                    entityValidator.validateTrainerForCreation(trainer))
                    .isInstanceOf(ValidationException.class)
                    .hasMessageContaining("Specialization is required");
        }
    }
}
