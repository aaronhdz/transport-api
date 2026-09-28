package com.transport.api.assignment.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record AssignmentResponse(
        UUID id,
        UUID orderId,
        UUID driverId,
        LocalDateTime assignedAt,
        String documentPath,
        String imagePath
) {
}