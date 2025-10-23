package com.github.amangusss.gym_application.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.amangusss.gym_application.dto.trainee.TraineeDTO;
import com.github.amangusss.gym_application.dto.trainer.TrainerDTO;
import com.github.amangusss.gym_application.dto.training.TrainingDTO;
import com.github.amangusss.gym_application.facade.TraineeFacade;
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

@WebMvcTest(TraineeController.class)
@DisplayName("TraineeController Tests")
class TraineeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private TraineeFacade traineeFacade;

    @Test
    @DisplayName("Should register trainee successfully")
    void registerTrainee_ShouldReturnRegisteredResponse() throws Exception {
        TraineeDTO.Request.Register request = new TraineeDTO.Request.Register(
                "Dastan", "Ibraimov", LocalDate.of(2004, 8, 14), "Panfilov St, 46A"
        );

        TraineeDTO.Response.Registered response = new TraineeDTO.Response.Registered(
                "Dastan.Ibraimov", "password123"
        );

        when(traineeFacade.registerTrainee(any())).thenReturn(response);

        mockMvc.perform(post("/api/trainees/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("Dastan.Ibraimov"))
                .andExpect(jsonPath("$.password").value("password123"));

        verify(traineeFacade).registerTrainee(any());
    }

    @Test
    @DisplayName("Should get trainee profile successfully")
    void getTraineeProfile_ShouldReturnProfile() throws Exception {
        TraineeDTO.Response.Profile response = new TraineeDTO.Response.Profile(
                "Dastan", "Ibraimov", LocalDate.of(2004, 8, 14),
                "Panfilov St, 46A", true, Collections.emptyList()
        );

        when(traineeFacade.getTraineeProfile(anyString(), anyString())).thenReturn(response);

        mockMvc.perform(get("/api/trainees/{username}", "Dastan.Ibraimov")
                        .param("password", "password123"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("Dastan"))
                .andExpect(jsonPath("$.lastName").value("Ibraimov"));

        verify(traineeFacade).getTraineeProfile("Dastan.Ibraimov", "password123");
    }

    @Test
    @DisplayName("Should update trainee successfully")
    void updateTrainee_ShouldReturnUpdatedResponse() throws Exception {
        TraineeDTO.Request.Update request = new TraineeDTO.Request.Update(
                "Dastan", "Ibraimov",
                LocalDate.of(2004, 8, 14), "New Address", true
        );

        TraineeDTO.Response.Updated response = new TraineeDTO.Response.Updated(
                "Dastan.Ibraimov", "Dastan", "Ibraimov",
                LocalDate.of(2004, 8, 14), "New Address", true, Collections.emptyList()
        );

        when(traineeFacade.updateTrainee(request, any(), anyString())).thenReturn(response);

        mockMvc.perform(put("/api/trainees")
                        .param("password", "password123")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("Dastan.Ibraimov"));

        verify(traineeFacade).updateTrainee(request, any(), eq("password123"));
    }

    @Test
    @DisplayName("Should delete trainee successfully")
    void deleteTrainee_ShouldReturnOk() throws Exception {
        doNothing().when(traineeFacade).deleteTrainee(anyString(), anyString());

        mockMvc.perform(delete("/api/trainees/{username}", "Dastan.Ibraimov")
                        .param("password", "password123"))
                .andExpect(status().isOk());

        verify(traineeFacade).deleteTrainee("Dastan.Ibraimov", "password123");
    }

    @Test
    @DisplayName("Should update trainee status successfully")
    void updateTraineeStatus_ShouldReturnOk() throws Exception {
        TraineeDTO.Request.UpdateStatus request = new TraineeDTO.Request.UpdateStatus(anyString(), false);
        doNothing().when(traineeFacade).updateTraineeStatus(anyString(), anyBoolean(), anyString());

        mockMvc.perform(patch("/api/trainees/{username}/activate", "Dastan.Ibraimov")
                        .param("password", "password123")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        verify(traineeFacade).updateTraineeStatus("Dastan.Ibraimov", false, "password123");
    }

    @Test
    @DisplayName("Should get trainee trainings list successfully")
    void getTraineeTrainings_ShouldReturnTrainingsList() throws Exception {
        List<TrainingDTO.Response.TraineeTraining> trainings = Collections.emptyList();
        when(traineeFacade.getTraineeTrainings(anyString(), anyString(), any(), any(), anyString(), anyString()))
                .thenReturn(trainings);

        mockMvc.perform(get("/api/trainees/{username}/trainings", "Dastan.Ibraimov")
                        .param("password", "password123")
                        .param("periodFrom", "2025-01-01")
                        .param("periodTo", "2025-12-31")
                        .param("trainerName", "Aman")
                        .param("trainingType", "YOGA"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());

        verify(traineeFacade).getTraineeTrainings(
                eq("Dastan.Ibraimov"), eq("password123"),
                eq(LocalDate.parse("2025-01-01")), eq(LocalDate.parse("2025-12-31")),
                eq("Aman"), eq("YOGA"));
    }

    @Test
    @DisplayName("Should get unassigned trainers successfully")
    void getUnassignedTrainers_ShouldReturnTrainersList() throws Exception {
        List<TrainerDTO.Response.Unassigned> trainers = Collections.emptyList();
        when(traineeFacade.getUnassignedTrainers(anyString(), anyString())).thenReturn(trainers);

        mockMvc.perform(get("/api/trainees/{username}/trainers/unassigned", "Dastan.Ibraimov")
                        .param("password", "password123"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());

        verify(traineeFacade).getUnassignedTrainers("Dastan.Ibraimov", "password123");
    }

    @Test
    @DisplayName("Should update trainee trainers list successfully")
    void updateTraineeTrainers_ShouldReturnTrainersList() throws Exception {
        TraineeDTO.Request.UpdateTrainers request = new TraineeDTO.Request.UpdateTrainers(
                "Dastan.Ibraimov",
                List.of("Aman.Nazarkulov")
        );

        List<TrainerDTO.Response.InList> response = Collections.emptyList();
        when(traineeFacade.updateTraineeTrainers(anyString(), any(), anyString())).thenReturn(response);

        mockMvc.perform(put("/api/trainees/{username}/trainers", "Dastan.Ibraimov")
                        .param("password", "password123")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());

        verify(traineeFacade).updateTraineeTrainers(eq("Dastan.Ibraimov"), any(), eq("password123"));
    }
}
