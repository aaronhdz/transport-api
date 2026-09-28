package com.transport.api.assignment.controller;

import com.transport.api.assignment.dto.AssignDriverRequest;
import com.transport.api.assignment.dto.AssignmentResponse;
import com.transport.api.assignment.service.AssignmentService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
public class AssignmentController {

    private final AssignmentService assignmentService;

    @PostMapping("/{orderId}/assignments")
    public ResponseEntity<AssignmentResponse> assignDriver(
            @PathVariable UUID orderId,
            @Valid @RequestBody AssignDriverRequest request) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        assignmentService.assignDriver(
                                orderId,
                                request
                        )
                );
    }

    @PostMapping(
            value = "/{orderId}/attachments/document",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ResponseEntity<AssignmentResponse> attachDocument(
            @PathVariable UUID orderId,
            @RequestPart("file") MultipartFile file) {

        return ResponseEntity.ok(
                assignmentService.attachDocument(orderId, file)
        );
    }

    @PostMapping(
            value = "/{orderId}/attachments/image",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ResponseEntity<AssignmentResponse> attachImage(
            @PathVariable UUID orderId,
            @RequestPart("file") MultipartFile file) {

        return ResponseEntity.ok(
                assignmentService.attachImage(orderId, file)
        );
    }

}