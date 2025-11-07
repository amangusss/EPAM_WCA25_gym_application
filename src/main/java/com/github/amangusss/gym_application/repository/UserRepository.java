package com.github.amangusss.gym_application.repository;

import com.github.amangusss.gym_application.entity.CustomUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<CustomUser, Long> {

    boolean existsByUsername(String username);
    Optional<CustomUser> findByUsername(String username);
}
