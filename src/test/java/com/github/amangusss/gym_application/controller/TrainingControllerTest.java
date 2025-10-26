package com.github.amangusss.gym_application.controller;

import com.github.amangusss.gym_application.dto.training.TrainingDTO;
import com.github.amangusss.gym_application.facade.TrainingFacade;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(TrainingController.class)
@DisplayName("TrainingController Tests")
class TrainingControllerTest {

    private static final String TRAINER_USERNAME = "Aman.Nazarkulov";
    private static final String TRAINEE_USERNAME = "Dastan.Ibraimov";
    private static final String TRAINING_NAME = "Morning Yoga";
    private static final LocalDate TRAINING_DATE = LocalDate.of(2025, 10, 28);
    private static final int TRAINING_DURATION = 60;

    private static final String TRAININGS_ENDPOINT = "/api/trainings";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private TrainingFacade trainingFacade;

    @BeforeEach
    void setUp() {
        reset(trainingFacade);
    }

    @Test
    @DisplayName("Should return 200 OK when adding training successfully")
    void shouldReturnOkWhenAddingTrainingSuccessfully() throws Exception {
        TrainingDTO.Request.Create createRequest = createValidTrainingRequest();
        doNothing().when(trainingFacade).addTraining(any(TrainingDTO.Request.Create.class));

        mockMvc.perform(post(TRAININGS_ENDPOINT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isOk());

        verify(trainingFacade, times(1)).addTraining(any(TrainingDTO.Request.Create.class));
    }

    @Test
    @DisplayName("Should return 400 Bad Request when trainer username is null")
    void shouldReturnBadRequestWhenTrainerUsernameIsNull() throws Exception {
        TrainingDTO.Request.Create invalidRequest = createTrainingRequestWithNullTrainerUsername();

        mockMvc.perform(post(TRAININGS_ENDPOINT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());

        verify(trainingFacade, never()).addTraining(any(TrainingDTO.Request.Create.class));
    }

    @Test
    @DisplayName("Should return 400 Bad Request when trainee username is null")
    void shouldReturnBadRequestWhenTraineeUsernameIsNull() throws Exception {
        TrainingDTO.Request.Create invalidRequest = createTrainingRequestWithNullTraineeUsername();

        mockMvc.perform(post(TRAININGS_ENDPOINT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());

        verify(trainingFacade, never()).addTraining(any(TrainingDTO.Request.Create.class));
    }

    private TrainingDTO.Request.Create createValidTrainingRequest() {
        return new TrainingDTO.Request.Create(
                TRAINER_USERNAME,
                TRAINEE_USERNAME,
                TRAINING_NAME,
                TRAINING_DATE,
                TRAINING_DURATION
        );
    }

    private TrainingDTO.Request.Create createTrainingRequestWithNullTrainerUsername() {
        return new TrainingDTO.Request.Create(
                null,
                TRAINEE_USERNAME,
                TRAINING_NAME,
                TRAINING_DATE,
                TRAINING_DURATION
        );
    }

    private TrainingDTO.Request.Create createTrainingRequestWithNullTraineeUsername() {
        return new TrainingDTO.Request.Create(
                TRAINER_USERNAME,
                null,
                TRAINING_NAME,
                TRAINING_DATE,
                TRAINING_DURATION
        );
    }
}
