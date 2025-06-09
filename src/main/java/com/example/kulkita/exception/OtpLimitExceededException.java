package com.example.kulkita.exception;

public class OtpLimitExceededException extends RuntimeException {
    public OtpLimitExceededException(String message) {
        super(message);
    }

    public OtpLimitExceededException(String message, Throwable cause) {
        super(message, cause);
    }
}