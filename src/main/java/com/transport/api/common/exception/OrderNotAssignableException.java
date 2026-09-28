package com.transport.api.common.exception;

import com.transport.api.order.entity.OrderStatus;

public class OrderNotAssignableException extends RuntimeException {

    public OrderNotAssignableException(OrderStatus status) {
        super(
                "Order cannot be assigned when status is: "
                        + status
        );
    }
}