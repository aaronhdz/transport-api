package com.transport.api.assignment.dto;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record AssignDriverRequest(

        @NotNull(message = "Driver id is required")
        UUID driverId

) {
}