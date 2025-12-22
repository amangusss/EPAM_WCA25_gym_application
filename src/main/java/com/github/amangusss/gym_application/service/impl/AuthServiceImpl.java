package com.github.amangusss.gym_application.service.impl;

import com.github.amangusss.gym_application.dto.auth.AuthDTO;
import com.github.amangusss.gym_application.entity.CustomUser;
import com.github.amangusss.gym_application.jwt.JwtUtils;
import com.github.amangusss.gym_application.repository.UserRepository;
import com.github.amangusss.gym_application.service.AuthService;
import com.github.amangusss.gym_application.service.BruteForceProtectionService;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthServiceImpl implements AuthService {

    AuthenticationManager authenticationManager;
    UserDetailsService userDetailsService;
    JwtUtils jwtUtils;
    BruteForceProtectionService bruteForceProtectionService;
    UserRepository userRepository;
    PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public AuthDTO.Response.Login login(AuthDTO.Request.Login request) {
        log.debug("Login attempt for user: {}", request.username());

        if (bruteForceProtectionService.isBlocked(request.username())) {
            log.warn("Login blocked for user {} due to too many failed attempts", request.username());
            throw new LockedException("Account is temporarily locked due to too many failed login attempts");
        }

        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.username(), request.password())
            );

            UserDetails userDetails = userDetailsService.loadUserByUsername(request.username());
            String token = jwtUtils.generateToken(userDetails);

            bruteForceProtectionService.registerSuccessfulLogin(request.username());

            log.info("CustomUser {} logged in successfully", request.username());
            return new AuthDTO.Response.Login(token, request.username());

        } catch (BadCredentialsException e) {
            bruteForceProtectionService.registerFailedLogin(request.username());
            int remainingAttempts = bruteForceProtectionService.getRemainingAttempts(request.username());

            log.warn("Failed login attempt for user: {}, remaining attempts: {}", request.username(), remainingAttempts);
            throw new BadCredentialsException("Invalid username or password");
        }
    }

    @Override
    @Transactional
    public void changePassword(String username, AuthDTO.Request.ChangePassword request) {
        log.debug("Password change request for user: {}", username);

        CustomUser user = userRepository.findByUsername(username)
                .orElseThrow(() -> {
                    log.warn("CustomUser not found: {}", username);
                    return new BadCredentialsException("User not found");
                });

        if (!passwordEncoder.matches(request.oldPassword(), user.getPassword())) {
            log.warn("Invalid old password for user: {}", username);
            throw new BadCredentialsException("Invalid old password");
        }

        String hashedPassword = passwordEncoder.encode(request.newPassword());
        user.setPassword(hashedPassword);
        userRepository.save(user);

        log.info("Password changed successfully for user: {}", username);
    }

    @Override
    @Transactional
    public void logout(String username) {
        log.info("CustomUser {} logged out", username);
    }
}
