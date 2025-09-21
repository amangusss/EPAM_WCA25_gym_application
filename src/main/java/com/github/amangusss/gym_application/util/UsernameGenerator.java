package com.github.amangusss.gym_application.util;

import com.github.amangusss.gym_application.exception.ValidationException;
import com.github.amangusss.gym_application.util.constants.ConfigConstants;
import com.github.amangusss.gym_application.util.constants.LoggerConstants;
import com.github.amangusss.gym_application.util.constants.ValidationConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component(ConfigConstants.BEAN_USERNAME_GENERATOR)
public class UsernameGenerator {

    public static final Logger logger = LoggerFactory.getLogger(UsernameGenerator.class);

    public String generateUsername(String firstName, String lastName, Set<String> existingUsernames) {
        if (firstName == null || lastName == null) {
            throw new ValidationException(ValidationConstants.FIRST_NAME_NULL + " and " + ValidationConstants.LAST_NAME_NULL);
        }

        String baseUsername = firstName.trim() + ConfigConstants.USERNAME_SEPARATOR + lastName.trim();
        String username = baseUsername;
        int counter = 1;

        while (existingUsernames.contains(username)) {
            username = baseUsername + counter;
            counter++;
            logger.debug(LoggerConstants.USERNAME_EXISTS, baseUsername, username);
        }

        logger.debug(LoggerConstants.USERNAME_GENERATED, username);
        return username;
    }
}