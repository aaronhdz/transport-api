package com.transport.api.security.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "Authentication request")
public record LoginRequest(

        @Schema(
                description = "API username",
                example = "transport-admin"
        )
        @NotBlank(message = "Username is required")
        String username,

        @Schema(
                description = "API password",
                example = "Transport2026!"
        )
        @NotBlank(message = "Password is required")
        String password

) {
}