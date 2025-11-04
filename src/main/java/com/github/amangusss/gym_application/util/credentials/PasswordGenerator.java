package com.github.amangusss.gym_application.util.credentials;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Random;

@Slf4j
@Component("passwordGenerator")
public class PasswordGenerator {

    private final Random random = new Random();
    private final String passwordCharacters;
    private final int passwordLength;

    public PasswordGenerator(@Value("${password.characters}") String passwordCharacters,
                           @Value("${password.length}") int passwordLength) {
        this.passwordCharacters = passwordCharacters;
        this.passwordLength = passwordLength;
    }

    public String generatePassword() {
        StringBuilder password = new StringBuilder(passwordLength);

        for (int i = 0; i < passwordLength; i++) {
            int randomIndex = random.nextInt(passwordCharacters.length());
            password.append(passwordCharacters.charAt(randomIndex));
        }

        log.debug("Password generated successfully");
        return password.toString();
    }
}
