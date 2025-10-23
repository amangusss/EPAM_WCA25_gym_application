package com.github.amangusss.gym_application.controller;

import com.github.amangusss.gym_application.dto.trainingtype.TrainingTypeDTO;
import com.github.amangusss.gym_application.facade.TrainingTypeFacade;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TrainingTypeController.class)
@DisplayName("TrainingTypeController Tests")
class TrainingTypeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TrainingTypeFacade trainingTypeFacade;

    @Test
    @DisplayName("Should get all training types successfully")
    void getAllTrainingTypes_ShouldReturnList() throws Exception {
        List<TrainingTypeDTO.Response.TrainingType> trainingTypes = Arrays.asList(
                new TrainingTypeDTO.Response.TrainingType(1L, "YOGA"),
                new TrainingTypeDTO.Response.TrainingType(2L, "FITNESS"),
                new TrainingTypeDTO.Response.TrainingType(3L, "CARDIO")
        );

        when(trainingTypeFacade.getAllTrainingTypes()).thenReturn(trainingTypes);

        mockMvc.perform(get("/api/training-types"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(3))
                .andExpect(jsonPath("$[0].trainingTypeName").value("YOGA"))
                .andExpect(jsonPath("$[1].trainingTypeName").value("FITNESS"))
                .andExpect(jsonPath("$[2].trainingTypeName").value("CARDIO"));

        verify(trainingTypeFacade).getAllTrainingTypes();
    }

    @Test
    @DisplayName("Should return empty list when no training types exist")
    void getAllTrainingTypes_WhenEmpty_ShouldReturnEmptyList() throws Exception {
        when(trainingTypeFacade.getAllTrainingTypes()).thenReturn(List.of());

        mockMvc.perform(get("/api/training-types"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(0));

        verify(trainingTypeFacade).getAllTrainingTypes();
    }
}
