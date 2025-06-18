package com.example.simon.exception;

/**
 * 资源不存在异常
 */
public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String message) {
        super(message);
    }

    public ResourceNotFoundException(String resourceName, String fieldName, Object fieldValue) {
        super(String.format("%s不存在，%s: %s", resourceName, fieldName, fieldValue));
    }
}