package com.transport.api.order.repository;

import com.transport.api.order.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.UUID;

public interface OrderRepository
        extends JpaRepository<Order, UUID>,
        JpaSpecificationExecutor<Order> {
}