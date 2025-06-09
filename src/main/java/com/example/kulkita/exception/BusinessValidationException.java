package com.example.kulkita.exception;

import java.math.BigDecimal;
import java.time.Instant;

/**
 * Exception thrown when business validation rules are violated
 */
public class BusinessValidationException extends BusinessException {

    public BusinessValidationException(String message, Object... parameters) {
        super("BUSINESS_VALIDATION_ERROR", message, parameters);
    }

    public BusinessValidationException(String message, Throwable cause, Object... parameters) {
        super("BUSINESS_VALIDATION_ERROR", message, cause, parameters);
    }

    // Factory methods for common validations
    public static BusinessValidationException insufficientStock(String batchCode,
                                                                BigDecimal available,
                                                                BigDecimal requested) {
        return new BusinessValidationException(
                "Insufficient stock in batch %s. Available: %s, Requested: %s",
                batchCode, available, requested
        );
    }

    public static BusinessValidationException inactiveBatch(String batchCode) {
        return new BusinessValidationException(
                "Batch %s is not active and cannot be used",
                batchCode
        );
    }

    public static BusinessValidationException expiredBatch(String batchCode, Instant expiryDate) {
        return new BusinessValidationException(
                "Batch %s has expired on %s",
                batchCode, expiryDate
        );
    }

    public static BusinessValidationException invalidWeight(BigDecimal weight) {
        return new BusinessValidationException(
                "Weight must be greater than zero. Provided: %s",
                weight
        );
    }
}