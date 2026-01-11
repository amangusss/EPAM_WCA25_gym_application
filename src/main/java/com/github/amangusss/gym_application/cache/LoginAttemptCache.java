package com.github.amangusss.gym_application.cache;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
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
