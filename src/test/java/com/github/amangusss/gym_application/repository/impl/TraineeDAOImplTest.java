package com.github.amangusss.gym_application.repository.impl;

import com.github.amangusss.gym_application.entity.trainee.Trainee;
import com.github.amangusss.gym_application.entity.trainee.TraineeBuilder;
import com.github.amangusss.gym_application.storage.TraineeStorage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TraineeDAOImplTest {

    @Mock
    private TraineeStorage traineeStorage;

    @InjectMocks
    private TraineeDAOImpl traineeDAO;

    private Trainee testTrainee;

    @BeforeEach
    void setUp() {
        testTrainee = TraineeBuilder.builder()
                .firstName("John")
                .lastName("Doe")
                .username("john.doe")
                .password("password123")
                .isActive(true)
                .dateOfBirth(LocalDate.of(1990, 1, 1))
                .address("123 Main St")
                .build();
        testTrainee.setId(1L);
    }

    @Test
    void save_WithValidTrainee_ShouldCallStorageSave() {
        when(traineeStorage.save(testTrainee)).thenReturn(testTrainee);

        Trainee result = traineeDAO.save(testTrainee);

        assertNotNull(result);
        assertEquals(testTrainee, result);
        verify(traineeStorage).save(testTrainee);
    }

    @Test
    void findById_WithValidId_ShouldReturnTrainee() {
        when(traineeStorage.findById(1L)).thenReturn(testTrainee);

        Trainee result = traineeDAO.findById(1L);

        assertNotNull(result);
        assertEquals(testTrainee, result);
        verify(traineeStorage).findById(1L);
    }

    @Test
    void findAll_ShouldReturnAllTrainees() {
        List<Trainee> trainees = Collections.singletonList(testTrainee);
        when(traineeStorage.findAll()).thenReturn(trainees);

        List<Trainee> result = traineeDAO.findAll();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testTrainee, result.get(0));
        verify(traineeStorage).findAll();
    }

    @Test
    void update_WithValidTrainee_ShouldCallStorageUpdate() {
        when(traineeStorage.existsById(1L)).thenReturn(true);
        when(traineeStorage.update(testTrainee)).thenReturn(testTrainee);

        Trainee result = traineeDAO.update(testTrainee);

        assertNotNull(result);
        assertEquals(testTrainee, result);
        verify(traineeStorage).existsById(1L);
        verify(traineeStorage).update(testTrainee);
    }

    @Test
    void deleteById_WithValidId_ShouldCallStorageDelete() {
        when(traineeStorage.deleteById(1L)).thenReturn(true);

        boolean result = traineeDAO.deleteById(1L);

        assertTrue(result);
        verify(traineeStorage).deleteById(1L);
    }

    @Test
    void findByUsername_WithValidUsername_ShouldReturnTrainee() {
        List<Trainee> trainees = Collections.singletonList(testTrainee);
        when(traineeStorage.findAll()).thenReturn(trainees);

        Trainee result = traineeDAO.findByUsername("john.doe");

        assertNotNull(result);
        assertEquals(testTrainee, result);
        verify(traineeStorage).findAll();
    }

    @Test
    void findByUsername_WithNonExistentUsername_ShouldReturnNull() {
        when(traineeStorage.findAll()).thenReturn(Collections.emptyList());

        Trainee result = traineeDAO.findByUsername("nonexistent");

        assertNull(result);
        verify(traineeStorage).findAll();
    }

    @Test
    void findActiveTrainees_ShouldReturnActiveTrainees() {
        List<Trainee> trainees = Collections.singletonList(testTrainee);
        when(traineeStorage.findAll()).thenReturn(trainees);

        List<Trainee> result = traineeDAO.findActiveTrainees();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testTrainee, result.get(0));
        verify(traineeStorage).findAll();
    }

    @Test
    void existsByUsername_WithValidUsername_ShouldReturnTrue() {
        List<Trainee> trainees = Collections.singletonList(testTrainee);
        when(traineeStorage.findAll()).thenReturn(trainees);

        boolean result = traineeDAO.existsByUsername("john.doe");

        assertTrue(result);
        verify(traineeStorage).findAll();
    }

    @Test
    void existsByUsername_WithNonExistentUsername_ShouldReturnFalse() {
        when(traineeStorage.findAll()).thenReturn(Collections.emptyList());

        boolean result = traineeDAO.existsByUsername("nonexistent");

        assertFalse(result);
        verify(traineeStorage).findAll();
    }
}