package com.example.kulkita.exception;

/**
 * Exception thrown when external service integration fails
 */
public class ExternalServiceException extends BusinessException {

    public ExternalServiceException(String serviceName, String message, Throwable cause) {
        super(
                "EXTERNAL_SERVICE_ERROR",
                String.format("External service '%s' error: %s", serviceName, message),
                cause,
                serviceName
        );
    }

    // Factory methods for specific services
    public static ExternalServiceException azureMLUnavailable(String endpoint, Throwable cause) {
        return new ExternalServiceException(
                "Azure ML",
                "Prediction service unavailable at " + endpoint,
                cause
        );
    }

    public static ExternalServiceException firebaseError(String operation, Throwable cause) {
        return new ExternalServiceException(
                "Firebase",
                "Firebase operation failed: " + operation,
                cause
        );
    }

    public static ExternalServiceException emailServiceError(String recipient, Throwable cause) {
        return new ExternalServiceException(
                "Email Service",
                "Failed to send email to " + recipient,
                cause
        );
    }
}