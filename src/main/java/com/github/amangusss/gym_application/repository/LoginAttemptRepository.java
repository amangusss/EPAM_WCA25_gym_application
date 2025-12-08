package com.github.amangusss.gym_application.repository;

import com.github.amangusss.gym_application.entity.auth.LoginAttempt;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface LoginAttemptRepository extends JpaRepository<LoginAttempt, Long> {

    Optional<LoginAttempt> findByUsername(String username);
    List<LoginAttempt> findAllByLockedUntilAfter(LocalDateTime time);
    void deleteAllByLastAttemptTimeBefore(LocalDateTime time);
}
