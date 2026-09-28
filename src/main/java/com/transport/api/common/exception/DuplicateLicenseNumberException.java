package com.transport.api.common.exception;

public class DuplicateLicenseNumberException extends RuntimeException {

    public DuplicateLicenseNumberException(String licenseNumber) {
        super("Driver already exists with license number: " + licenseNumber);
    }
}