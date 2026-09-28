package com.transport.api.assignment.service;

import com.transport.api.assignment.dto.AssignDriverRequest;
import com.transport.api.assignment.dto.AssignmentResponse;
import com.transport.api.assignment.entity.OrderAssignment;
import com.transport.api.assignment.mapper.AssignmentMapper;
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
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class AssignmentService {

    private final OrderRepository orderRepository;
    private final DriverRepository driverRepository;
    private final OrderAssignmentRepository assignmentRepository;
    private final FileStorageService fileStorageService;

    @Transactional
    public AssignmentResponse assignDriver(
            UUID orderId,
            AssignDriverRequest request) {

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> {
                    log.warn(
                            "event=assignment_rejected reason=order_not_found orderId={}",
                            orderId
                    );

                    return new ResourceNotFoundException(
                            "Order not found with id: " + orderId
                    );
                });

        if (order.getStatus() != OrderStatus.CREATED) {

            log.warn(
                    "event=assignment_rejected reason=invalid_order_status orderId={} status={}",
                    orderId,
                    order.getStatus()
            );

            throw new OrderNotAssignableException(
                    order.getStatus()
            );
        }

        Driver driver = driverRepository.findById(request.driverId())
                .orElseThrow(() -> {
                    log.warn(
                            "event=assignment_rejected reason=driver_not_found orderId={} driverId={}",
                            orderId,
                            request.driverId()
                    );

                    return new ResourceNotFoundException(
                            "Driver not found with id: "
                                    + request.driverId()
                    );
                });

        if (!driver.isActive()) {

            log.warn(
                    "event=assignment_rejected reason=inactive_driver orderId={} driverId={}",
                    orderId,
                    driver.getId()
            );

            throw new InactiveDriverException(
                    driver.getId()
            );
        }

        if (assignmentRepository.existsByOrderId(orderId)) {

            log.warn(
                    "event=assignment_rejected reason=order_already_assigned orderId={} driverId={}",
                    orderId,
                    driver.getId()
            );

            throw new OrderAlreadyAssignedException(orderId);
        }

        OrderAssignment assignment =
                OrderAssignment.create(order, driver);

        OrderAssignment savedAssignment =
                assignmentRepository.save(assignment);

        log.info(
                "event=driver_assigned assignmentId={} orderId={} driverId={}",
                savedAssignment.getId(),
                orderId,
                driver.getId()
        );

        return AssignmentMapper.toResponse(savedAssignment);
    }

    @Transactional
    public AssignmentResponse attachDocument(
            UUID orderId,
            MultipartFile file) {

        OrderAssignment assignment = findAssignmentByOrderId(orderId);

        String documentPath = fileStorageService.storeDocument(
                assignment.getId(),
                file
        );

        assignment.attachDocument(documentPath);

        log.info(
                "event=document_attached assignmentId={} orderId={} path={}",
                assignment.getId(),
                orderId,
                documentPath
        );

        return AssignmentMapper.toResponse(assignment);
    }

    @Transactional
    public AssignmentResponse attachImage(
            UUID orderId,
            MultipartFile file) {

        OrderAssignment assignment = findAssignmentByOrderId(orderId);

        String imagePath = fileStorageService.storeImage(
                assignment.getId(),
                file
        );

        assignment.attachImage(imagePath);

        log.info(
                "event=image_attached assignmentId={} orderId={} path={}",
                assignment.getId(),
                orderId,
                imagePath
        );

        return AssignmentMapper.toResponse(assignment);
    }

    private OrderAssignment findAssignmentByOrderId(UUID orderId) {

        return assignmentRepository.findByOrderId(orderId)
                .orElseThrow(() -> {
                    log.warn(
                            "event=attachment_rejected reason=assignment_not_found orderId={}",
                            orderId
                    );

                    return new ResourceNotFoundException(
                            "Assignment not found for order id: " + orderId
                    );
                });
    }
}