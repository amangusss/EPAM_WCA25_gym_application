package com.github.amangusss.gym_application.service;

import com.github.amangusss.gym_application.dto.trainingType.TrainingTypeDTO;
import com.github.amangusss.gym_application.entity.TrainingType;
import com.github.amangusss.gym_application.exception.TrainingTypeNotFoundException;
import com.github.amangusss.gym_application.mapper.TrainingTypeMapper;
import com.github.amangusss.gym_application.repository.TrainingTypeRepository;
import com.github.amangusss.gym_application.service.impl.TrainingTypeServiceImpl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("TrainingTypeService Tests")
class TrainingTypeServiceTest {

    @Mock
    private TrainingTypeRepository trainingTypeRepository;

    @Mock
    private TrainingTypeMapper trainingTypeMapper;

    @InjectMocks
    private TrainingTypeServiceImpl trainingTypeService;

    private TrainingType fitnessType;
    private TrainingType yogaType;
    private TrainingTypeDTO.Response.TrainingType fitnessResponse;
    private TrainingTypeDTO.Response.TrainingType yogaResponse;

    @BeforeEach
    void setUp() {
        fitnessType = TrainingType.builder()
                .id(1L)
                .typeName("FITNESS")
                .build();

        yogaType = TrainingType.builder()
                .id(2L)
                .typeName("YOGA")
                .build();

        fitnessResponse = new TrainingTypeDTO.Response.TrainingType(1L, "FITNESS");
        yogaResponse = new TrainingTypeDTO.Response.TrainingType(2L, "YOGA");
    }

    @Nested
    @DisplayName("FindById Tests")
    class FindByIdTests {

        @Test
        @DisplayName("Should find training type by id successfully")
        void shouldFindTrainingTypeById() {
            when(trainingTypeRepository.findById(1L)).thenReturn(Optional.of(fitnessType));

            TrainingType result = trainingTypeService.findById(1L);

            assertThat(result).isNotNull();
            assertThat(result.getId()).isEqualTo(1L);
            assertThat(result.getTypeName()).isEqualTo("FITNESS");

            verify(trainingTypeRepository).findById(1L);
        }

        @Test
        @DisplayName("Should throw TrainingTypeNotFoundException when id not found")
        void shouldThrowNotFoundExceptionWhenIdNotFound() {
            when(trainingTypeRepository.findById(999L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> trainingTypeService.findById(999L))
                    .isInstanceOf(TrainingTypeNotFoundException.class);

            verify(trainingTypeRepository).findById(999L);
        }
    }

    @Nested
    @DisplayName("GetAllTrainingTypes Tests")
    class GetAllTrainingTypesTests {

        @Test
        @DisplayName("Should return all training types")
        void shouldReturnAllTrainingTypes() {
            List<TrainingType> trainingTypes = Arrays.asList(fitnessType, yogaType);
            when(trainingTypeRepository.findAll()).thenReturn(trainingTypes);
            when(trainingTypeMapper.toResponse(fitnessType)).thenReturn(fitnessResponse);
            when(trainingTypeMapper.toResponse(yogaType)).thenReturn(yogaResponse);

            List<TrainingTypeDTO.Response.TrainingType> result = trainingTypeService.getAllTrainingTypes();

            assertThat(result).hasSize(2);
            assertThat(result).extracting("typeName")
                    .containsExactly("FITNESS", "YOGA");

            verify(trainingTypeRepository).findAll();
        }

        @Test
        @DisplayName("Should return empty list when no training types exist")
        void shouldReturnEmptyListWhenNoTrainingTypes() {
            when(trainingTypeRepository.findAll()).thenReturn(Collections.emptyList());

            List<TrainingTypeDTO.Response.TrainingType> result = trainingTypeService.getAllTrainingTypes();

            assertThat(result).isEmpty();
            verify(trainingTypeRepository).findAll();
        }
    }
}
