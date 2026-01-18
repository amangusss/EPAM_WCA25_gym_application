package com.github.amangusss.gym_application.bdd.steps;

import com.github.amangusss.gym_application.bdd.context.SharedTestContext;

import io.cucumber.java.en.Then;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RequiredArgsConstructor
@FieldDefaults(level = lombok.AccessLevel.PRIVATE, makeFinal = true)
@SuppressWarnings("unused")
public class CommonSteps {

    SharedTestContext context;

    @Then("the response status should be {int}")
    public void theResponseStatusShouldBe(int statusCode) throws Exception {
        context.getResultActions()
                .andExpect(status().is(statusCode));
    }
}