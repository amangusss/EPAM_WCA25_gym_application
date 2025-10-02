package com.github.amangusss.gym_application.service;

import com.github.amangusss.gym_application.entity.trainee.Trainee;
import com.github.amangusss.gym_application.repository.TraineeDAO;
import com.github.amangusss.gym_application.repository.TrainerDAO;
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
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anySet;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import com.github.amangusss.gym_application.exception.ValidationException;

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
        when(usernameGenerator.generateUsername(eq("John"), eq("Doe"), anySet()))
                .thenReturn("John.Doe");
        when(passwordGenerator.generatePassword()).thenReturn("password123");
        when(traineeDAO.findAll()).thenReturn(List.of());
        when(trainerDAO.findAll()).thenReturn(List.of());
        when(traineeDAO.save(any(Trainee.class))).thenReturn(testTrainee);

        Trainee result = traineeService.createTrainee(testTrainee);

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
        assertThrows(ValidationException.class, () -> traineeService.createTrainee(null));
    }

    @Test
    void createTrainee_WithEmptyFirstName_ShouldThrowException() {
        testTrainee.setFirstName("");

        assertThrows(ValidationException.class, () -> traineeService.createTrainee(testTrainee));
    }

    @Test
    void updateTrainee_ShouldUpdateAndReturnTrainee() {
        testTrainee.setId(1L);
        when(traineeDAO.findById(1L)).thenReturn(testTrainee);
        when(traineeDAO.update(any(Trainee.class))).thenReturn(testTrainee);

        Trainee result = traineeService.updateTrainee(testTrainee);

        assertNotNull(result);
        verify(traineeDAO).findById(1L);
        verify(traineeDAO).update(testTrainee);
    }

    @Test
    void deleteTrainee_ShouldDeleteAndReturnTrue() {
        Long traineeId = 1L;
        testTrainee.setId(traineeId);
        when(traineeDAO.findById(traineeId)).thenReturn(testTrainee);
        when(traineeDAO.deleteById(traineeId)).thenReturn(true);

        boolean result = traineeService.deleteTrainee(traineeId);

        assertTrue(result);
        verify(traineeDAO).findById(traineeId);
        verify(traineeDAO).deleteById(traineeId);
    }

    @Test
    void findTraineeById_ShouldReturnTrainee() {
        Long traineeId = 1L;
        when(traineeDAO.findById(traineeId)).thenReturn(testTrainee);

        Trainee result = traineeService.findTraineeById(traineeId);

        assertNotNull(result);
        assertEquals(testTrainee, result);
        verify(traineeDAO).findById(traineeId);
    }

    @Test
    void findAllTrainees_ShouldReturnAllTrainees() {
        List<Trainee> trainees = Collections.singletonList(testTrainee);
        when(traineeDAO.findAll()).thenReturn(trainees);

        List<Trainee> result = traineeService.findAllTrainees();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testTrainee, result.get(0));
        verify(traineeDAO).findAll();
    }
}
