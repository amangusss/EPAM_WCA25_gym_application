package com.github.amangusss.gym_application.cache;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginAttemptCache {

    private String username;
    private int attemptCount;
    private LocalDateTime firstAttemptTime;
    private LocalDateTime lastAttemptTime;
    private LocalDateTime lockedUntil;
}
