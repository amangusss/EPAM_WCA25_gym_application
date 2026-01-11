package com.github.amangusss.gym_application.integration;

import com.github.amangusss.gym_application.dto.trainee.TraineeDTO;
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
import static org.springframework.web.util.UriComponentsBuilder.fromUriString;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@Transactional
@WireMockTest(httpPort = 8089)
@DisplayName("Trainee Integration Tests - Full CRUD Operations with WireMock")
class TraineeIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    private String baseUrl;

    @MockitoBean
    private WorkloadMessageProducer workloadMessageProducer;

    @MockitoBean
    private WorkloadDlqListener workloadDlqListener;

    @BeforeEach
    void setUp() {
        baseUrl = "http://localhost:" + port;
    }

    private String getJwtToken(String username, String password) {
        AuthDTO.Request.Login loginRequest = new AuthDTO.Request.Login(username, password);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<AuthDTO.Request.Login> loginEntity = new HttpEntity<>(loginRequest, headers);

        ResponseEntity<AuthDTO.Response.Login> loginResponse = restTemplate.postForEntity(
                baseUrl + "/api/auth/login",
                loginEntity,
                AuthDTO.Response.Login.class
        );

        assertThat(loginResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(loginResponse.getBody()).isNotNull();
        return loginResponse.getBody().token();
    }

    private HttpHeaders createAuthHeaders(String jwtToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + jwtToken);
        return headers;
    }

    @Test
    @DisplayName("Integration Test: Create Trainee -> Get Profile -> Update -> Delete")
    void shouldPerformFullTraineeCrudCycle() {
        TraineeDTO.Request.Register registerRequest = new TraineeDTO.Request.Register(
                "John",
                "Doe",
                LocalDate.of(1990, 1, 1),
                "123 Main Street"
        );

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<TraineeDTO.Request.Register> registerEntity = new HttpEntity<>(registerRequest, headers);

        ResponseEntity<TraineeDTO.Response.Registered> registerResponse = restTemplate.postForEntity(
                baseUrl + "/api/trainees/register",
                registerEntity,
                TraineeDTO.Response.Registered.class
        );

        assertThat(registerResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        TraineeDTO.Response.Registered registered = registerResponse.getBody();
        assertThat(registered).isNotNull();
        assertThat(registered.username()).isNotNull();
        assertThat(registered.password()).isNotNull();

        String createdUsername = registered.username();
        String createdPassword = registered.password();

        String jwtToken = getJwtToken(createdUsername, createdPassword);
        HttpHeaders authHeaders = createAuthHeaders(jwtToken);

        String getProfileUrl = fromUriString(baseUrl + "/api/trainees/{username}")
                .queryParam("password", createdPassword)
                .buildAndExpand(createdUsername)
                .toUriString();

        ResponseEntity<TraineeDTO.Response.Profile> profileResponse = restTemplate.exchange(
                getProfileUrl,
                HttpMethod.GET,
                new HttpEntity<>(authHeaders),
                TraineeDTO.Response.Profile.class
        );

        assertThat(profileResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        TraineeDTO.Response.Profile profile = profileResponse.getBody();
        assertThat(profile).isNotNull();
        assertThat(profile.firstName()).isEqualTo("John");
        assertThat(profile.lastName()).isEqualTo("Doe");
        assertThat(profile.dateOfBirth()).isEqualTo(LocalDate.of(1990, 1, 1));
        assertThat(profile.address()).isEqualTo("123 Main Street");
        assertThat(profile.isActive()).isTrue();

        TraineeDTO.Request.Update updateRequest = new TraineeDTO.Request.Update(
                "Johnny",
                "Doeson",
                LocalDate.of(1990, 1, 1),
                "456 New Avenue",
                true
        );

        String updateUrl = fromUriString(baseUrl + "/api/trainees/{username}")
                .queryParam("password", createdPassword)
                .buildAndExpand(createdUsername)
                .toUriString();

        HttpEntity<TraineeDTO.Request.Update> updateEntity = new HttpEntity<>(updateRequest, authHeaders);
        ResponseEntity<TraineeDTO.Response.Profile> updateResponse = restTemplate.exchange(
                updateUrl,
                HttpMethod.PUT,
                updateEntity,
                TraineeDTO.Response.Profile.class
        );

        assertThat(updateResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        TraineeDTO.Response.Profile updatedProfile = updateResponse.getBody();
        assertThat(updatedProfile).isNotNull();
        assertThat(updatedProfile.firstName()).isEqualTo("Johnny");
        assertThat(updatedProfile.lastName()).isEqualTo("Doeson");

        String deleteUrl = fromUriString(baseUrl + "/api/trainees/{username}")
                .queryParam("password", createdPassword)
                .buildAndExpand(createdUsername)
                .toUriString();

        ResponseEntity<Void> deleteResponse = restTemplate.exchange(
                deleteUrl,
                HttpMethod.DELETE,
                new HttpEntity<>(authHeaders),
                Void.class
        );

        assertThat(deleteResponse.getStatusCode()).isEqualTo(HttpStatus.OK);

        ResponseEntity<Object> verifyResponse = restTemplate.exchange(
                getProfileUrl,
                HttpMethod.GET,
                new HttpEntity<>(authHeaders),
                Object.class
        );
        assertThat(verifyResponse.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    @DisplayName("Integration Test: Authentication and Password Change Flow")
    void shouldAuthenticateAndChangePassword() {
        TraineeDTO.Request.Register registerRequest = new TraineeDTO.Request.Register(
                "Bob",
                "Wilson",
                LocalDate.of(1988, 3, 20),
                "321 Gym Street"
        );

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<TraineeDTO.Request.Register> registerEntity = new HttpEntity<>(registerRequest, headers);

        ResponseEntity<TraineeDTO.Response.Registered> registerResponse = restTemplate.postForEntity(
                baseUrl + "/api/trainees/register",
                registerEntity,
                TraineeDTO.Response.Registered.class
        );

        assertThat(registerResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        TraineeDTO.Response.Registered registered = registerResponse.getBody();
        assertThat(registered).isNotNull();

        String username = registered.username();
        String oldPassword = registered.password();

        String jwtToken = getJwtToken(username, oldPassword);
        assertThat(jwtToken).isNotNull();

        String newPassword = "newSecurePassword123";
        AuthDTO.Request.ChangePassword changePasswordRequest =
                new AuthDTO.Request.ChangePassword(oldPassword, newPassword);

        HttpHeaders authHeaders = createAuthHeaders(jwtToken);
        HttpEntity<AuthDTO.Request.ChangePassword> changePasswordEntity =
                new HttpEntity<>(changePasswordRequest, authHeaders);

        ResponseEntity<Void> changePasswordResponse = restTemplate.exchange(
                baseUrl + "/api/auth/change-password",
                HttpMethod.PUT,
                changePasswordEntity,
                Void.class
        );

        assertThat(changePasswordResponse.getStatusCode()).isEqualTo(HttpStatus.OK);

        String newJwtToken = getJwtToken(username, newPassword);
        assertThat(newJwtToken).isNotNull();

        String getProfileUrl = fromUriString(baseUrl + "/api/trainees/{username}")
                .queryParam("password", newPassword)
                .buildAndExpand(username)
                .toUriString();

        HttpHeaders newAuthHeaders = createAuthHeaders(newJwtToken);
        ResponseEntity<TraineeDTO.Response.Profile> profileResponse = restTemplate.exchange(
                getProfileUrl,
                HttpMethod.GET,
                new HttpEntity<>(newAuthHeaders),
                TraineeDTO.Response.Profile.class
        );

        assertThat(profileResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        TraineeDTO.Response.Profile profile = profileResponse.getBody();
        assertThat(profile).isNotNull();
        assertThat(profile.firstName()).isEqualTo("Bob");
        assertThat(profile.lastName()).isEqualTo("Wilson");
    }

    @Test
    @DisplayName("Integration Test: Manage Trainee Trainers Relationship")
    void shouldManageTraineeTrainersRelationship() {
        TraineeDTO.Request.Register traineeRequest = new TraineeDTO.Request.Register(
                "Alice",
                "Smith",
                LocalDate.of(1995, 5, 15),
                "789 Fitness Lane"
        );

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<TraineeDTO.Request.Register> registerEntity = new HttpEntity<>(traineeRequest, headers);

        ResponseEntity<TraineeDTO.Response.Registered> registerResponse = restTemplate.postForEntity(
                baseUrl + "/api/trainees/register",
                registerEntity,
                TraineeDTO.Response.Registered.class
        );

        assertThat(registerResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        TraineeDTO.Response.Registered trainee = registerResponse.getBody();
        assertThat(trainee).isNotNull();
        System.out.println("Created trainee: " + trainee.username());

        String jwtToken = getJwtToken(trainee.username(), trainee.password());
        HttpHeaders authHeaders = createAuthHeaders(jwtToken);

        String unassignedUrl = fromUriString(baseUrl + "/api/trainees/{username}/trainers/unassigned")
                .buildAndExpand(trainee.username())
                .toUriString();

        ResponseEntity<Object[]> unassignedResponse = restTemplate.exchange(
                unassignedUrl,
                HttpMethod.GET,
                new HttpEntity<>(authHeaders),
                Object[].class
        );
        assertThat(unassignedResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        System.out.println("Retrieved unassigned trainers");
    }
}
