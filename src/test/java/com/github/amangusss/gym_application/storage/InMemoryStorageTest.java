package com.github.amangusss.gym_application.storage;

import com.github.amangusss.gym_application.entity.trainee.Trainee;
import com.github.amangusss.gym_application.exception.ValidationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryStorageTest {

    private TraineeStorage storage;

    @BeforeEach
    void setUp() {
        storage = new TraineeStorage();
    }

    @Test
    void save_WithNewEntity_ShouldGenerateIdAndSave() {
        Trainee trainee = new Trainee();
        trainee.setFirstName("John");
        trainee.setLastName("Doe");
        trainee.setDateOfBirth(LocalDate.of(1990, 5, 15));
        trainee.setAddress("123 Main St");

        Trainee savedTrainee = storage.save(trainee);

        assertNotNull(savedTrainee.getId());
        assertEquals(1L, savedTrainee.getId());
        assertEquals("John", savedTrainee.getFirstName());
    }

    @Test
    void save_WithExistingId_ShouldUpdateEntity() {
        Trainee trainee = new Trainee();
        trainee.setId(1L);
        trainee.setFirstName("John");
        trainee.setLastName("Doe");

        Trainee savedTrainee = storage.save(trainee);

        assertEquals(1L, savedTrainee.getId());
    }

    @Test
    void save_WithNullEntity_ShouldThrowException() {
        assertThrows(ValidationException.class, () -> storage.save(null));
    }

    @Test
    void findById_WithExistingId_ShouldReturnEntity() {
        Trainee trainee = new Trainee();
        trainee.setFirstName("John");
        trainee.setLastName("Doe");
        Trainee savedTrainee = storage.save(trainee);
        Long id = savedTrainee.getId();

        Trainee foundTrainee = storage.findById(id);

        assertNotNull(foundTrainee);
        assertEquals(id, foundTrainee.getId());
        assertEquals("John", foundTrainee.getFirstName());
    }

    @Test
    void findById_WithNonExistingId_ShouldReturnNull() {
        Trainee foundTrainee = storage.findById(999L);

        assertNull(foundTrainee);
    }

    @Test
    void findById_WithNullId_ShouldReturnNull() {
        Trainee foundTrainee = storage.findById(null);

        assertNull(foundTrainee);
    }

    @Test
    void findAll_WithEmptyStorage_ShouldReturnEmptyList() {
        List<Trainee> trainees = storage.findAll();

        assertNotNull(trainees);
        assertTrue(trainees.isEmpty());
    }

    @Test
    void findAll_WithEntities_ShouldReturnAllEntities() {
        Trainee trainee1 = new Trainee();
        trainee1.setFirstName("John");
        trainee1.setLastName("Doe");
        storage.save(trainee1);

        Trainee trainee2 = new Trainee();
        trainee2.setFirstName("Jane");
        trainee2.setLastName("Smith");
        storage.save(trainee2);

        List<Trainee> trainees = storage.findAll();

        assertNotNull(trainees);
        assertEquals(2, trainees.size());
    }

    @Test
    void update_WithExistingEntity_ShouldUpdateEntity() {
        Trainee trainee = new Trainee();
        trainee.setFirstName("John");
        trainee.setLastName("Doe");
        Trainee savedTrainee = storage.save(trainee);
        Long id = savedTrainee.getId();

        Trainee updatedTrainee = new Trainee();
        updatedTrainee.setId(id);
        updatedTrainee.setFirstName("Johnny");
        updatedTrainee.setLastName("Doe");

        Trainee result = storage.update(updatedTrainee);

        assertEquals(id, result.getId());
        assertEquals("Johnny", result.getFirstName());
    }

    @Test
    void update_WithNonExistingEntity_ShouldThrowException() {
        Trainee trainee = new Trainee();
        trainee.setId(999L);
        trainee.setFirstName("John");
        trainee.setLastName("Doe");

        assertThrows(ValidationException.class, () -> storage.update(trainee));
    }

    @Test
    void update_WithNullEntity_ShouldThrowException() {
        assertThrows(ValidationException.class, () -> storage.update(null));
    }

    @Test
    void deleteById_WithExistingId_ShouldReturnTrue() {
        Trainee trainee = new Trainee();
        trainee.setFirstName("John");
        trainee.setLastName("Doe");
        Trainee savedTrainee = storage.save(trainee);
        Long id = savedTrainee.getId();

        boolean result = storage.deleteById(id);

        assertTrue(result);
        assertNull(storage.findById(id));
    }

    @Test
    void deleteById_WithNonExistingId_ShouldReturnFalse() {
        boolean result = storage.deleteById(999L);

        assertFalse(result);
    }

    @Test
    void deleteById_WithNullId_ShouldReturnFalse() {
        boolean result = storage.deleteById(null);

        assertFalse(result);
    }

    @Test
    void existsById_WithExistingId_ShouldReturnTrue() {
        Trainee trainee = new Trainee();
        trainee.setFirstName("John");
        trainee.setLastName("Doe");
        Trainee savedTrainee = storage.save(trainee);
        Long id = savedTrainee.getId();

        boolean exists = storage.existsById(id);

        assertTrue(exists);
    }

    @Test
    void existsById_WithNonExistingId_ShouldReturnFalse() {
        boolean exists = storage.existsById(999L);

        assertFalse(exists);
    }

    @Test
    void generateNextId_ShouldReturnIncrementalIds() {
        Long id1 = storage.generateNextId();
        Long id2 = storage.generateNextId();
        Long id3 = storage.generateNextId();

        assertEquals(1L, id1);
        assertEquals(2L, id2);
        assertEquals(3L, id3);
    }
}