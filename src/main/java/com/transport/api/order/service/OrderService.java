package com.transport.api.order.service;

import com.transport.api.common.exception.ResourceNotFoundException;
import com.transport.api.order.dto.CreateOrderRequest;
import com.transport.api.order.dto.OrderResponse;
import com.transport.api.order.dto.UpdateOrderStatusRequest;
import com.transport.api.order.entity.Order;
import com.transport.api.order.entity.OrderStatus;
import com.transport.api.order.mapper.OrderMapper;
import com.transport.api.order.repository.OrderRepository;
import com.transport.api.order.repository.OrderSpecification;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;

    @Transactional
    public OrderResponse create(CreateOrderRequest request) {

        Order order = Order.create(
                request.origin(),
                request.destination()
        );

        Order savedOrder = orderRepository.save(order);

        log.info(
                "event=order_created orderId={} status={} origin={} destination={}",
                savedOrder.getId(),
                savedOrder.getStatus(),
                savedOrder.getOrigin(),
                savedOrder.getDestination()
        );

        return OrderMapper.toResponse(savedOrder);
    }

    @Transactional(readOnly = true)
    public OrderResponse findById(UUID id) {

        Order order = orderRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn(
                            "event=order_not_found orderId={}",
                            id
                    );

                    return new ResourceNotFoundException(
                            "Order not found with id: " + id
                    );
                });

        return OrderMapper.toResponse(order);
    }

    @Transactional
    public OrderResponse updateStatus(
            UUID id,
            UpdateOrderStatusRequest request) {

        Order order = orderRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn(
                            "event=order_not_found orderId={}",
                            id
                    );

                    return new ResourceNotFoundException(
                            "Order not found with id: " + id
                    );
                });

        OrderStatus previousStatus = order.getStatus();

        order.changeStatus(request.status());

        log.info(
                "event=order_status_changed orderId={} previousStatus={} newStatus={}",
                order.getId(),
                previousStatus,
                order.getStatus()
        );

        return OrderMapper.toResponse(order);
    }

    @Transactional(readOnly = true)
    public Page<OrderResponse> findAll(
            OrderStatus status,
            LocalDateTime from,
            LocalDateTime to,
            String origin,
            String destination,
            Pageable pageable) {

        Specification<Order> specification =
                OrderSpecification.hasStatus(status)
                        .and(OrderSpecification.createdFrom(from))
                        .and(OrderSpecification.createdTo(to))
                        .and(OrderSpecification.originContains(origin))
                        .and(OrderSpecification.destinationContains(destination));

        return orderRepository
                .findAll(specification, pageable)
                .map(OrderMapper::toResponse);
    }

}