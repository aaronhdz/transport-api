package com.transport.api.order.entity;

import com.transport.api.common.exception.InvalidOrderStatusTransitionException;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "orders")
public class Order {

    @Id
    private UUID id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private OrderStatus status;

    @Column(nullable = false, length = 255)
    private String origin;

    @Column(nullable = false, length = 255)
    private String destination;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    public static Order create(String origin, String destination) {
        LocalDateTime now = LocalDateTime.now();

        return Order.builder()
                .id(UUID.randomUUID())
                .status(OrderStatus.CREATED)
                .origin(origin)
                .destination(destination)
                .createdAt(now)
                .updatedAt(now)
                .build();
    }

    public void changeStatus(OrderStatus newStatus) {
        if (newStatus == null) {
            throw new IllegalArgumentException("New status must not be null");
        }

        if (!isValidTransition(newStatus)) {
            throw new InvalidOrderStatusTransitionException(
                    this.status,
                    newStatus
            );
        }

        this.status = newStatus;
        this.updatedAt = LocalDateTime.now();
    }

    private boolean isValidTransition(OrderStatus newStatus) {
        return switch (this.status) {
            case CREATED ->
                    newStatus == OrderStatus.IN_TRANSIT
                            || newStatus == OrderStatus.CANCELLED;

            case IN_TRANSIT ->
                    newStatus == OrderStatus.DELIVERED;

            case DELIVERED, CANCELLED -> false;
        };
    }

}