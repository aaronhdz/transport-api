package com.transport.api.common.exception;

import java.util.UUID;

public class InactiveDriverException extends RuntimeException {

    public InactiveDriverException(UUID driverId) {
        super("Driver is inactive with id: " + driverId);
    }
}