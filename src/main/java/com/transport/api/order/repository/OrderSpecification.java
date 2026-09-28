package com.transport.api.order.repository;

import com.transport.api.order.entity.Order;
import com.transport.api.order.entity.OrderStatus;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;

public final class OrderSpecification {

    private OrderSpecification() {
    }

    public static Specification<Order> hasStatus(OrderStatus status) {
        return (root, query, criteriaBuilder) ->
                status == null
                        ? criteriaBuilder.conjunction()
                        : criteriaBuilder.equal(root.get("status"), status);
    }

    public static Specification<Order> originContains(String origin) {
        return (root, query, criteriaBuilder) ->
                origin == null || origin.isBlank()
                        ? criteriaBuilder.conjunction()
                        : criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("origin")),
                        "%" + origin.toLowerCase() + "%"
                );
    }

    public static Specification<Order> destinationContains(
            String destination) {

        return (root, query, criteriaBuilder) ->
                destination == null || destination.isBlank()
                        ? criteriaBuilder.conjunction()
                        : criteriaBuilder.like(
                        criteriaBuilder.lower(
                                root.get("destination")
                        ),
                        "%" + destination.toLowerCase() + "%"
                );
    }

    public static Specification<Order> createdFrom(
            LocalDateTime from) {

        return (root, query, criteriaBuilder) ->
                from == null
                        ? criteriaBuilder.conjunction()
                        : criteriaBuilder.greaterThanOrEqualTo(
                        root.get("createdAt"),
                        from
                );
    }

    public static Specification<Order> createdTo(
            LocalDateTime to) {

        return (root, query, criteriaBuilder) ->
                to == null
                        ? criteriaBuilder.conjunction()
                        : criteriaBuilder.lessThanOrEqualTo(
                        root.get("createdAt"),
                        to
                );
    }
}