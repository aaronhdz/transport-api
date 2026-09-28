package com.transport.api.assignment.mapper;

import com.transport.api.assignment.dto.AssignmentResponse;
import com.transport.api.assignment.entity.OrderAssignment;

public final class AssignmentMapper {

    private AssignmentMapper() {
    }

    public static AssignmentResponse toResponse(
            OrderAssignment assignment) {

        return new AssignmentResponse(
                assignment.getId(),
                assignment.getOrder().getId(),
                assignment.getDriver().getId(),
                assignment.getAssignedAt(),
                assignment.getDocumentPath(),
                assignment.getImagePath()
        );
    }
}