package com.github.amangusss.gym_application.service.impl;

import com.github.amangusss.gym_application.cache.LoginAttemptCache;
import com.github.amangusss.gym_application.config.CacheConfig;
import com.github.amangusss.gym_application.entity.auth.LoginAttempt;
import com.github.amangusss.gym_application.repository.LoginAttemptRepository;
import com.github.amangusss.gym_application.service.BruteForceProtectionService;

import jakarta.annotation.PostConstruct;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BruteForceProtectionServiceImpl implements BruteForceProtectionService {

    final LoginAttemptRepository loginAttemptRepository;
    final CacheManager cacheManager;
    final ConcurrentHashMap<String, Object> locks = new ConcurrentHashMap<>();

    @Value("${security.max-login-attempts}")
    int maxLoginAttempts;

    @Value("${security.lockout-duration}")
    long lockoutDurationMillis;

    @PostConstruct
    public void initCache() {
        log.info("Initializing brute force protection cache from database");

        try {
            List<LoginAttempt> activeAttempts = loginAttemptRepository
                    .findAllByLockedUntilAfter(LocalDateTime.now());

            activeAttempts.forEach(attempt -> {
                LoginAttemptCache cacheEntry = convertToCache(attempt);
                putInCache(attempt.getUsername(), cacheEntry);
                log.debug("Restored locked user from DB to cache: {}, locked until: {}",
                         attempt.getUsername(), attempt.getLockedUntil());
            });

            log.info("Loaded {} active lockouts into cache", activeAttempts.size());
        } catch (Exception e) {
            log.error("Error initializing cache from database", e);
        }
    }

    @Override
    @Transactional
    public void registerSuccessfulLogin(String username) {
        log.debug("Registering successful login for user: {}", username);

        evictFromCache(username);

        loginAttemptRepository.findByUsername(username)
                .ifPresent(attempt -> {
                    loginAttemptRepository.delete(attempt);
                    log.debug("Cleared login attempts from DB for user: {}", username);
                });
    }

    @Override
    @Transactional
    public void registerFailedLogin(String username) {
        Object lock = locks.computeIfAbsent(username, k -> new Object());
        synchronized (lock) {
            log.debug("Registering failed login for user: {}", username);

            LoginAttemptCache cacheEntry = getOrCreateAttempt(username);
            incrementAttemptCount(cacheEntry);
            applyLockoutIfNeeded(cacheEntry);
            saveAttempt(cacheEntry);
        }
    }

    private LoginAttemptCache getOrCreateAttempt(String username) {
        LoginAttemptCache cacheEntry = getFromCache(username);
        LocalDateTime now = LocalDateTime.now();

        if (cacheEntry == null) {
            log.debug("First failed login attempt for user: {}", username);
            return createNewAttempt(username, now);
        }

        if (isLockoutExpired(cacheEntry, now)) {
            log.debug("Lockout expired for user: {}, resetting counter", username);
            return createNewAttempt(username, now);
        }

        return cacheEntry;
    }

    private LoginAttemptCache createNewAttempt(String username, LocalDateTime now) {
        return LoginAttemptCache.builder()
                .username(username)
                .attemptCount(0)
                .firstAttemptTime(now)
                .lastAttemptTime(now)
                .build();
    }

    private boolean isLockoutExpired(LoginAttemptCache cacheEntry, LocalDateTime now) {
        return cacheEntry.getLockedUntil() != null && now.isAfter(cacheEntry.getLockedUntil());
    }

    private void incrementAttemptCount(LoginAttemptCache cacheEntry) {
        cacheEntry.setAttemptCount(cacheEntry.getAttemptCount() + 1);
        cacheEntry.setLastAttemptTime(LocalDateTime.now());
        log.debug("Failed login attempt #{} for user: {}", cacheEntry.getAttemptCount(), cacheEntry.getUsername());
    }

    private void applyLockoutIfNeeded(LoginAttemptCache cacheEntry) {
        if (cacheEntry.getAttemptCount() >= maxLoginAttempts) {
            LocalDateTime lockoutTime = cacheEntry.getFirstAttemptTime()
                    .plusSeconds(lockoutDurationMillis / 1000);
            cacheEntry.setLockedUntil(lockoutTime);

            log.warn("User {} locked until {} after {} failed attempts",
                    cacheEntry.getUsername(), lockoutTime, cacheEntry.getAttemptCount());
        }
    }

    @Override
    public boolean isBlocked(String username) {
        LoginAttemptCache cacheEntry = getFromCache(username);

        if (cacheEntry == null) {
            return false;
        }

        LocalDateTime now = LocalDateTime.now();

        if (cacheEntry.getLockedUntil() != null && now.isAfter(cacheEntry.getLockedUntil())) {
            log.debug("User {} lockout expired, clearing attempts", username);
            clearAttempts(username);
            return false;
        }

        boolean isBlocked = cacheEntry.getLockedUntil() != null &&
                           now.isBefore(cacheEntry.getLockedUntil());

        if (isBlocked) {
            log.debug("User {} is blocked until {}", username, cacheEntry.getLockedUntil());
        }

        return isBlocked;
    }

    @Override
    public int getRemainingAttempts(String username) {
        if (isBlocked(username)) {
            return 0;
        }

        LoginAttemptCache cacheEntry = getFromCache(username);

        if (cacheEntry == null) {
            return maxLoginAttempts;
        }

        int remaining = Math.max(0, maxLoginAttempts - cacheEntry.getAttemptCount());

        log.debug("User {} has {} remaining login attempts", username, remaining);
        return remaining;
    }

    private void saveAttempt(LoginAttemptCache cacheEntry) {
        putInCache(cacheEntry.getUsername(), cacheEntry);

        LoginAttempt entity = convertToEntity(cacheEntry);

        loginAttemptRepository.findByUsername(cacheEntry.getUsername())
                .ifPresentOrElse(
                        existing -> {
                            existing.setAttemptCount(cacheEntry.getAttemptCount());
                            existing.setFirstAttemptTime(cacheEntry.getFirstAttemptTime());
                            existing.setLastAttemptTime(cacheEntry.getLastAttemptTime());
                            existing.setLockedUntil(cacheEntry.getLockedUntil());
                            loginAttemptRepository.save(existing);
                        },
                        () -> loginAttemptRepository.save(entity)
                );

        log.debug("Saved login attempt for user {} to cache and DB", cacheEntry.getUsername());
    }

    private void clearAttempts(String username) {
        evictFromCache(username);

        loginAttemptRepository.findByUsername(username)
                .ifPresent(loginAttemptRepository::delete);

        log.debug("Cleared all login attempts for user: {}", username);
    }

    private LoginAttemptCache getFromCache(String username) {
        Cache cache = cacheManager.getCache(CacheConfig.LOGIN_ATTEMPTS_CACHE);
        if (cache == null) {
            log.warn("Login attempts cache not found!");
            return null;
        }

        return cache.get(username, LoginAttemptCache.class);
    }

    private void putInCache(String username, LoginAttemptCache cacheEntry) {
        Cache cache = cacheManager.getCache(CacheConfig.LOGIN_ATTEMPTS_CACHE);
        if (cache == null) {
            log.warn("Login attempts cache not found!");
            return;
        }

        cache.put(username, cacheEntry);
    }

    private void evictFromCache(String username) {
        Cache cache = cacheManager.getCache(CacheConfig.LOGIN_ATTEMPTS_CACHE);
        if (cache == null) {
            log.warn("Login attempts cache not found!");
            return;
        }

        cache.evict(username);
    }

    private LoginAttempt convertToEntity(LoginAttemptCache cache) {
        return LoginAttempt.builder()
                .username(cache.getUsername())
                .attemptCount(cache.getAttemptCount())
                .firstAttemptTime(cache.getFirstAttemptTime())
                .lastAttemptTime(cache.getLastAttemptTime())
                .lockedUntil(cache.getLockedUntil())
                .build();
    }

    private LoginAttemptCache convertToCache(LoginAttempt entity) {
        return LoginAttemptCache.builder()
                .username(entity.getUsername())
                .attemptCount(entity.getAttemptCount())
                .firstAttemptTime(entity.getFirstAttemptTime())
                .lastAttemptTime(entity.getLastAttemptTime())
                .lockedUntil(entity.getLockedUntil())
                .build();
    }

    @Scheduled(cron = "0 0 2 * * ?")
    @Transactional
    public void cleanupOldAttempts() {
        log.info("Running scheduled cleanup of old login attempts");

        try {
            LocalDateTime threshold = LocalDateTime.now().minusDays(7);
            loginAttemptRepository.deleteAllByLastAttemptTimeBefore(threshold);

            log.info("Cleaned up login attempts older than 7 days");
        } catch (Exception e) {
            log.error("Error during scheduled cleanup of login attempts", e);
        }
    }
}
