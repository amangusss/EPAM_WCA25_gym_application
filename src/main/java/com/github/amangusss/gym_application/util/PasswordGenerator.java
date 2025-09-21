package com.github.amangusss.gym_application.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Random;

@Component
public class PasswordGenerator {

    public static final Logger logger = LoggerFactory.getLogger(PasswordGenerator.class);
    public static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    public static final int PASSWORD_LENGTH = 10;

    private final Random random = new Random();

    public String generatePassword() {
        StringBuilder password = new StringBuilder(PASSWORD_LENGTH);

        for (int i = 0; i < PASSWORD_LENGTH; i++) {
            int randomIndex = random.nextInt(CHARACTERS.length());
            password.append(CHARACTERS.charAt(randomIndex));
        }

        String generatedPassword = password.toString();
        logger.debug("Generated password: {}", generatedPassword);
        return generatedPassword;
    }
}
