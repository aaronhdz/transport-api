package com.transport.api.common.exception;

import com.transport.api.order.entity.OrderStatus;

public class InvalidOrderStatusTransitionException extends RuntimeException {

    public InvalidOrderStatusTransitionException(
            OrderStatus currentStatus,
            OrderStatus newStatus) {

        super(
                "Invalid order status transition from "
                        + currentStatus
                        + " to "
                        + newStatus
        );
    }
}