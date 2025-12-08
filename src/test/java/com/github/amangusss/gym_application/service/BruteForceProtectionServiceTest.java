package com.github.amangusss.gym_application.service;

import com.github.amangusss.gym_application.cache.LoginAttemptCache;
import com.github.amangusss.gym_application.config.CacheConfig;
import com.github.amangusss.gym_application.entity.auth.LoginAttempt;
import com.github.amangusss.gym_application.repository.LoginAttemptRepository;
import com.github.amangusss.gym_application.service.impl.BruteForceProtectionServiceImpl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.concurrent.ConcurrentMapCache;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.lenient;

@ExtendWith(MockitoExtension.class)
@DisplayName("BruteForceProtectionService Tests")
class BruteForceProtectionServiceTest {

    @Mock
    private LoginAttemptRepository loginAttemptRepository;

    @Mock
    private CacheManager cacheManager;

    @InjectMocks
    private BruteForceProtectionServiceImpl bruteForceProtectionService;

    private Cache testCache;
    private static final String TEST_USERNAME = "testuser";
    private static final int MAX_ATTEMPTS = 5;
    private static final long LOCKOUT_DURATION = 300000;

    @BeforeEach
    void setUp() {
        testCache = new ConcurrentMapCache(CacheConfig.LOGIN_ATTEMPTS_CACHE);
        when(cacheManager.getCache(CacheConfig.LOGIN_ATTEMPTS_CACHE)).thenReturn(testCache);

        ReflectionTestUtils.setField(bruteForceProtectionService, "maxLoginAttempts", MAX_ATTEMPTS);
        ReflectionTestUtils.setField(bruteForceProtectionService, "lockoutDurationMillis", LOCKOUT_DURATION);

        lenient().when(loginAttemptRepository.findAllByLockedUntilAfter(any())).thenReturn(Collections.emptyList());
    }

    @Test
    @DisplayName("Should have all attempts available for new user")
    void shouldHaveAllAttemptsForNewUser() {
        int remainingAttempts = bruteForceProtectionService.getRemainingAttempts(TEST_USERNAME);

        assertThat(remainingAttempts).isEqualTo(MAX_ATTEMPTS);
    }

    @Test
    @DisplayName("Should decrement remaining attempts after failed login")
    void shouldDecrementAttemptsAfterFailedLogin() {
        when(loginAttemptRepository.findByUsername(TEST_USERNAME)).thenReturn(Optional.empty());

        bruteForceProtectionService.registerFailedLogin(TEST_USERNAME);
        int remainingAfterFirst = bruteForceProtectionService.getRemainingAttempts(TEST_USERNAME);

        assertThat(remainingAfterFirst).isEqualTo(MAX_ATTEMPTS - 1);

        bruteForceProtectionService.registerFailedLogin(TEST_USERNAME);
        int remainingAfterSecond = bruteForceProtectionService.getRemainingAttempts(TEST_USERNAME);

        assertThat(remainingAfterSecond).isEqualTo(MAX_ATTEMPTS - 2);
    }

    @Test
    @DisplayName("Should block user after max failed attempts")
    void shouldBlockUserAfterMaxAttempts() {
        when(loginAttemptRepository.findByUsername(TEST_USERNAME)).thenReturn(Optional.empty());

        for (int i = 0; i < MAX_ATTEMPTS; i++) {
            bruteForceProtectionService.registerFailedLogin(TEST_USERNAME);
        }

        assertThat(bruteForceProtectionService.isBlocked(TEST_USERNAME)).isTrue();
        assertThat(bruteForceProtectionService.getRemainingAttempts(TEST_USERNAME)).isZero();
    }

    @Test
    @DisplayName("Should not block user before reaching max attempts")
    void shouldNotBlockUserBeforeMaxAttempts() {
        when(loginAttemptRepository.findByUsername(TEST_USERNAME)).thenReturn(Optional.empty());

        for (int i = 0; i < MAX_ATTEMPTS - 1; i++) {
            bruteForceProtectionService.registerFailedLogin(TEST_USERNAME);
        }

        assertThat(bruteForceProtectionService.isBlocked(TEST_USERNAME)).isFalse();
        assertThat(bruteForceProtectionService.getRemainingAttempts(TEST_USERNAME)).isEqualTo(1);
    }

    @Test
    @DisplayName("Should clear attempts after successful login")
    void shouldClearAttemptsAfterSuccessfulLogin() {
        when(loginAttemptRepository.findByUsername(TEST_USERNAME)).thenReturn(Optional.empty());

        bruteForceProtectionService.registerFailedLogin(TEST_USERNAME);
        bruteForceProtectionService.registerFailedLogin(TEST_USERNAME);

        assertThat(bruteForceProtectionService.getRemainingAttempts(TEST_USERNAME)).isEqualTo(MAX_ATTEMPTS - 2);

        bruteForceProtectionService.registerSuccessfulLogin(TEST_USERNAME);

        assertThat(bruteForceProtectionService.getRemainingAttempts(TEST_USERNAME)).isEqualTo(MAX_ATTEMPTS);
        assertThat(bruteForceProtectionService.isBlocked(TEST_USERNAME)).isFalse();
    }

    @Test
    @DisplayName("Should unblock user after lockout period expires")
    void shouldUnblockUserAfterLockoutExpires() {
        LocalDateTime now = LocalDateTime.now();
        LoginAttemptCache expiredEntry = LoginAttemptCache.builder()
                .username(TEST_USERNAME)
                .attemptCount(MAX_ATTEMPTS)
                .firstAttemptTime(now.minusMinutes(10))
                .lastAttemptTime(now.minusMinutes(10))
                .lockedUntil(now.minusMinutes(1))
                .build();

        testCache.put(TEST_USERNAME, expiredEntry);

        boolean isBlocked = bruteForceProtectionService.isBlocked(TEST_USERNAME);
        int remainingAttempts = bruteForceProtectionService.getRemainingAttempts(TEST_USERNAME);

        assertThat(isBlocked).isFalse();
        assertThat(remainingAttempts).isEqualTo(MAX_ATTEMPTS);
    }

    @Test
    @DisplayName("Should reset counter after lockout expires and new attempt is made")
    void shouldResetCounterAfterLockoutExpires() {
        LocalDateTime now = LocalDateTime.now();
        LoginAttemptCache expiredEntry = LoginAttemptCache.builder()
                .username(TEST_USERNAME)
                .attemptCount(MAX_ATTEMPTS)
                .firstAttemptTime(now.minusMinutes(10))
                .lastAttemptTime(now.minusMinutes(10))
                .lockedUntil(now.minusMinutes(1))
                .build();

        testCache.put(TEST_USERNAME, expiredEntry);
        when(loginAttemptRepository.findByUsername(TEST_USERNAME)).thenReturn(Optional.empty());

        bruteForceProtectionService.registerFailedLogin(TEST_USERNAME);
        int remainingAttempts = bruteForceProtectionService.getRemainingAttempts(TEST_USERNAME);

        assertThat(remainingAttempts).isEqualTo(MAX_ATTEMPTS - 1);
        assertThat(bruteForceProtectionService.isBlocked(TEST_USERNAME)).isFalse();
    }

    @Test
    @DisplayName("Should track first attempt time correctly")
    void shouldTrackFirstAttemptTime() {
        when(loginAttemptRepository.findByUsername(TEST_USERNAME)).thenReturn(Optional.empty());
        LocalDateTime beforeFirstAttempt = LocalDateTime.now();

        bruteForceProtectionService.registerFailedLogin(TEST_USERNAME);

        LoginAttemptCache cacheEntry = testCache.get(TEST_USERNAME, LoginAttemptCache.class);

        assertThat(cacheEntry).isNotNull();
        assertThat(cacheEntry.getFirstAttemptTime()).isNotNull();
        assertThat(cacheEntry.getFirstAttemptTime()).isAfterOrEqualTo(beforeFirstAttempt);
        assertThat(cacheEntry.getFirstAttemptTime()).isBeforeOrEqualTo(LocalDateTime.now());
    }

    @Test
    @DisplayName("Should calculate lockout time from first attempt, not current time")
    void shouldCalculateLockoutFromFirstAttempt() {
        when(loginAttemptRepository.findByUsername(TEST_USERNAME)).thenReturn(Optional.empty());

        for (int i = 0; i < MAX_ATTEMPTS; i++) {
            bruteForceProtectionService.registerFailedLogin(TEST_USERNAME);
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        LoginAttemptCache cacheEntry = testCache.get(TEST_USERNAME, LoginAttemptCache.class);

        assertThat(cacheEntry).isNotNull();
        assertThat(cacheEntry.getLockedUntil()).isNotNull();

        LocalDateTime expectedLockoutTime = cacheEntry.getFirstAttemptTime()
                .plusSeconds(LOCKOUT_DURATION / 1000);

        assertThat(cacheEntry.getLockedUntil()).isEqualToIgnoringNanos(expectedLockoutTime);
    }

    @Test
    @DisplayName("Should restore cache from database on initialization")
    void shouldRestoreCacheFromDatabase() {
        LocalDateTime now = LocalDateTime.now();
        LoginAttempt dbEntry = LoginAttempt.builder()
                .username(TEST_USERNAME)
                .attemptCount(3)
                .firstAttemptTime(now.minusMinutes(2))
                .lastAttemptTime(now.minusMinutes(1))
                .lockedUntil(now.plusMinutes(3))
                .build();

        when(loginAttemptRepository.findAllByLockedUntilAfter(any()))
                .thenReturn(Collections.singletonList(dbEntry));

        bruteForceProtectionService.initCache();

        LoginAttemptCache cacheEntry = testCache.get(TEST_USERNAME, LoginAttemptCache.class);
        assertThat(cacheEntry).isNotNull();
        assertThat(cacheEntry.getAttemptCount()).isEqualTo(3);
        assertThat(cacheEntry.getLockedUntil()).isEqualTo(now.plusMinutes(3));
    }

    @Test
    @DisplayName("Should persist to database when saving attempt")
    void shouldPersistToDatabase() {
        when(loginAttemptRepository.findByUsername(TEST_USERNAME)).thenReturn(Optional.empty());

        bruteForceProtectionService.registerFailedLogin(TEST_USERNAME);

        verify(loginAttemptRepository, times(1)).save(any(LoginAttempt.class));
    }
}
