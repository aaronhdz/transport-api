package com.transport.api.assignment.entity;

import com.transport.api.driver.entity.Driver;
import com.transport.api.order.entity.Order;
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
@Table(
        name = "order_assignments",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_assignment_order",
                        columnNames = "order_id"
                )
        }
)
public class OrderAssignment {

    @Id
    private UUID id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "order_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_assignment_order")
    )
    private Order order;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "driver_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_assignment_driver")
    )
    private Driver driver;

    @Column(name = "assigned_at", nullable = false)
    private LocalDateTime assignedAt;

    @Column(name = "document_path", length = 500)
    private String documentPath;

    @Column(name = "image_path", length = 500)
    private String imagePath;

    public static OrderAssignment create(
            Order order,
            Driver driver) {

        return OrderAssignment.builder()
                .id(UUID.randomUUID())
                .order(order)
                .driver(driver)
                .assignedAt(LocalDateTime.now())
                .build();
    }

    public void attachDocument(String documentPath) {
        this.documentPath = documentPath;
    }

    public void attachImage(String imagePath) {
        this.imagePath = imagePath;
    }

}