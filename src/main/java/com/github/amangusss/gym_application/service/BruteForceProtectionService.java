package com.github.amangusss.gym_application.service;

public interface BruteForceProtectionService {

    void registerSuccessfulLogin(String username);
    void registerFailedLogin(String username);
    boolean isBlocked(String username);
    int getRemainingAttempts(String username);
}
