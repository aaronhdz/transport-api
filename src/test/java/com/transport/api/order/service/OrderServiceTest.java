package com.transport.api.order.service;

import com.transport.api.common.exception.ResourceNotFoundException;
import com.transport.api.order.dto.CreateOrderRequest;
import com.transport.api.order.dto.OrderResponse;
import com.transport.api.order.dto.UpdateOrderStatusRequest;
import com.transport.api.order.entity.Order;
import com.transport.api.order.entity.OrderStatus;
import com.transport.api.order.repository.OrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @InjectMocks
    private OrderService orderService;

    private UUID orderId;
    private Order order;

    @BeforeEach
    void setUp() {
        orderId = UUID.randomUUID();

        order = Order.create(
                "Mexico City",
                "Puebla"
        );
    }

    @Test
    void shouldCreateOrderSuccessfully() {

        CreateOrderRequest request =
                new CreateOrderRequest(
                        "Mexico City",
                        "Puebla"
                );

        when(orderRepository.save(any(Order.class)))
                .thenAnswer(invocation ->
                        invocation.getArgument(0)
                );

        OrderResponse response =
                orderService.create(request);

        assertNotNull(response);
        assertEquals(
                OrderStatus.CREATED,
                response.status()
        );
        assertEquals(
                "Mexico City",
                response.origin()
        );
        assertEquals(
                "Puebla",
                response.destination()
        );

        verify(orderRepository)
                .save(any(Order.class));
    }

    @Test
    void shouldFindOrderByIdSuccessfully() {

        when(orderRepository.findById(orderId))
                .thenReturn(Optional.of(order));

        OrderResponse response =
                orderService.findById(orderId);

        assertNotNull(response);
        assertEquals(
                OrderStatus.CREATED,
                response.status()
        );
        assertEquals(
                "Mexico City",
                response.origin()
        );

        verify(orderRepository)
                .findById(orderId);
    }

    @Test
    void shouldThrowExceptionWhenOrderDoesNotExist() {

        when(orderRepository.findById(orderId))
                .thenReturn(Optional.empty());

        ResourceNotFoundException exception =
                assertThrows(
                        ResourceNotFoundException.class,
                        () -> orderService.findById(orderId)
                );

        assertTrue(
                exception.getMessage()
                        .contains(orderId.toString())
        );

        verify(orderRepository)
                .findById(orderId);
    }

    @Test
    void shouldChangeStatusFromCreatedToInTransit() {

        when(orderRepository.findById(orderId))
                .thenReturn(Optional.of(order));

        UpdateOrderStatusRequest request =
                new UpdateOrderStatusRequest(
                        OrderStatus.IN_TRANSIT
                );

        OrderResponse response =
                orderService.updateStatus(
                        orderId,
                        request
                );

        assertEquals(
                OrderStatus.IN_TRANSIT,
                response.status()
        );

        verify(orderRepository)
                .findById(orderId);
    }
}