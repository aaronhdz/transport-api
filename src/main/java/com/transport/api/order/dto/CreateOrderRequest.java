package com.transport.api.order.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateOrderRequest(

        @NotBlank(message = "Origin is required")
        @Size(max = 255, message = "Origin must not exceed 255 characters")
        String origin,

        @NotBlank(message = "Destination is required")
        @Size(max = 255, message = "Destination must not exceed 255 characters")
        String destination

) {
}