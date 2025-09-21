package com.github.amangusss.gym_application.util;

import com.github.amangusss.gym_application.util.constants.ConfigConstants;
import com.github.amangusss.gym_application.util.constants.LoggerConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Random;

@Component(ConfigConstants.BEAN_PASSWORD_GENERATOR)
public class PasswordGenerator {

    public static final Logger logger = LoggerFactory.getLogger(PasswordGenerator.class);

    private final Random random = new Random();
    private final String passwordCharacters;
    private final int passwordLength;

    public PasswordGenerator(@Value("${" + ConfigConstants.PROP_PASSWORD_CHARACTERS + "}") String passwordCharacters,
                           @Value("${" + ConfigConstants.PROP_PASSWORD_LENGTH + "}") int passwordLength) {
        this.passwordCharacters = passwordCharacters;
        this.passwordLength = passwordLength;
    }

    public String generatePassword() {
        StringBuilder password = new StringBuilder(passwordLength);

        for (int i = 0; i < passwordLength; i++) {
            int randomIndex = random.nextInt(passwordCharacters.length());
            password.append(passwordCharacters.charAt(randomIndex));
        }

        String generatedPassword = password.toString();
        logger.debug(LoggerConstants.PASSWORD_GENERATED, generatedPassword);
        return generatedPassword;
    }
}
