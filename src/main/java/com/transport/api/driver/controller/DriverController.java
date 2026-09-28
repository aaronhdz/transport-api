package com.transport.api.driver.controller;

import com.transport.api.driver.dto.CreateDriverRequest;
import com.transport.api.driver.dto.DriverResponse;
import com.transport.api.driver.service.DriverService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/drivers")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
public class DriverController {

    private final DriverService driverService;

    @PostMapping
    public ResponseEntity<DriverResponse> create(
            @Valid @RequestBody CreateDriverRequest request) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(driverService.create(request));
    }

    @GetMapping("/active")
    public ResponseEntity<List<DriverResponse>> findAllActive() {
        return ResponseEntity.ok(
                driverService.findAllActive()
        );
    }
}