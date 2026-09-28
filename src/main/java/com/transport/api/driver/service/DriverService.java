package com.transport.api.driver.service;

import com.transport.api.common.exception.DuplicateLicenseNumberException;
import com.transport.api.driver.dto.CreateDriverRequest;
import com.transport.api.driver.dto.DriverResponse;
import com.transport.api.driver.entity.Driver;
import com.transport.api.driver.mapper.DriverMapper;
import com.transport.api.driver.repository.DriverRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class DriverService {

    private final DriverRepository driverRepository;

    @Transactional
    public DriverResponse create(CreateDriverRequest request) {

        if (driverRepository.existsByLicenseNumber(request.licenseNumber())) {

            log.warn(
                    "event=driver_creation_rejected reason=duplicate_license licenseNumber={}",
                    request.licenseNumber()
            );

            throw new DuplicateLicenseNumberException(
                    request.licenseNumber()
            );
        }

        Driver driver = Driver.create(
                request.name(),
                request.licenseNumber()
        );

        Driver savedDriver = driverRepository.save(driver);

        log.info(
                "event=driver_created driverId={} licenseNumber={} active={}",
                savedDriver.getId(),
                savedDriver.getLicenseNumber(),
                savedDriver.isActive()
        );

        return DriverMapper.toResponse(savedDriver);
    }

    @Transactional(readOnly = true)
    public List<DriverResponse> findAllActive() {
        return driverRepository.findAllByActiveTrue()
                .stream()
                .map(DriverMapper::toResponse)
                .toList();
    }
}