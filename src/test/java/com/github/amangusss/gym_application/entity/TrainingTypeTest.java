package com.github.amangusss.gym_application.entity;

import com.github.amangusss.gym_application.exception.ValidationException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TrainingTypeTest {

    @Test
    void values_ShouldReturnAllTrainingTypes() {
        TrainingType[] types = TrainingType.values();

        assertEquals(5, types.length);
        assertTrue(java.util.Arrays.asList(types).contains(TrainingType.FITNESS));
        assertTrue(java.util.Arrays.asList(types).contains(TrainingType.YOGA));
        assertTrue(java.util.Arrays.asList(types).contains(TrainingType.ZUMBA));
        assertTrue(java.util.Arrays.asList(types).contains(TrainingType.STRETCHING));
        assertTrue(java.util.Arrays.asList(types).contains(TrainingType.RESISTANCE));
    }

    @Test
    void valueOf_WithValidName_ShouldReturnTrainingType() {
        assertEquals(TrainingType.FITNESS, TrainingType.valueOf("FITNESS"));
        assertEquals(TrainingType.YOGA, TrainingType.valueOf("YOGA"));
        assertEquals(TrainingType.ZUMBA, TrainingType.valueOf("ZUMBA"));
        assertEquals(TrainingType.STRETCHING, TrainingType.valueOf("STRETCHING"));
        assertEquals(TrainingType.RESISTANCE, TrainingType.valueOf("RESISTANCE"));
    }

    @Test
    void valueOf_WithInvalidName_ShouldThrowException() {
        assertThrows(IllegalArgumentException.class, () -> TrainingType.valueOf("INVALID"));
    }

    @Test
    void getDisplayName_ShouldReturnCorrectDisplayName() {
        assertEquals("fitness", TrainingType.FITNESS.getDisplayName());
        assertEquals("yoga", TrainingType.YOGA.getDisplayName());
        assertEquals("zumba", TrainingType.ZUMBA.getDisplayName());
        assertEquals("stretching", TrainingType.STRETCHING.getDisplayName());
        assertEquals("resistance", TrainingType.RESISTANCE.getDisplayName());
    }

    @Test
    void fromDisplayName_WithValidDisplayName_ShouldReturnTrainingType() {
        assertEquals(TrainingType.FITNESS, TrainingType.fromDisplayName("fitness"));
        assertEquals(TrainingType.YOGA, TrainingType.fromDisplayName("yoga"));
        assertEquals(TrainingType.ZUMBA, TrainingType.fromDisplayName("zumba"));
        assertEquals(TrainingType.STRETCHING, TrainingType.fromDisplayName("stretching"));
        assertEquals(TrainingType.RESISTANCE, TrainingType.fromDisplayName("resistance"));
    }

    @Test
    void fromDisplayName_WithInvalidDisplayName_ShouldThrowException() {
        assertThrows(ValidationException.class, () -> TrainingType.fromDisplayName("invalid"));
    }

    @Test
    void fromDisplayName_WithNullDisplayName_ShouldThrowException() {
        assertThrows(ValidationException.class, () -> TrainingType.fromDisplayName(null));
    }

    @Test
    void fromDisplayName_WithEmptyDisplayName_ShouldThrowException() {
        assertThrows(ValidationException.class, () -> TrainingType.fromDisplayName(""));
    }

    @Test
    void fromDisplayName_WithCaseInsensitiveDisplayName_ShouldReturnTrainingType() {
        assertEquals(TrainingType.FITNESS, TrainingType.fromDisplayName("FITNESS"));
        assertEquals(TrainingType.YOGA, TrainingType.fromDisplayName("Yoga"));
        assertEquals(TrainingType.ZUMBA, TrainingType.fromDisplayName("ZUMBA"));
    }
}