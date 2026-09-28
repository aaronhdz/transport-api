package com.transport.api.assignment.repository;

import com.transport.api.assignment.entity.OrderAssignment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface OrderAssignmentRepository
        extends JpaRepository<OrderAssignment, UUID> {

    boolean existsByOrderId(UUID orderId);

    Optional<OrderAssignment> findByOrderId(UUID orderId);

}