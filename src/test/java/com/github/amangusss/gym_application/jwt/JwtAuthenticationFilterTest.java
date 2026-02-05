package com.github.amangusss.gym_application.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.io.IOException;
import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("JwtAuthenticationFilter Tests")
class JwtAuthenticationFilterTest {

    private static final String USERNAME = "John.Doe";
    private static final String VALID_TOKEN = "valid.jwt.token";
    private static final String BEARER_TOKEN = "Bearer " + VALID_TOKEN;

    @Mock
    private JwtUtils jwtUtils;

    @Mock
    private UserDetailsService userDetailsService;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain filterChain;

    @InjectMocks
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    private UserDetails userDetails;

    @BeforeEach
    void setUp() {
        SecurityContextHolder.clearContext();

        userDetails = User.builder()
                .username(USERNAME)
                .password("password")
                .authorities(Collections.emptyList())
                .build();
    }

    @Nested
    @DisplayName("Filter Tests")
    class FilterTests {

        @Test
        @DisplayName("Should authenticate user with valid token")
        void shouldAuthenticateUserWithValidToken() throws ServletException, IOException {
            when(request.getHeader("Authorization")).thenReturn(BEARER_TOKEN);
            when(jwtUtils.extractUsername(VALID_TOKEN)).thenReturn(USERNAME);
            when(userDetailsService.loadUserByUsername(USERNAME)).thenReturn(userDetails);
            when(jwtUtils.validateToken(VALID_TOKEN, userDetails)).thenReturn(true);

            jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

            assertThat(SecurityContextHolder.getContext().getAuthentication()).isNotNull();
            assertThat(SecurityContextHolder.getContext().getAuthentication().getName()).isEqualTo(USERNAME);
            verify(filterChain).doFilter(request, response);
        }

        @Test
        @DisplayName("Should continue filter chain when no authorization header")
        void shouldContinueFilterChainWhenNoAuthHeader() throws ServletException, IOException {
            when(request.getHeader("Authorization")).thenReturn(null);

            jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

            assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
            verify(filterChain).doFilter(request, response);
            verify(jwtUtils, never()).extractUsername(anyString());
        }

        @Test
        @DisplayName("Should continue filter chain when authorization header doesn't start with Bearer")
        void shouldContinueFilterChainWhenNotBearerToken() throws ServletException, IOException {
            when(request.getHeader("Authorization")).thenReturn("Basic sometoken");

            jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

            assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
            verify(filterChain).doFilter(request, response);
            verify(jwtUtils, never()).extractUsername(anyString());
        }

        @Test
        @DisplayName("Should not authenticate when token is invalid")
        void shouldNotAuthenticateWhenTokenIsInvalid() throws ServletException, IOException {
            when(request.getHeader("Authorization")).thenReturn(BEARER_TOKEN);
            when(jwtUtils.extractUsername(VALID_TOKEN)).thenReturn(USERNAME);
            when(userDetailsService.loadUserByUsername(USERNAME)).thenReturn(userDetails);
            when(jwtUtils.validateToken(VALID_TOKEN, userDetails)).thenReturn(false);

            jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

            assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
            verify(filterChain).doFilter(request, response);
        }

        @Test
        @DisplayName("Should handle exception during username extraction")
        void shouldHandleExceptionDuringUsernameExtraction() throws ServletException, IOException {
            when(request.getHeader("Authorization")).thenReturn(BEARER_TOKEN);
            when(jwtUtils.extractUsername(VALID_TOKEN)).thenThrow(new RuntimeException("Token parsing error"));

            jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

            assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
            verify(filterChain).doFilter(request, response);
        }
    }
}
