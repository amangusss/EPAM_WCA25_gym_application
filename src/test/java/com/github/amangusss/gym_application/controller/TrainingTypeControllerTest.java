package com.github.amangusss.gym_application.controller;

import com.github.amangusss.gym_application.dto.trainingtype.TrainingTypeDTO;
import com.github.amangusss.gym_application.metrics.ApiPerformanceMetrics;
import com.github.amangusss.gym_application.metrics.TrainingTypeMetrics;
import com.github.amangusss.gym_application.service.TrainingTypeService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(TrainingTypeController.class)
@DisplayName("TrainingTypeController Tests")
class TrainingTypeControllerTest {

    private static final Long YOGA_ID = 1L;
    private static final String YOGA_NAME = "YOGA";
    private static final Long FITNESS_ID = 2L;
    private static final String FITNESS_NAME = "FITNESS";
    private static final Long CARDIO_ID = 3L;
    private static final String CARDIO_NAME = "CARDIO";
    private static final int EXPECTED_TRAINING_TYPES_COUNT = 3;

    private static final String TRAINING_TYPES_ENDPOINT = "/api/training-types";

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private TrainingTypeService trainingTypeService;

    @MockitoBean
    private TrainingTypeMetrics trainingTypeMetrics;

    @MockitoBean
    private ApiPerformanceMetrics apiPerformanceMetrics;

    @BeforeEach
    void setUp() {
        reset(trainingTypeService, trainingTypeMetrics, apiPerformanceMetrics);
    }

    @Test
    @DisplayName("Should return 200 OK and list of training types when getting all training types")
    void shouldReturnOkAndListOfTrainingTypesWhenGettingAllTrainingTypes() throws Exception {
        List<TrainingTypeDTO.Response.TrainingType> expectedTrainingTypes = createTrainingTypesList();
        when(trainingTypeService.getAllTrainingTypes()).thenReturn(expectedTrainingTypes);

        mockMvc.perform(get(TRAINING_TYPES_ENDPOINT))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(EXPECTED_TRAINING_TYPES_COUNT))
                .andExpect(jsonPath("$[0].id").value(YOGA_ID))
                .andExpect(jsonPath("$[0].typeName").value(YOGA_NAME))
                .andExpect(jsonPath("$[1].id").value(FITNESS_ID))
                .andExpect(jsonPath("$[1].typeName").value(FITNESS_NAME))
                .andExpect(jsonPath("$[2].id").value(CARDIO_ID))
                .andExpect(jsonPath("$[2].typeName").value(CARDIO_NAME));

        verify(trainingTypeService, times(1)).getAllTrainingTypes();
    }

    @Test
    @DisplayName("Should return 200 OK and empty list when no training types exist")
    void shouldReturnOkAndEmptyListWhenNoTrainingTypesExist() throws Exception {
        List<TrainingTypeDTO.Response.TrainingType> emptyList = Collections.emptyList();
        when(trainingTypeService.getAllTrainingTypes()).thenReturn(emptyList);

        mockMvc.perform(get(TRAINING_TYPES_ENDPOINT))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(0));

        verify(trainingTypeService, times(1)).getAllTrainingTypes();
    }

    @Test
    @DisplayName("Should return 200 OK and single training type when only one type exists")
    void shouldReturnOkAndSingleTrainingTypeWhenOnlyOneTypeExists() throws Exception {
        List<TrainingTypeDTO.Response.TrainingType> singleTypeList = List.of(
                createTrainingType(YOGA_ID, YOGA_NAME)
        );
        when(trainingTypeService.getAllTrainingTypes()).thenReturn(singleTypeList);

        mockMvc.perform(get(TRAINING_TYPES_ENDPOINT))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(YOGA_ID))
                .andExpect(jsonPath("$[0].typeName").value(YOGA_NAME));

        verify(trainingTypeService, times(1)).getAllTrainingTypes();
    }


    private List<TrainingTypeDTO.Response.TrainingType> createTrainingTypesList() {
        return Arrays.asList(
                createTrainingType(YOGA_ID, YOGA_NAME),
                createTrainingType(FITNESS_ID, FITNESS_NAME),
                createTrainingType(CARDIO_ID, CARDIO_NAME)
        );
    }

    private TrainingTypeDTO.Response.TrainingType createTrainingType(Long id, String name) {
        return new TrainingTypeDTO.Response.TrainingType(id, name);
    }
}
