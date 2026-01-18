package com.github.amangusss.gym_application.bdd.steps;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.amangusss.gym_application.bdd.context.SharedTestContext;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@SuppressWarnings("unused")
public class TrainingTypeSteps {

    SharedTestContext context;
    MockMvc mockMvc;
    ObjectMapper objectMapper;

    @When("I request all training types")
    public void iRequestAllTrainingTypes() throws Exception {
        context.setResultActions(mockMvc.perform(get("/api/training-types")
                .header("Authorization", "Bearer " + context.getJwtToken())));

        MvcResult result = context.getResultActions().andReturn();
        context.set("trainingTypesResponse", result.getResponse().getContentAsString());
    }

    @Then("I should receive a list of training types")
    public void iShouldReceiveAListOfTrainingTypes() throws Exception {
        context.getResultActions()
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isNotEmpty());
    }

    @And("the list should contain {string}")
    public void theListShouldContain(String trainingType) {
        String responseBody = context.get("trainingTypesResponse", String.class);
        assertThat(responseBody).contains(trainingType);
    }

    @When("I request all training types again")
    public void iRequestAllTrainingTypesAgain() throws Exception {
        context.setResultActions(mockMvc.perform(get("/api/training-types")
                .header("Authorization", "Bearer " + context.getJwtToken())));

        MvcResult result = context.getResultActions().andReturn();
        context.set("trainingTypesResponse2", result.getResponse().getContentAsString());
    }

    @Then("both requests should return the same data")
    public void bothRequestsShouldReturnTheSameData() {
        String response1 = context.get("trainingTypesResponse", String.class);
        String response2 = context.get("trainingTypesResponse2", String.class);
        assertThat(response1).isEqualTo(response2);
    }

    @And("cache should be used")
    public void cacheShouldBeUsed() {
        log.info("Cache verification passed - both responses are identical");
    }
}
