package com.github.amangusss.gym_application.bdd.steps;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.amangusss.gym_application.bdd.context.SharedTestContext;
import com.github.amangusss.gym_application.entity.CustomUser;
import com.github.amangusss.gym_application.entity.TrainingType;
import com.github.amangusss.gym_application.entity.trainer.Trainer;
import com.github.amangusss.gym_application.repository.TraineeRepository;
import com.github.amangusss.gym_application.repository.TrainerRepository;
import com.github.amangusss.gym_application.repository.TrainingTypeRepository;
import com.github.amangusss.gym_application.repository.UserRepository;

import io.cucumber.java.en.And;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@SuppressWarnings("unused")
public class TraineeSteps {

    SharedTestContext context;
    MockMvc mockMvc;
    ObjectMapper objectMapper;
    UserRepository userRepository;
    TraineeRepository traineeRepository;
    TrainerRepository trainerRepository;
    TrainingTypeRepository trainingTypeRepository;
    PasswordEncoder passwordEncoder;

    @When("I register a new trainee with firstName {string} and lastName {string}")
    public void iRegisterANewTraineeWithFirstNameAndLastName(String firstName, String lastName) throws Exception {
        String requestJson = String.format(
                "{\"firstName\":\"%s\",\"lastName\":\"%s\",\"dateOfBirth\":\"2000-01-01\",\"address\":\"123 Test Street\"}",
                firstName, lastName
        );

        context.setResultActions(mockMvc.perform(post("/api/trainees/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson)));
    }

    @Then("I should receive username and password")
    public void iShouldReceiveUsernameAndPassword() throws Exception {
        context.getResultActions()
                .andExpect(jsonPath("$.username").exists())
                .andExpect(jsonPath("$.password").exists());
    }

    @And("username should start with {string}")
    public void usernameShouldStartWith(String prefix) throws Exception {
        MvcResult result = context.getResultActions().andReturn();
        String responseBody = result.getResponse().getContentAsString();
        String username = objectMapper.readTree(responseBody).get("username").asText();
        assertThat(username).startsWith(prefix);
    }

    @When("I request my trainee profile")
    public void iRequestMyTraineeProfile() throws Exception {
        context.setResultActions(mockMvc.perform(get("/api/trainees/profile")
                .header("Authorization", "Bearer " + context.getJwtToken())));
    }

    @When("I request profile for username {string}")
    public void iRequestProfileForUsername(String username) throws Exception {
        context.setResultActions(mockMvc.perform(get("/api/trainees/" + username)
                .header("Authorization", "Bearer " + context.getJwtToken())));
    }

    @Then("the profile should contain username {string}")
    public void theProfileShouldContainUsername(String username) throws Exception {
        String expectedFirstName = username.split("\\.")[0];
        context.getResultActions()
                .andExpect(jsonPath("$.firstName").value(expectedFirstName));
    }

    @When("I update my profile with firstName {string} and lastName {string}")
    public void iUpdateMyProfileWithFirstNameAndLastName(String firstName, String lastName) throws Exception {
        String username = context.getCurrentUsername();

        String requestJson = String.format(
                "{\"firstName\":\"%s\",\"lastName\":\"%s\",\"dateOfBirth\":\"2000-01-01\",\"address\":\"Test Address\",\"isActive\":true}",
                firstName, lastName
        );

        context.setResultActions(mockMvc.perform(put("/api/trainees/" + username)
                .header("Authorization", "Bearer " + context.getJwtToken())
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson)));
    }

    @Then("the profile should be updated")
    public void theProfileShouldBeUpdated() throws Exception {
        context.getResultActions()
                .andExpect(jsonPath("$.firstName").exists())
                .andExpect(jsonPath("$.lastName").exists());
    }

    @When("I delete my trainee profile")
    public void iDeleteMyTraineeProfile() throws Exception {
        String username = context.getCurrentUsername();

        context.setResultActions(mockMvc.perform(delete("/api/trainees/" + username)
                .header("Authorization", "Bearer " + context.getJwtToken())));
    }

    @When("I activate my trainee profile")
    public void iActivateMyTraineeProfile() throws Exception {
        String username = context.getCurrentUsername();

        String requestJson = "{\"isActive\":true}";

        context.setResultActions(mockMvc.perform(patch("/api/trainees/" + username + "/activate")
                .header("Authorization", "Bearer " + context.getJwtToken())
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson)));
    }

    @When("I deactivate my trainee profile")
    public void iDeactivateMyTraineeProfile() throws Exception {
        String username = context.getCurrentUsername();

        String requestJson = "{\"isActive\":false}";

        context.setResultActions(mockMvc.perform(patch("/api/trainees/" + username + "/activate")
                .header("Authorization", "Bearer " + context.getJwtToken())
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson)));
    }

    @Then("the profile should be active")
    public void theProfileShouldBeActive() throws Exception {
        MvcResult profileResult = mockMvc.perform(get("/api/trainees/profile")
                        .header("Authorization", "Bearer " + context.getJwtToken()))
                .andExpect(jsonPath("$.isActive").value(true))
                .andReturn();
    }

    @Then("the profile should be inactive")
    public void theProfileShouldBeInactive() throws Exception {
        MvcResult profileResult = mockMvc.perform(get("/api/trainees/profile")
                        .header("Authorization", "Bearer " + context.getJwtToken()))
                .andExpect(jsonPath("$.isActive").value(false))
                .andReturn();
    }

    @And("a trainer exists with username {string}")
    public void aTrainerExistsWithUsername(String username) {
        TrainingType trainingType = trainingTypeRepository.findByTypeName("FITNESS")
                .orElseGet(() -> trainingTypeRepository.save(TrainingType.builder().typeName("FITNESS").build()));

        CustomUser user = CustomUser.builder()
                .firstName(username.split("\\.")[0])
                .lastName(username.contains(".") ? username.split("\\.")[1] : "Trainer")
                .username(username)
                .password(passwordEncoder.encode("password123"))
                .isActive(true)
                .build();

        Trainer trainer = Trainer.builder()
                .user(user)
                .specialization(trainingType)
                .build();
        trainerRepository.save(trainer);

        log.info("Created trainer with username: {}", username);
    }

    @When("I update my trainers list with {string}")
    public void iUpdateMyTrainersListWith(String trainerUsername) throws Exception {
        String username = context.getCurrentUsername();

        String requestJson = String.format("{\"trainerUsernames\":[\"%s\"]}", trainerUsername);

        context.setResultActions(mockMvc.perform(put("/api/trainees/" + username + "/trainers")
                .header("Authorization", "Bearer " + context.getJwtToken())
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson)));
    }

    @And("my trainers list should contain {string}")
    public void myTrainersListShouldContain(String trainerUsername) throws Exception {
        context.getResultActions()
                .andExpect(jsonPath("$[0].username").value(trainerUsername));
    }

    @When("I request my trainings list")
    public void iRequestMyTrainingsList() throws Exception {
        String username = context.getCurrentUsername();

        context.setResultActions(mockMvc.perform(get("/api/trainees/" + username + "/trainings")
                .header("Authorization", "Bearer " + context.getJwtToken())));
    }

    @And("I should receive a list of trainings")
    public void iShouldReceiveAListOfTrainings() throws Exception {
        context.getResultActions()
                .andExpect(jsonPath("$").isArray());
    }
}
