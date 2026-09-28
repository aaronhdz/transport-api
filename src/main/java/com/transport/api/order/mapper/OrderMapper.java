package com.transport.api.order.mapper;

import com.transport.api.order.dto.OrderResponse;
import com.transport.api.order.entity.Order;

public final class OrderMapper {

    private OrderMapper() {
    }

    public static OrderResponse toResponse(Order order) {
        return new OrderResponse(
                order.getId(),
                order.getStatus(),
                order.getOrigin(),
                order.getDestination(),
                order.getCreatedAt(),
                order.getUpdatedAt()
        );
    }
}