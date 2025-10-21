package com.github.amangusss.gym_application.util.credentials;

import com.github.amangusss.gym_application.exception.ValidationException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.function.Predicate;

@Component("usernameGenerator")
public class UsernameGenerator {

    public static final Logger logger = LoggerFactory.getLogger(UsernameGenerator.class);

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
            logger.debug("Username {} already exists. Trying username {}", baseUsername, username);
        }

        logger.debug("Generated username: {}", username);
        return username;
    }
}
