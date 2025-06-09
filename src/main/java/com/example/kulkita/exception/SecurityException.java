package com.example.kulkita.exception;

/**
 * Exception thrown when authentication or authorization fails
 */
public class SecurityException extends BusinessException {

    public SecurityException(String message, Object... parameters) {
        super("SECURITY_ERROR", message, parameters);
    }

    public static SecurityException invalidCredentials() {
        return new SecurityException("Invalid email or password");
    }

    public static SecurityException accountNotVerified() {
        return new SecurityException("Account is not verified. Please check your email for verification instructions.");
    }

    public static SecurityException tokenExpired() {
        return new SecurityException("Authentication token has expired");
    }

    public static SecurityException insufficientPermissions(String operation) {
        return new SecurityException("Insufficient permissions for operation: %s", operation);
    }
}