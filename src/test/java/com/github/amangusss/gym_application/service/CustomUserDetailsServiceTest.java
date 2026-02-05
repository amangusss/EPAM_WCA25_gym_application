package com.github.amangusss.gym_application.service;

import com.github.amangusss.gym_application.entity.CustomUser;
import com.github.amangusss.gym_application.repository.UserRepository;
import com.github.amangusss.gym_application.service.impl.CustomUserDetailsService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("CustomUserDetailsService Tests")
class CustomUserDetailsServiceTest {

    private static final String USERNAME = "John.Doe";
    private static final String PASSWORD = "encodedPassword";

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private CustomUserDetailsService customUserDetailsService;

    private CustomUser testUser;

    @BeforeEach
    void setUp() {
        testUser = CustomUser.builder()
                .id(1L)
                .username(USERNAME)
                .password(PASSWORD)
                .firstName("John")
                .lastName("Doe")
                .isActive(true)
                .build();
    }

    @Test
    @DisplayName("Should load user by username successfully when user is active")
    void shouldLoadUserByUsernameWhenActive() {
        when(userRepository.findByUsername(USERNAME)).thenReturn(Optional.of(testUser));

        UserDetails userDetails = customUserDetailsService.loadUserByUsername(USERNAME);

        assertThat(userDetails).isNotNull();
        assertThat(userDetails.getUsername()).isEqualTo(USERNAME);
        assertThat(userDetails.getPassword()).isEqualTo(PASSWORD);
        assertThat(userDetails.isEnabled()).isTrue();

        verify(userRepository).findByUsername(USERNAME);
    }

    @Test
    @DisplayName("Should load user by username when user is inactive")
    void shouldLoadUserByUsernameWhenInactive() {
        testUser.setActive(false);
        when(userRepository.findByUsername(USERNAME)).thenReturn(Optional.of(testUser));

        UserDetails userDetails = customUserDetailsService.loadUserByUsername(USERNAME);

        assertThat(userDetails).isNotNull();
        assertThat(userDetails.getUsername()).isEqualTo(USERNAME);
        assertThat(userDetails.isEnabled()).isFalse();

        verify(userRepository).findByUsername(USERNAME);
    }

    @Test
    @DisplayName("Should throw UsernameNotFoundException when user not found")
    void shouldThrowUsernameNotFoundExceptionWhenUserNotFound() {
        when(userRepository.findByUsername("unknown.user")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> customUserDetailsService.loadUserByUsername("unknown.user"))
                .isInstanceOf(UsernameNotFoundException.class)
                .hasMessageContaining("unknown.user");

        verify(userRepository).findByUsername("unknown.user");
    }
}
