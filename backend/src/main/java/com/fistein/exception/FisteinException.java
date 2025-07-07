package com.fistein.exception;

public class FisteinException extends RuntimeException {
    
    private final ErrorType errorType;
    
    public FisteinException(String message) {
        super(message);
        this.errorType = ErrorType.BAD_REQUEST;
    }
    
    public FisteinException(String message, ErrorType errorType) {
        super(message);
        this.errorType = errorType;
    }
    
    public FisteinException(String message, Throwable cause) {
        super(message, cause);
        this.errorType = ErrorType.INTERNAL_ERROR;
    }
    
    public ErrorType getErrorType() {
        return errorType;
    }
    
    public enum ErrorType {
        NOT_FOUND,
        UNAUTHORIZED,
        FORBIDDEN,
        BAD_REQUEST,
        CONFLICT,
        INTERNAL_ERROR
    }
}