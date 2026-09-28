package com.transport.api.order.dto;

import com.transport.api.order.entity.OrderStatus;
import jakarta.validation.constraints.NotNull;

public record UpdateOrderStatusRequest(

        @NotNull(message = "Status is required")
        OrderStatus status

) {
}