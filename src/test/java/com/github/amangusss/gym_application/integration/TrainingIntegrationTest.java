package com.github.amangusss.gym_application.integration;

import com.github.amangusss.dto.generated.TraineeRegistrationRequest;
import com.github.amangusss.dto.generated.TraineeRegistrationResponse;
import com.github.amangusss.dto.generated.TrainingCreateRequest;
import com.github.amangusss.dto.generated.TrainerRegistrationRequest;
import com.github.amangusss.dto.generated.TrainerRegistrationResponse;
import com.github.amangusss.dto.generated.TraineeTrainingResponse;
import com.github.amangusss.dto.generated.TrainerTrainingResponse;
import com.github.amangusss.gym_application.dto.auth.AuthDTO;
import com.github.amangusss.gym_application.jms.listener.WorkloadDlqListener;
import com.github.amangusss.gym_application.jms.service.WorkloadMessageProducer;
import com.github.tomakehurst.wiremock.junit5.WireMockTest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;

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

    @MockitoBean
    private WorkloadMessageProducer workloadMessageProducer;

    @MockitoBean
    private WorkloadDlqListener workloadDlqListener;

    private String baseUrl;

    @BeforeEach
    void setUp() {
        baseUrl = "http://localhost:" + port;
    }

    private String getJwtToken(String username, String password) {
        AuthDTO.Request.Login loginRequest = new AuthDTO.Request.Login(username, password);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<AuthDTO.Request.Login> entity = new HttpEntity<>(loginRequest, headers);
        ResponseEntity<AuthDTO.Response.Login> response = restTemplate.postForEntity(
                baseUrl + "/api/auth/login", entity, AuthDTO.Response.Login.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        return response.getBody().token();
    }

    private HttpHeaders authHeaders(String token) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(token);
        return headers;
    }

    @Test
    @DisplayName("Integration Test: Create Trainee -> Create Trainer -> Create Training -> Get Trainings")
    void shouldPerformCompleteTrainingWorkflow() {
        TraineeRegistrationRequest traineeRequest = new TraineeRegistrationRequest(
                "Emma",
                "Watson",
                LocalDate.of(1992, 4, 15),
                "10 Downing Street"
        );

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<TraineeRegistrationRequest> traineeEntity = new HttpEntity<>(traineeRequest, headers);

        ResponseEntity<TraineeRegistrationResponse> traineeResponse = restTemplate.postForEntity(
                baseUrl + "/api/trainees/register",
                traineeEntity,
                TraineeRegistrationResponse.class
        );

        assertThat(traineeResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        TraineeRegistrationResponse trainee = traineeResponse.getBody();
        assertThat(trainee).isNotNull();
        System.out.println("Created trainee: " + trainee.getUsername());

        TrainerRegistrationRequest trainerRequest = new TrainerRegistrationRequest(
                "Tom",
                "Hardy",
                1L
        );

        HttpEntity<TrainerRegistrationRequest> trainerEntity = new HttpEntity<>(trainerRequest, headers);
        ResponseEntity<TrainerRegistrationResponse> trainerResponse = restTemplate.postForEntity(
                baseUrl + "/api/trainers/register",
                trainerEntity,
                TrainerRegistrationResponse.class
        );

        assertThat(trainerResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        TrainerRegistrationResponse trainer = trainerResponse.getBody();
        assertThat(trainer).isNotNull();

        String traineeJwt = getJwtToken(trainee.getUsername(), trainee.getPassword());
        String trainerJwt = getJwtToken(trainer.getUsername(), trainer.getPassword());
        HttpHeaders traineeAuth = authHeaders(traineeJwt);
        HttpHeaders trainerAuth = authHeaders(trainerJwt);

        TrainingCreateRequest trainingRequest = new TrainingCreateRequest(
                trainee.getUsername(),
                trainer.getUsername(),
                "Morning Cardio Session",
                LocalDate.now().plusDays(1),
                60.0
        );

        HttpEntity<TrainingCreateRequest> trainingEntity = new HttpEntity<>(trainingRequest, trainerAuth);
        ResponseEntity<Void> trainingCreateResponse = restTemplate.postForEntity(
                baseUrl + "/api/trainings",
                trainingEntity,
                Void.class
        );

        assertThat(trainingCreateResponse.getStatusCode()).isEqualTo(HttpStatus.OK);

        String traineeTrainingsUrl = baseUrl + "/api/trainees/" + trainee.getUsername() + "/trainings";
        ResponseEntity<TraineeTrainingResponse[]> traineeTrainingsResponse = restTemplate.exchange(
                traineeTrainingsUrl,
                HttpMethod.GET,
                new HttpEntity<>(traineeAuth),
                TraineeTrainingResponse[].class
        );

        assertThat(traineeTrainingsResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        TraineeTrainingResponse[] traineeTrainings = traineeTrainingsResponse.getBody();
        assertThat(traineeTrainings).isNotNull();
        assertThat(traineeTrainings).hasSize(1);
        assertThat(traineeTrainings[0].getTrainingName()).isEqualTo("Morning Cardio Session");
        assertThat(traineeTrainings[0].getTrainingDuration()).isEqualTo(60.0);
        System.out.println("Retrieved trainee's trainings");

        String trainerTrainingsUrl = baseUrl + "/api/trainers/" + trainer.getUsername() + "/trainings";
        ResponseEntity<TrainerTrainingResponse[]> trainerTrainingsResponse = restTemplate.exchange(
                trainerTrainingsUrl,
                HttpMethod.GET,
                new HttpEntity<>(trainerAuth),
                TrainerTrainingResponse[].class
        );

        assertThat(trainerTrainingsResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        TrainerTrainingResponse[] trainerTrainings = trainerTrainingsResponse.getBody();
        assertThat(trainerTrainings).isNotNull();
        assertThat(trainerTrainings).hasSize(1);
        assertThat(trainerTrainings[0].getTrainingName()).isEqualTo("Morning Cardio Session");
        assertThat(trainerTrainings[0].getTrainingDuration()).isEqualTo(60.0);
        System.out.println("Retrieved trainer's trainings");
    }

    @Test
    @DisplayName("Integration Test: Get Training Types")
    void shouldGetAllTrainingTypes() {
        TraineeRegistrationRequest traineeRequest = new TraineeRegistrationRequest(
                "Types",
                "Reader",
                LocalDate.of(1990, 1, 1),
                "Addr"
        );
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        ResponseEntity<TraineeRegistrationResponse> traineeResponse = restTemplate.postForEntity(
                baseUrl + "/api/trainees/register",
                new HttpEntity<>(traineeRequest, headers),
                TraineeRegistrationResponse.class
        );
        assertThat(traineeResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertNotNull(traineeResponse.getBody());
        String jwt = getJwtToken(traineeResponse.getBody().getUsername(), traineeResponse.getBody().getPassword());
        HttpHeaders auth = authHeaders(jwt);

        ResponseEntity<Object[]> response = restTemplate.exchange(
                baseUrl + "/api/training-types",
                HttpMethod.GET,
                new HttpEntity<>(auth),
                Object[].class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        Object[] trainingTypes = response.getBody();
        assertThat(trainingTypes).isNotNull();
        assertThat(trainingTypes.length).isGreaterThan(0);
        System.out.println("Retrieved all training types: " + trainingTypes.length);
    }
}
