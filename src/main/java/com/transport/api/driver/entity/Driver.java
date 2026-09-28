package com.transport.api.driver.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "drivers")
public class Driver {

    @Id
    private UUID id;

    @Column(nullable = false, length = 150)
    private String name;

    @Column(name = "license_number", nullable = false, unique = true, length = 50)
    private String licenseNumber;

    @Column(nullable = false)
    private boolean active;

    public static Driver create(
            String name,
            String licenseNumber) {

        return Driver.builder()
                .id(UUID.randomUUID())
                .name(name)
                .licenseNumber(licenseNumber)
                .active(true)
                .build();
    }

}