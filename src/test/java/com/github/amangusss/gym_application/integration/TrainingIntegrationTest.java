package com.github.amangusss.gym_application.integration;

import com.github.amangusss.gym_application.dto.trainee.TraineeDTO;
import com.github.amangusss.gym_application.dto.trainer.TrainerDTO;
import com.github.amangusss.gym_application.dto.training.TrainingDTO;
import com.github.tomakehurst.wiremock.junit5.WireMockTest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@Transactional
@WireMockTest(httpPort = 8089)
@DisplayName("Training Integration Tests - Full Training Workflow with WireMock")
class TrainingIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    private String baseUrl;

    @BeforeEach
    void setUp() {
        baseUrl = "http://localhost:" + port;
    }

    @Test
    @DisplayName("Integration Test: Create Trainee -> Create Trainer -> Create Training -> Get Trainings")
    void shouldPerformCompleteTrainingWorkflow() {
        TraineeDTO.Request.Register traineeRequest = new TraineeDTO.Request.Register(
                "Emma",
                "Watson",
                LocalDate.of(1992, 4, 15),
                "10 Downing Street"
        );

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<TraineeDTO.Request.Register> traineeEntity = new HttpEntity<>(traineeRequest, headers);

        ResponseEntity<TraineeDTO.Response.Registered> traineeResponse = restTemplate.postForEntity(
                baseUrl + "/api/trainees/register",
                traineeEntity,
                TraineeDTO.Response.Registered.class
        );

        assertThat(traineeResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        TraineeDTO.Response.Registered trainee = traineeResponse.getBody();
        assertThat(trainee).isNotNull();
        System.out.println("Created trainee: " + trainee.username());

        TrainerDTO.Request.Register trainerRequest = new TrainerDTO.Request.Register(
                "Tom",
                "Hardy",
                1L
        );

        HttpEntity<TrainerDTO.Request.Register> trainerEntity = new HttpEntity<>(trainerRequest, headers);
        ResponseEntity<TrainerDTO.Response.Registered> trainerResponse = restTemplate.postForEntity(
                baseUrl + "/api/trainers/register",
                trainerEntity,
                TrainerDTO.Response.Registered.class
        );

        assertThat(trainerResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        TrainerDTO.Response.Registered trainer = trainerResponse.getBody();
        assertThat(trainer).isNotNull();
        System.out.println("Created trainer: " + trainer.username());

        TrainingDTO.Request.Create trainingRequest = new TrainingDTO.Request.Create(
                trainee.username(),
                trainer.username(),
                "Morning Cardio Session",
                LocalDate.now().plusDays(1),
                60
        );

        HttpEntity<TrainingDTO.Request.Create> trainingEntity = new HttpEntity<>(trainingRequest, headers);
        ResponseEntity<Void> trainingCreateResponse = restTemplate.postForEntity(
                baseUrl + "/api/trainings",
                trainingEntity,
                Void.class
        );

        assertThat(trainingCreateResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        System.out.println("Created training session");

        String traineeTrainingsUrl = UriComponentsBuilder.fromHttpUrl(baseUrl + "/api/trainees/{username}/trainings")
                .queryParam("password", trainee.password())
                .buildAndExpand(trainee.username())
                .toUriString();

        ResponseEntity<TrainingDTO.Response.TraineeTraining[]> traineeTrainingsResponse = restTemplate.getForEntity(
                traineeTrainingsUrl,
                TrainingDTO.Response.TraineeTraining[].class
        );

        assertThat(traineeTrainingsResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        TrainingDTO.Response.TraineeTraining[] traineeTrainings = traineeTrainingsResponse.getBody();
        assertThat(traineeTrainings).isNotNull();
        assertThat(traineeTrainings).hasSize(1);
        assertThat(traineeTrainings[0].trainingName()).isEqualTo("Morning Cardio Session");
        assertThat(traineeTrainings[0].trainingDuration()).isEqualTo(60);
        System.out.println("Retrieved trainee's trainings");

        String trainerTrainingsUrl = UriComponentsBuilder.fromHttpUrl(baseUrl + "/api/trainers/{username}/trainings")
                .queryParam("password", trainer.password())
                .buildAndExpand(trainer.username())
                .toUriString();

        ResponseEntity<TrainingDTO.Response.TrainerTraining[]> trainerTrainingsResponse = restTemplate.getForEntity(
                trainerTrainingsUrl,
                TrainingDTO.Response.TrainerTraining[].class
        );

        assertThat(trainerTrainingsResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        TrainingDTO.Response.TrainerTraining[] trainerTrainings = trainerTrainingsResponse.getBody();
        assertThat(trainerTrainings).isNotNull();
        assertThat(trainerTrainings).hasSize(1);
        assertThat(trainerTrainings[0].trainingName()).isEqualTo("Morning Cardio Session");
        assertThat(trainerTrainings[0].trainingDuration()).isEqualTo(60);
        System.out.println("Retrieved trainer's trainings");
    }

    @Test
    @DisplayName("Integration Test: Get Training Types")
    void shouldGetAllTrainingTypes() {
        ResponseEntity<Object[]> response = restTemplate.getForEntity(
                baseUrl + "/api/training-types",
                Object[].class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        Object[] trainingTypes = response.getBody();
        assertThat(trainingTypes).isNotNull();
        assertThat(trainingTypes.length).isGreaterThan(0);
        System.out.println("Retrieved all training types: " + trainingTypes.length);
    }
}
