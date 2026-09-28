package com.transport.api.security;

import com.transport.api.security.dto.LoginRequest;
import com.transport.api.security.dto.LoginResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private JwtService jwtService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthService authService;

    @BeforeEach
    void setUp() {

        ReflectionTestUtils.setField(
                authService,
                "configuredUsername",
                "transport-admin"
        );

        ReflectionTestUtils.setField(
                authService,
                "configuredPasswordHash",
                "encoded-password"
        );

        ReflectionTestUtils.setField(
                authService,
                "jwtExpiration",
                3600000L
        );
    }

    @Test
    void shouldLoginSuccessfully() {

        LoginRequest request =
                new LoginRequest(
                        "transport-admin",
                        "Transport2026!"
                );

        when(passwordEncoder.matches(
                "Transport2026!",
                "encoded-password"
        )).thenReturn(true);

        when(jwtService.generateToken(
                "transport-admin"
        )).thenReturn("generated-jwt-token");

        LoginResponse response =
                authService.login(request);

        assertNotNull(response);

        assertEquals(
                "generated-jwt-token",
                response.token()
        );

        assertEquals(
                "Bearer",
                response.type()
        );

        assertEquals(
                3600,
                response.expiresIn()
        );

        verify(passwordEncoder).matches(
                "Transport2026!",
                "encoded-password"
        );

        verify(jwtService).generateToken(
                "transport-admin"
        );
    }

    @Test
    void shouldRejectInvalidUsername() {

        LoginRequest request =
                new LoginRequest(
                        "invalid-user",
                        "Transport2026!"
                );

        when(passwordEncoder.matches(
                "Transport2026!",
                "encoded-password"
        )).thenReturn(true);

        assertThrows(
                BadCredentialsException.class,
                () -> authService.login(request)
        );

        verify(jwtService, never())
                .generateToken(anyString());
    }

    @Test
    void shouldRejectInvalidPassword() {

        LoginRequest request =
                new LoginRequest(
                        "transport-admin",
                        "wrong-password"
                );

        when(passwordEncoder.matches(
                "wrong-password",
                "encoded-password"
        )).thenReturn(false);

        assertThrows(
                BadCredentialsException.class,
                () -> authService.login(request)
        );

        verify(jwtService, never())
                .generateToken(anyString());
    }
}