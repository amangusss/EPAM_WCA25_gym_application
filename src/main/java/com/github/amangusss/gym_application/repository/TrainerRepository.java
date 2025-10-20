package com.github.amangusss.gym_application.repository;

import com.github.amangusss.gym_application.entity.trainer.Trainer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TrainerRepository extends JpaRepository<Trainer, Long> {

    @Query("SELECT t FROM Trainer t LEFT JOIN FETCH t.user WHERE t.user.username = :username")
    Optional<Trainer> findByUserUsername(@Param("username") String username);

    @Query("SELECT CASE WHEN COUNT(t) > 0 THEN true ELSE false END FROM Trainer t WHERE t.user.username = :username AND t.user.password = :password")
    boolean existsByUserUsernameAndUserPassword(@Param("username") String username, @Param("password") String password);
}