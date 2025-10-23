package com.github.amangusss.gym_application.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.amangusss.gym_application.dto.trainer.TrainerDTO;
import com.github.amangusss.gym_application.dto.training.TrainingDTO;
import com.github.amangusss.gym_application.facade.TrainerFacade;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TrainerController.class)
@DisplayName("TrainerController Tests")
class TrainerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private TrainerFacade trainerFacade;

    @Test
    @DisplayName("Should register trainer successfully")
    void registerTrainer_ShouldReturnRegisteredResponse() throws Exception {
        TrainerDTO.Request.Register request = new TrainerDTO.Request.Register(
                "Aman", "Nazarkulov", 1L
        );

        TrainerDTO.Response.Registered response = new TrainerDTO.Response.Registered(
                "Aman.Nazarkulov", "password123"
        );

        when(trainerFacade.registerTrainer(any())).thenReturn(response);

        mockMvc.perform(post("/api/trainers/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("Aman.Nazarkulov"))
                .andExpect(jsonPath("$.password").value("password123"));

        verify(trainerFacade).registerTrainer(any());
    }

    @Test
    @DisplayName("Should get trainer profile successfully")
    void getTrainerProfile_ShouldReturnProfile() throws Exception {
        TrainerDTO.Response.Profile response = new TrainerDTO.Response.Profile(
                "Aman", "Nazarkulov", "YOGA", true, Collections.emptyList()
        );

        when(trainerFacade.getTrainerProfile(anyString(), anyString())).thenReturn(response);

        mockMvc.perform(get("/api/trainers/{username}", "Aman.Nazarkulov")
                        .param("password", "password123"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("Aman"))
                .andExpect(jsonPath("$.lastName").value("Nazarkulov"))
                .andExpect(jsonPath("$.specialization").value("YOGA"));

        verify(trainerFacade).getTrainerProfile("Aman.Nazarkulov", "password123");
    }

    @Test
    @DisplayName("Should update trainer successfully")
    void updateTrainer_ShouldReturnUpdatedResponse() throws Exception {
        TrainerDTO.Request.Update request = new TrainerDTO.Request.Update(
                "Aman", "Nazarkulov", 1L, true
        );

        TrainerDTO.Response.Updated response = new TrainerDTO.Response.Updated(
                "Aman.Nazarkulov", "Aman", "Nazarkulov", "YOGA", true, Collections.emptyList()
        );

        when(trainerFacade.updateTrainer(request, any(), anyString())).thenReturn(response);

        mockMvc.perform(put("/api/trainers")
                        .param("password", "password123")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("Aman.Nazarkulov"));

        verify(trainerFacade).updateTrainer(request, any(), eq("password123"));
    }

    @Test
    @DisplayName("Should update trainer status successfully")
    void updateTrainerStatus_ShouldReturnOk() throws Exception {
        TrainerDTO.Request.UpdateStatus request = new TrainerDTO.Request.UpdateStatus(true);
        doNothing().when(trainerFacade).updateTrainerStatus(anyString(), anyBoolean(), anyString());

        mockMvc.perform(patch("/api/trainers/{username}/activate", "Aman.Nazarkulov")
                        .param("password", "password123")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        verify(trainerFacade).updateTrainerStatus("Aman.Nazarkulov", true, "password123");
    }

    @Test
    @DisplayName("Should get trainer trainings list successfully")
    void getTrainerTrainings_ShouldReturnTrainingsList() throws Exception {
        List<TrainingDTO.Response.TrainerTraining> trainings = Collections.emptyList();
        when(trainerFacade.getTrainerTrainings(anyString(), anyString(), any(), any(), anyString()))
                .thenReturn(trainings);

        mockMvc.perform(get("/api/trainers/{username}/trainings", "Aman.Nazarkulov")
                        .param("password", "password123")
                        .param("periodFrom", "2025-01-01")
                        .param("periodTo", "2025-12-31")
                        .param("traineeName", "Dastan"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());

        verify(trainerFacade).getTrainerTrainings(
                eq("Aman.Nazarkulov"), eq("password123"),
                eq(LocalDate.parse("2025-01-01")), eq(LocalDate.parse("2025-12-31")), eq("Dastan"));
    }
}
