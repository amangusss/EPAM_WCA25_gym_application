package com.github.amangusss.gym_application.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.amangusss.gym_application.dto.training.TrainingDTO;
import com.github.amangusss.gym_application.facade.TrainingFacade;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TrainingController.class)
@DisplayName("TrainingController Tests")
class TrainingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private TrainingFacade trainingFacade;

    @Test
    @DisplayName("Should add training successfully")
    void addTraining_ShouldReturnOk() throws Exception {
        TrainingDTO.Request.Create request = new TrainingDTO.Request.Create(
                "Aman.Nazarkulov",
                "Dastan.Ibraimov",
                "Morning Yoga",
                LocalDate.now().plusDays(1),
                60
        );

        doNothing().when(trainingFacade).addTraining(any());

        mockMvc.perform(post("/api/trainings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        verify(trainingFacade).addTraining(any());
    }

    @Test
    @DisplayName("Should return 400 when request is invalid")
    void addTraining_WithInvalidRequest_ShouldReturnBadRequest() throws Exception {
        TrainingDTO.Request.Create request = new TrainingDTO.Request.Create(
                null,
                "Dastan.Ibraimov",
                "Morning Yoga",
                LocalDate.now().plusDays(1),
                60
        );

        mockMvc.perform(post("/api/trainings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verify(trainingFacade, never()).addTraining(any());
    }
}
