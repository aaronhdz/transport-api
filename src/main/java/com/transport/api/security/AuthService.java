package com.transport.api.security;

import com.transport.api.security.dto.LoginRequest;
import com.transport.api.security.dto.LoginResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.security.username}")
    private String configuredUsername;

    @Value("${app.security.password-hash}")
    private String configuredPasswordHash;

    @Value("${app.jwt.expiration}")
    private long jwtExpiration;

    public LoginResponse login(LoginRequest request) {

        boolean validUsername =
                configuredUsername.equals(request.username());

        boolean validPassword =
                passwordEncoder.matches(
                        request.password(),
                        configuredPasswordHash
                );

        if (!validUsername || !validPassword) {

            log.warn(
                    "event=authentication_failed username={}",
                    request.username()
            );

            throw new BadCredentialsException(
                    "Invalid username or password"
            );
        }

        String token = jwtService.generateToken(
                request.username()
        );

        log.info(
                "event=authentication_success username={}",
                request.username()
        );

        return new LoginResponse(
                token,
                "Bearer",
                jwtExpiration / 1000
        );
    }
}