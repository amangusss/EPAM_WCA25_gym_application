package com.github.amangusss.gym_application.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
public class UsernameGenerator {

    public static final Logger logger = LoggerFactory.getLogger(UsernameGenerator.class);

    public String generateUsername(String firstName, String lastName, Set<String> existingUsernames) {
        if (firstName == null || lastName == null) {
            throw new IllegalArgumentException("First name and last name must not be null");
        }

        String baseUsername = firstName.trim() + "." + lastName.trim();
        String username = baseUsername;
        int counter = 1;

        while (existingUsernames.contains(username)) {
            username = baseUsername + counter;
            counter++;
            logger.debug("Username {} already exists. Trying username {}", baseUsername, username);
        }

        logger.debug("Generated username: {}", username);
        return username;
    }
}