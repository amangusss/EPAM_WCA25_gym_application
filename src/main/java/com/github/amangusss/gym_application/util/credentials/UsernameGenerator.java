package com.github.amangusss.gym_application.util.credentials;

import com.github.amangusss.gym_application.exception.ValidationException;

import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Component;

import java.util.function.Predicate;

@Slf4j
@Component("usernameGenerator")
public class UsernameGenerator {
    public String generateUsername(String firstName, String lastName, Predicate<String> usernameExistsChecker) {
        if (firstName == null || lastName == null) {
            throw new ValidationException("First name must not be null and last name must not be null");
        }

        String baseUsername = firstName.trim() + "." + lastName.trim();
        String username = baseUsername;
        int counter = 1;

        while (usernameExistsChecker.test(username)) {
            username = baseUsername + counter;
            counter++;
            log.debug("Username {} already exists. Trying username {}", baseUsername, username);
        }

        log.debug("Generated username: {}", username);
        return username;
    }
}
