package com.transport.api.driver.mapper;

import com.transport.api.driver.dto.DriverResponse;
import com.transport.api.driver.entity.Driver;

public final class DriverMapper {

    private DriverMapper() {
    }

    public static DriverResponse toResponse(Driver driver) {
        return new DriverResponse(
                driver.getId(),
                driver.getName(),
                driver.getLicenseNumber(),
                driver.isActive()
        );
    }
}