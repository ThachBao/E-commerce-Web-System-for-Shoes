package com.CongNgheJave.ecommerce_system.exception;

public class UnauthorizedOrderAccessException extends RuntimeException {

    public UnauthorizedOrderAccessException(String message) {
        super(message);
    }
}
