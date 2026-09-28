package com.transport.api.common.exception;

import java.util.UUID;

public class OrderAlreadyAssignedException extends RuntimeException {

    public OrderAlreadyAssignedException(UUID orderId) {
        super("Order already has a driver assigned: " + orderId);
    }
}