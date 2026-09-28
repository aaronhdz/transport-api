package com.transport.api.assignment.service;

import com.transport.api.assignment.dto.AssignDriverRequest;
import com.transport.api.assignment.dto.AssignmentResponse;
import com.transport.api.assignment.entity.OrderAssignment;
import com.transport.api.assignment.repository.OrderAssignmentRepository;
import com.transport.api.common.exception.InactiveDriverException;
import com.transport.api.common.exception.OrderAlreadyAssignedException;
import com.transport.api.common.exception.OrderNotAssignableException;
import com.transport.api.common.exception.ResourceNotFoundException;
import com.transport.api.driver.entity.Driver;
import com.transport.api.driver.repository.DriverRepository;
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
class AssignmentServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private DriverRepository driverRepository;

    @Mock
    private OrderAssignmentRepository assignmentRepository;

    @Mock
    private FileStorageService fileStorageService;

    @InjectMocks
    private AssignmentService assignmentService;

    private UUID orderId;
    private UUID driverId;
    private Order order;
    private Driver driver;

    @BeforeEach
    void setUp() {

        orderId = UUID.randomUUID();
        driverId = UUID.randomUUID();

        order = Order.create(
                "Mexico City",
                "Puebla"
        );

        driver = Driver.create(
                "Carlos Ramirez",
                "LIC-2026-0001"
        );
    }

    @Test
    void shouldAssignActiveDriverToCreatedOrder() {

        AssignDriverRequest request =
                new AssignDriverRequest(driverId);

        when(orderRepository.findById(orderId))
                .thenReturn(Optional.of(order));

        when(driverRepository.findById(driverId))
                .thenReturn(Optional.of(driver));

        when(assignmentRepository.existsByOrderId(orderId))
                .thenReturn(false);

        when(assignmentRepository.save(any(OrderAssignment.class)))
                .thenAnswer(invocation ->
                        invocation.getArgument(0)
                );

        AssignmentResponse response =
                assignmentService.assignDriver(
                        orderId,
                        request
                );

        assertNotNull(response);

        verify(orderRepository)
                .findById(orderId);

        verify(driverRepository)
                .findById(driverId);

        verify(assignmentRepository)
                .existsByOrderId(orderId);

        verify(assignmentRepository)
                .save(any(OrderAssignment.class));
    }

    @Test
    void shouldRejectAssignmentWhenOrderDoesNotExist() {

        AssignDriverRequest request =
                new AssignDriverRequest(driverId);

        when(orderRepository.findById(orderId))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> assignmentService.assignDriver(
                        orderId,
                        request
                )
        );

        verify(driverRepository, never())
                .findById(any());

        verify(assignmentRepository, never())
                .save(any());
    }

    @Test
    void shouldRejectAssignmentWhenOrderIsNotCreated() {

        order.changeStatus(OrderStatus.IN_TRANSIT);

        AssignDriverRequest request =
                new AssignDriverRequest(driverId);

        when(orderRepository.findById(orderId))
                .thenReturn(Optional.of(order));

        assertThrows(
                OrderNotAssignableException.class,
                () -> assignmentService.assignDriver(
                        orderId,
                        request
                )
        );

        verify(driverRepository, never())
                .findById(any());

        verify(assignmentRepository, never())
                .save(any());
    }

    @Test
    void shouldRejectAssignmentWhenDriverDoesNotExist() {

        AssignDriverRequest request =
                new AssignDriverRequest(driverId);

        when(orderRepository.findById(orderId))
                .thenReturn(Optional.of(order));

        when(driverRepository.findById(driverId))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> assignmentService.assignDriver(
                        orderId,
                        request
                )
        );

        verify(assignmentRepository, never())
                .save(any());
    }

    @Test
    void shouldRejectAlreadyAssignedOrder() {

        AssignDriverRequest request =
                new AssignDriverRequest(driverId);

        when(orderRepository.findById(orderId))
                .thenReturn(Optional.of(order));

        when(driverRepository.findById(driverId))
                .thenReturn(Optional.of(driver));

        when(assignmentRepository.existsByOrderId(orderId))
                .thenReturn(true);

        assertThrows(
                OrderAlreadyAssignedException.class,
                () -> assignmentService.assignDriver(
                        orderId,
                        request
                )
        );

        verify(assignmentRepository, never())
                .save(any());
    }

    @Test
    void shouldRejectAssignmentWhenDriverIsInactive() {

        Driver inactiveDriver = Driver.builder()
                .id(driverId)
                .name("Carlos Ramirez")
                .licenseNumber("LIC-2026-0001")
                .active(false)
                .build();

        AssignDriverRequest request =
                new AssignDriverRequest(driverId);

        when(orderRepository.findById(orderId))
                .thenReturn(Optional.of(order));

        when(driverRepository.findById(driverId))
                .thenReturn(Optional.of(inactiveDriver));

        assertThrows(
                InactiveDriverException.class,
                () -> assignmentService.assignDriver(
                        orderId,
                        request
                )
        );

        verify(orderRepository)
                .findById(orderId);

        verify(driverRepository)
                .findById(driverId);

        verify(assignmentRepository, never())
                .existsByOrderId(any());

        verify(assignmentRepository, never())
                .save(any());
    }
}