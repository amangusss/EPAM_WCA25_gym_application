package com.github.amangusss.gym_application.repository.impl;

import com.github.amangusss.gym_application.entity.trainer.Trainer;
import com.github.amangusss.gym_application.entity.trainer.TrainerBuilder;
import com.github.amangusss.gym_application.entity.TrainingType;
import com.github.amangusss.gym_application.storage.TrainerStorage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TrainerDAOImplTest {

    @Mock
    private TrainerStorage trainerStorage;

    @InjectMocks
    private TrainerDAOImpl trainerDAO;

    private Trainer testTrainer;

    @BeforeEach
    void setUp() {
        testTrainer = TrainerBuilder.builder()
                .firstName("Jane")
                .lastName("Smith")
                .username("jane.smith")
                .password("password123")
                .isActive(true)
                .specialization(TrainingType.FITNESS)
                .build();
        testTrainer.setId(1L);
    }

    @Test
    void save_WithValidTrainer_ShouldCallStorageSave() {
        when(trainerStorage.save(testTrainer)).thenReturn(testTrainer);

        Trainer result = trainerDAO.save(testTrainer);

        assertNotNull(result);
        assertEquals(testTrainer, result);
        verify(trainerStorage).save(testTrainer);
    }

    @Test
    void findById_WithValidId_ShouldReturnTrainer() {
        when(trainerStorage.findById(1L)).thenReturn(testTrainer);

        Trainer result = trainerDAO.findById(1L);

        assertNotNull(result);
        assertEquals(testTrainer, result);
        verify(trainerStorage).findById(1L);
    }

    @Test
    void findAll_ShouldReturnAllTrainers() {
        List<Trainer> trainers = Collections.singletonList(testTrainer);
        when(trainerStorage.findAll()).thenReturn(trainers);

        List<Trainer> result = trainerDAO.findAll();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testTrainer, result.get(0));
        verify(trainerStorage).findAll();
    }

    @Test
    void update_WithValidTrainer_ShouldCallStorageUpdate() {
        when(trainerStorage.existsById(1L)).thenReturn(true);
        when(trainerStorage.update(testTrainer)).thenReturn(testTrainer);

        Trainer result = trainerDAO.update(testTrainer);

        assertNotNull(result);
        assertEquals(testTrainer, result);
        verify(trainerStorage).existsById(1L);
        verify(trainerStorage).update(testTrainer);
    }

    @Test
    void findByUsername_WithValidUsername_ShouldReturnTrainer() {
        List<Trainer> trainers = Collections.singletonList(testTrainer);
        when(trainerStorage.findAll()).thenReturn(trainers);

        Trainer result = trainerDAO.findByUsername("jane.smith");

        assertNotNull(result);
        assertEquals(testTrainer, result);
        verify(trainerStorage).findAll();
    }

    @Test
    void findByUsername_WithNonExistentUsername_ShouldReturnNull() {
        when(trainerStorage.findAll()).thenReturn(Collections.emptyList());

        Trainer result = trainerDAO.findByUsername("nonexistent");

        assertNull(result);
        verify(trainerStorage).findAll();
    }

    @Test
    void findBySpecialization_WithValidSpecialization_ShouldReturnTrainers() {
        List<Trainer> trainers = Collections.singletonList(testTrainer);
        when(trainerStorage.findAll()).thenReturn(trainers);

        List<Trainer> result = trainerDAO.findBySpecialization(TrainingType.FITNESS);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testTrainer, result.get(0));
        verify(trainerStorage).findAll();
    }

    @Test
    void findActiveTrainers_ShouldReturnActiveTrainers() {
        List<Trainer> trainers = Collections.singletonList(testTrainer);
        when(trainerStorage.findAll()).thenReturn(trainers);

        List<Trainer> result = trainerDAO.findActiveTrainers();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testTrainer, result.get(0));
        verify(trainerStorage).findAll();
    }

    @Test
    void existsByUsername_WithValidUsername_ShouldReturnTrue() {
        List<Trainer> trainers = Collections.singletonList(testTrainer);
        when(trainerStorage.findAll()).thenReturn(trainers);

        boolean result = trainerDAO.existsByUsername("jane.smith");

        assertTrue(result);
        verify(trainerStorage).findAll();
    }

    @Test
    void existsByUsername_WithNonExistentUsername_ShouldReturnFalse() {
        when(trainerStorage.findAll()).thenReturn(Collections.emptyList());

        boolean result = trainerDAO.existsByUsername("nonexistent");

        assertFalse(result);
        verify(trainerStorage).findAll();
    }
}