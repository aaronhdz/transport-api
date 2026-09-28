package com.transport.api.order.controller;

import com.transport.api.order.dto.CreateOrderRequest;
import com.transport.api.order.dto.OrderResponse;
import com.transport.api.order.dto.UpdateOrderStatusRequest;
import com.transport.api.order.entity.OrderStatus;
import com.transport.api.order.service.OrderService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    public ResponseEntity<OrderResponse> create(
            @Valid @RequestBody CreateOrderRequest request) {

        OrderResponse response = orderService.create(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderResponse> findById(
            @PathVariable UUID id) {

        return ResponseEntity.ok(
                orderService.findById(id)
        );
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<OrderResponse> updateStatus(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateOrderStatusRequest request) {

        return ResponseEntity.ok(
                orderService.updateStatus(id, request)
        );
    }

    @GetMapping
    public ResponseEntity<Page<OrderResponse>> findAll(
            @RequestParam(required = false) OrderStatus status,
            @RequestParam(required = false) LocalDateTime createdFrom,
            @RequestParam(required = false) LocalDateTime createdTo,
            @RequestParam(required = false) String origin,
            @RequestParam(required = false) String destination,
            @ParameterObject Pageable pageable) {

        return ResponseEntity.ok(
                orderService.findAll(
                        status,
                        createdFrom,
                        createdTo,
                        origin,
                        destination,
                        pageable
                )
        );
    }

}