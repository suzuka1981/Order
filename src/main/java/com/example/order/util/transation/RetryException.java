package com.example.order.util.transation;

public class RetryException extends RuntimeException {
    public RetryException(String message) {
        super(message);
    }
}