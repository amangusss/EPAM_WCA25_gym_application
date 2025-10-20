package com.github.amangusss.gym_application.repository;

import com.github.amangusss.gym_application.entity.trainee.Trainee;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TraineeRepository extends JpaRepository<Trainee, Long> {

    @Query("SELECT t FROM Trainee t LEFT JOIN FETCH t.user WHERE t.user.username = :username")
    Optional<Trainee> findByUserUsername(@Param("username") String username);

    @Query("SELECT CASE WHEN COUNT(t) > 0 THEN true ELSE false END FROM Trainee t WHERE t.user.username = :username AND t.user.password = :password")
    boolean existsByUserUsernameAndUserPassword(@Param("username") String username, @Param("password") String password);
}
