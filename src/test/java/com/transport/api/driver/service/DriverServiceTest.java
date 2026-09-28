package com.transport.api.driver.service;

import com.transport.api.common.exception.DuplicateLicenseNumberException;
import com.transport.api.driver.dto.CreateDriverRequest;
import com.transport.api.driver.dto.DriverResponse;
import com.transport.api.driver.entity.Driver;
import com.transport.api.driver.repository.DriverRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DriverServiceTest {

    @Mock
    private DriverRepository driverRepository;

    @InjectMocks
    private DriverService driverService;

    private Driver driver;

    @BeforeEach
    void setUp() {
        driver = Driver.create(
                "Carlos Ramirez",
                "LIC-2026-0001"
        );
    }

    @Test
    void shouldCreateDriverSuccessfully() {

        CreateDriverRequest request =
                new CreateDriverRequest(
                        "Carlos Ramirez",
                        "LIC-2026-0001"
                );

        when(driverRepository.existsByLicenseNumber(
                request.licenseNumber()
        )).thenReturn(false);

        when(driverRepository.save(any(Driver.class)))
                .thenAnswer(invocation ->
                        invocation.getArgument(0)
                );

        DriverResponse response =
                driverService.create(request);

        assertNotNull(response);
        assertEquals(
                "Carlos Ramirez",
                response.name()
        );
        assertEquals(
                "LIC-2026-0001",
                response.licenseNumber()
        );
        assertTrue(response.active());

        verify(driverRepository)
                .existsByLicenseNumber(
                        "LIC-2026-0001"
                );

        verify(driverRepository)
                .save(any(Driver.class));
    }

    @Test
    void shouldRejectDuplicateLicenseNumber() {

        CreateDriverRequest request =
                new CreateDriverRequest(
                        "Carlos Ramirez",
                        "LIC-2026-0001"
                );

        when(driverRepository.existsByLicenseNumber(
                request.licenseNumber()
        )).thenReturn(true);

        assertThrows(
                DuplicateLicenseNumberException.class,
                () -> driverService.create(request)
        );

        verify(driverRepository)
                .existsByLicenseNumber(
                        "LIC-2026-0001"
                );

        verify(
                driverRepository,
                never()
        ).save(any(Driver.class));
    }

    @Test
    void shouldReturnOnlyActiveDrivers() {

        when(driverRepository.findAllByActiveTrue())
                .thenReturn(List.of(driver));

        List<DriverResponse> response =
                driverService.findAllActive();

        assertNotNull(response);
        assertEquals(1, response.size());

        assertEquals(
                "Carlos Ramirez",
                response.get(0).name()
        );

        assertTrue(
                response.get(0).active()
        );

        verify(driverRepository)
                .findAllByActiveTrue();
    }
}