package com.example.kulkita.exception;

/**
 * Exception thrown when rate limits are exceeded
 */
public class RateLimitExceededException extends BusinessException {

    public RateLimitExceededException(String operation, String timeWindow) {
        super(
                "RATE_LIMIT_EXCEEDED",
                String.format("Rate limit exceeded for %s. Please try again after %s", operation, timeWindow),
                operation, timeWindow
        );
    }

    public static RateLimitExceededException otpRequests(String email, String timeWindow) {
        return new RateLimitExceededException(
                "OTP requests for " + email,
                timeWindow
        );
    }
}