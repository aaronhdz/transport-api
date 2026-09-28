package com.transport.api.security.dto;

public record LoginResponse(
        String token,
        String type,
        long expiresIn
) {
}