package com.github.amangusss.gym_application.service;

import com.github.amangusss.gym_application.entity.Trainee;
import com.github.amangusss.gym_application.repository.dao.TraineeDAO;
import com.github.amangusss.gym_application.repository.dao.TrainerDAO;
import com.github.amangusss.gym_application.service.impl.TraineeServiceImpl;
import com.github.amangusss.gym_application.util.PasswordGenerator;
import com.github.amangusss.gym_application.util.UsernameGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anySet;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TraineeServiceTest {

    @Mock
    private TraineeDAO traineeDAO;

    @Mock
    private TrainerDAO trainerDAO;

    @Mock
    private UsernameGenerator usernameGenerator;

    @Mock
    private PasswordGenerator passwordGenerator;

    @InjectMocks
    private TraineeServiceImpl traineeService;

    private Trainee testTrainee;

    @BeforeEach
    void setUp() {
        testTrainee = new Trainee("John", "Doe");
        testTrainee.setDateOfBirth(LocalDate.of(1990, 5, 15));
        testTrainee.setAddress("123 Main St");
    }

    @Test
    void createTrainee_ShouldGenerateCredentialsAndSave() {
        // Given
        when(usernameGenerator.generateUsername(eq("John"), eq("Doe"), anySet()))
                .thenReturn("John.Doe");
        when(passwordGenerator.generatePassword()).thenReturn("password123");
        when(traineeDAO.findAll()).thenReturn(Arrays.asList());
        when(trainerDAO.findAll()).thenReturn(Arrays.asList());
        when(traineeDAO.save(any(Trainee.class))).thenReturn(testTrainee);

        // When
        Trainee result = traineeService.createTrainee(testTrainee);

        // Then
        assertNotNull(result);
        assertEquals("John.Doe", result.getUsername());
        assertEquals("password123", result.getPassword());
        assertTrue(result.isActive());
        
        verify(usernameGenerator).generateUsername(eq("John"), eq("Doe"), anySet());
        verify(passwordGenerator).generatePassword();
        verify(traineeDAO).save(testTrainee);
    }

    @Test
    void createTrainee_WithNullTrainee_ShouldThrowException() {
        // When & Then
        assertThrows(IllegalArgumentException.class, () -> {
            traineeService.createTrainee(null);
        });
    }

    @Test
    void createTrainee_WithEmptyFirstName_ShouldThrowException() {
        // Given
        testTrainee.setFirstName("");

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> {
            traineeService.createTrainee(testTrainee);
        });
    }

    @Test
    void updateTrainee_ShouldUpdateAndReturnTrainee() {
        // Given
        testTrainee.setId(1L);
        when(traineeDAO.findById(1L)).thenReturn(testTrainee);
        when(traineeDAO.update(any(Trainee.class))).thenReturn(testTrainee);

        // When
        Trainee result = traineeService.updateTrainee(testTrainee);

        // Then
        assertNotNull(result);
        verify(traineeDAO).findById(1L);
        verify(traineeDAO).update(testTrainee);
    }

    @Test
    void deleteTrainee_ShouldDeleteAndReturnTrue() {
        // Given
        Long traineeId = 1L;
        testTrainee.setId(traineeId);
        when(traineeDAO.findById(traineeId)).thenReturn(testTrainee);
        when(traineeDAO.deleteById(traineeId)).thenReturn(true);

        // When
        boolean result = traineeService.deleteTrainee(traineeId);

        // Then
        assertTrue(result);
        verify(traineeDAO).findById(traineeId);
        verify(traineeDAO).deleteById(traineeId);
    }

    @Test
    void findTraineeById_ShouldReturnTrainee() {
        // Given
        Long traineeId = 1L;
        when(traineeDAO.findById(traineeId)).thenReturn(testTrainee);

        // When
        Trainee result = traineeService.findTraineeById(traineeId);

        // Then
        assertNotNull(result);
        assertEquals(testTrainee, result);
        verify(traineeDAO).findById(traineeId);
    }

    @Test
    void findAllTrainees_ShouldReturnAllTrainees() {
        // Given
        List<Trainee> trainees = Arrays.asList(testTrainee);
        when(traineeDAO.findAll()).thenReturn(trainees);

        // When
        List<Trainee> result = traineeService.findAllTrainees();

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testTrainee, result.get(0));
        verify(traineeDAO).findAll();
    }
}
