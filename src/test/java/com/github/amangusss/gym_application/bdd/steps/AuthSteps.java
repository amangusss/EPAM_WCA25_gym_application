package com.github.amangusss.gym_application.bdd.steps;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.amangusss.gym_application.bdd.context.SharedTestContext;
import com.github.amangusss.gym_application.entity.CustomUser;
import com.github.amangusss.gym_application.entity.trainee.Trainee;
import com.github.amangusss.gym_application.repository.TraineeRepository;
import com.github.amangusss.gym_application.repository.UserRepository;
import com.github.amangusss.gym_application.service.BruteForceProtectionService;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@SuppressWarnings("unused")
public class AuthSteps {

    SharedTestContext context;
    MockMvc mockMvc;
    ObjectMapper objectMapper;
    UserRepository userRepository;
    TraineeRepository traineeRepository;
    PasswordEncoder passwordEncoder;
    BruteForceProtectionService bruteForceProtectionService;

    @Given("the system is ready")
    public void theSystemIsReady() {
        log.info("System is ready for testing");
    }

    @Given("a trainee exists with username {string} and password {string}")
    public void aTraineeExistsWithUsernameAndPassword(String username, String password) {
        CustomUser user = CustomUser.builder()
                .firstName(username.split("\\.")[0])
                .lastName(username.contains(".") ? username.split("\\.")[1] : "User")
                .username(username)
                .password(passwordEncoder.encode(password))
                .isActive(true)
                .build();

        Trainee trainee = Trainee.builder()
                .user(user)
                .build();

        traineeRepository.save(trainee);

        log.info("Created trainee with username: {}", username);
    }

    @Given("an inactive trainee exists with username {string}")
    public void anInactiveTraineeExistsWithUsername(String username) {
        CustomUser user = CustomUser.builder()
                .firstName(username.split("\\.")[0])
                .lastName(username.contains(".") ? username.split("\\.")[1] : "User")
                .username(username)
                .password(passwordEncoder.encode("password123"))
                .isActive(false)
                .build();

        Trainee trainee = Trainee.builder()
                .user(user)
                .build();

        traineeRepository.save(trainee);

        log.info("Created inactive trainee with username: {}", username);
    }

    @Given("I am logged in as {string} with password {string}")
    public void iAmLoggedInAsWithPassword(String username, String password) throws Exception {
        String requestJson = String.format(
                "{\"username\":\"%s\",\"password\":\"%s\"}",
                username, password
        );

        MvcResult result = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists())
                .andReturn();

        String responseBody = result.getResponse().getContentAsString();
        String token = objectMapper.readTree(responseBody).get("token").asText();
        context.setJwtToken(token);
        context.setCurrentUsername(username);

        log.info("Logged in as user: {}", username);
    }

    @When("I login with username {string} and password {string}")
    public void iLoginWithUsernameAndPassword(String username, String password) throws Exception {
        String requestJson = String.format(
                "{\"username\":\"%s\",\"password\":\"%s\"}",
                username, password
        );

        context.setResultActions(mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson)));

        try {
            MvcResult result = context.getResultActions().andReturn();
            if (result.getResponse().getStatus() == 200) {
                String responseBody = result.getResponse().getContentAsString();
                if (responseBody.contains("token")) {
                    String token = objectMapper.readTree(responseBody).get("token").asText();
                    context.setJwtToken(token);
                }
            }
        } catch (Exception ignored) {
        }
    }

    @When("I login with username {string} and password {string} {int} times")
    public void iLoginWithUsernameAndPasswordTimes(String username, String password, int times) throws Exception {
        for (int i = 0; i < times; i++) {
            String requestJson = String.format(
                    "{\"username\":\"%s\",\"password\":\"%s\"}",
                    username, password
            );
            mockMvc.perform(post("/api/auth/login")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(requestJson));
        }
    }

    @When("I change password from {string} to {string}")
    public void iChangePasswordFromTo(String oldPassword, String newPassword) throws Exception {
        String username = context.getCurrentUsername();
        String requestJson = String.format(
                "{\"username\":\"%s\",\"oldPassword\":\"%s\",\"newPassword\":\"%s\"}",
                username, oldPassword, newPassword
        );

        context.setResultActions(mockMvc.perform(put("/api/auth/change-password")
                .header("Authorization", "Bearer " + context.getJwtToken())
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson)));
    }

    @Then("I should receive a JWT token")
    public void iShouldReceiveAJwtToken() throws Exception {
        context.getResultActions()
                .andExpect(jsonPath("$.token").exists())
                .andExpect(jsonPath("$.token").isNotEmpty());
    }

    @Then("I should not receive a JWT token")
    public void iShouldNotReceiveAJwtToken() {
        assertThat(context.getJwtToken()).isNull();
    }

    @Then("the user {string} should be blocked")
    public void theUserShouldBeBlocked(String username) {
        assertThat(bruteForceProtectionService.isBlocked(username)).isTrue();
    }

    @And("login attempt with correct password should fail")
    public void loginAttemptWithCorrectPasswordShouldFail() throws Exception {
        String requestJson = "{\"username\":\"John.Doe\",\"password\":\"password123\"}";

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isUnauthorized());
    }
}
