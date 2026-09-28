package com.transport.api.order.dto;

import com.transport.api.order.entity.OrderStatus;

import java.time.LocalDateTime;
import java.util.UUID;

public record OrderResponse(
        UUID id,
        OrderStatus status,
        String origin,
        String destination,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}