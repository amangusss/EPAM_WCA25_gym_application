package com.github.amangusss.gym_application.service.impl;

import com.github.amangusss.gym_application.entity.auth.LoginAttempt;
import com.github.amangusss.gym_application.repository.LoginAttemptRepository;
import com.github.amangusss.gym_application.service.BruteForceProtectionService;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BruteForceProtectionServiceImpl implements BruteForceProtectionService {

    final LoginAttemptRepository loginAttemptRepository;

    @Value("${security.max-login-attempts}")
    int maxLoginAttempts;

    @Value("${security.lockout-duration}")
    long lockoutDurationMillis;

    @Override
    @Transactional
    public void registerSuccessfulLogin(String username) {
        log.debug("Registering successful login for user: {}", username);
        loginAttemptRepository.findByUsername(username)
                .ifPresent(attempt -> {
                    loginAttemptRepository.delete(attempt);
                    log.debug("Reset login attempts for user: {}", username);
                });
    }

    @Override
    @Transactional
    public void registerFailedLogin(String username) {
        log.debug("Registering failed login for user: {}", username);

        LoginAttempt attempt = loginAttemptRepository.findByUsername(username)
                .orElse(LoginAttempt.builder()
                        .username(username)
                        .attemptCount(0)
                        .build());

        attempt.setAttemptCount(attempt.getAttemptCount() + 1);
        attempt.setLastAttemptTime(LocalDateTime.now());

        if (attempt.getAttemptCount() >= maxLoginAttempts) {
            attempt.setLockedUntil(LocalDateTime.now().plusSeconds(lockoutDurationMillis / 1000));
            log.warn("CustomUser {} locked until {} after {} failed attempts",
                    username, attempt.getLockedUntil(), attempt.getAttemptCount());
        }

        loginAttemptRepository.save(attempt);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isBlocked(String username) {
        return loginAttemptRepository.findByUsername(username)
                .map(attempt -> {
                    if (attempt.getLockedUntil() != null &&
                        attempt.getLockedUntil().isAfter(LocalDateTime.now())) {
                        log.debug("CustomUser {} is blocked until {}", username, attempt.getLockedUntil());
                        return true;
                    }
                    return false;
                })
                .orElse(false);
    }

    @Override
    @Transactional(readOnly = true)
    public int getRemainingAttempts(String username) {
        return loginAttemptRepository.findByUsername(username)
                .map(attempt -> Math.max(0, maxLoginAttempts - attempt.getAttemptCount()))
                .orElse(maxLoginAttempts);
    }
}
