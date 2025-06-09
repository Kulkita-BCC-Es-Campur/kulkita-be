package com.example.kulkita.exception;

import com.example.kulkita.dto.response.ErrorResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import jakarta.servlet.http.HttpServletRequest;
import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    // =================================================================
    // BUSINESS EXCEPTIONS
    // =================================================================

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleEntityNotFound(
            EntityNotFoundException ex,
            HttpServletRequest request) {

        String traceId = generateTraceId();
        logError(ex, traceId, "Entity not found");

        ErrorResponse error = createErrorResponse(
                ex.getErrorCode(),
                ex.getMessage(),
                request,
                traceId
        );

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .contentType(MediaType.APPLICATION_JSON)
                .body(error);
    }

    @ExceptionHandler(BusinessValidationException.class)
    public ResponseEntity<ErrorResponse> handleBusinessValidation(
            BusinessValidationException ex,
            HttpServletRequest request) {

        String traceId = generateTraceId();
        logError(ex, traceId, "Business validation failed");

        ErrorResponse error = createErrorResponse(
                ex.getErrorCode(),
                ex.getMessage(),
                request,
                traceId
        );

        // Add validation details
        Map<String, Object> details = new HashMap<>();
        details.put("parameters", ex.getParameters());
        error.setDetails(details);

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .contentType(MediaType.APPLICATION_JSON)
                .body(error);
    }

    @ExceptionHandler(ExternalServiceException.class)
    public ResponseEntity<ErrorResponse> handleExternalService(
            ExternalServiceException ex,
            HttpServletRequest request) {

        String traceId = generateTraceId();
        logError(ex, traceId, "External service error");

        ErrorResponse error = createErrorResponse(
                ex.getErrorCode(),
                "External service temporarily unavailable. Please try again later.",
                request,
                traceId
        );

        return ResponseEntity
                .status(HttpStatus.SERVICE_UNAVAILABLE)
                .contentType(MediaType.APPLICATION_JSON)
                .body(error);
    }

    @ExceptionHandler(SecurityException.class)
    public ResponseEntity<ErrorResponse> handleSecurity(
            SecurityException ex,
            HttpServletRequest request) {

        String traceId = generateTraceId();
        logError(ex, traceId, "Security error");

        ErrorResponse error = createErrorResponse(
                ex.getErrorCode(),
                ex.getMessage(),
                request,
                traceId
        );

        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .contentType(MediaType.APPLICATION_JSON)
                .body(error);
    }

    @ExceptionHandler(RateLimitExceededException.class)
    public ResponseEntity<ErrorResponse> handleRateLimit(
            RateLimitExceededException ex,
            HttpServletRequest request) {

        String traceId = generateTraceId();
        logError(ex, traceId, "Rate limit exceeded");

        ErrorResponse error = createErrorResponse(
                ex.getErrorCode(),
                ex.getMessage(),
                request,
                traceId
        );

        return ResponseEntity
                .status(HttpStatus.TOO_MANY_REQUESTS)
                .contentType(MediaType.APPLICATION_JSON)
                .body(error);
    }

    // =================================================================
    // SPRING SECURITY EXCEPTIONS
    // =================================================================

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ErrorResponse> handleBadCredentials(
            BadCredentialsException ex,
            HttpServletRequest request) {

        String traceId = generateTraceId();
        logError(ex, traceId, "Authentication failed");

        ErrorResponse error = createErrorResponse(
                "INVALID_CREDENTIALS",
                "Invalid email or password",
                request,
                traceId
        );

        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .contentType(MediaType.APPLICATION_JSON)
                .body(error);
    }

    @ExceptionHandler(DisabledException.class)
    public ResponseEntity<ErrorResponse> handleDisabled(
            DisabledException ex,
            HttpServletRequest request) {

        String traceId = generateTraceId();
        logError(ex, traceId, "Account disabled");

        ErrorResponse error = createErrorResponse(
                "ACCOUNT_DISABLED",
                "Account is not verified. Please check your email for verification instructions.",
                request,
                traceId
        );

        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .contentType(MediaType.APPLICATION_JSON)
                .body(error);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccessDenied(
            AccessDeniedException ex,
            HttpServletRequest request) {

        String traceId = generateTraceId();
        logError(ex, traceId, "Access denied");

        ErrorResponse error = createErrorResponse(
                "ACCESS_DENIED",
                "You don't have permission to access this resource",
                request,
                traceId
        );

        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .contentType(MediaType.APPLICATION_JSON)
                .body(error);
    }

    // =================================================================
    // VALIDATION EXCEPTIONS
    // =================================================================

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(
            MethodArgumentNotValidException ex,
            HttpServletRequest request) {

        String traceId = generateTraceId();
        logError(ex, traceId, "Validation failed");

        List<ErrorResponse.FieldError> fieldErrors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(this::mapFieldError)
                .collect(Collectors.toList());

        ErrorResponse error = createErrorResponse(
                "VALIDATION_FAILED",
                "Request validation failed",
                request,
                traceId
        );
        error.setFieldErrors(fieldErrors);

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .contentType(MediaType.APPLICATION_JSON)
                .body(error);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgument(
            IllegalArgumentException ex,
            HttpServletRequest request) {

        String traceId = generateTraceId();
        logError(ex, traceId, "Invalid argument");

        ErrorResponse error = createErrorResponse(
                "INVALID_ARGUMENT",
                ex.getMessage(),
                request,
                traceId
        );

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .contentType(MediaType.APPLICATION_JSON)
                .body(error);
    }

    // =================================================================
    // GENERIC EXCEPTIONS
    // =================================================================

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorResponse> handleRuntimeException(
            RuntimeException ex,
            HttpServletRequest request) {

        String traceId = generateTraceId();
        logError(ex, traceId, "Runtime exception");

        ErrorResponse error = createErrorResponse(
                "INTERNAL_SERVER_ERROR",
                "An unexpected error occurred. Please try again later.",
                request,
                traceId
        );

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .contentType(MediaType.APPLICATION_JSON)
                .body(error);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(
            Exception ex,
            HttpServletRequest request) {

        String traceId = generateTraceId();
        logError(ex, traceId, "Unexpected exception");

        ErrorResponse error = createErrorResponse(
                "INTERNAL_SERVER_ERROR",
                "An unexpected error occurred. Please try again later.",
                request,
                traceId
        );

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .contentType(MediaType.APPLICATION_JSON)
                .body(error);
    }

    // =================================================================
    // HELPER METHODS
    // =================================================================

    private ErrorResponse createErrorResponse(String errorCode, String message,
                                              HttpServletRequest request, String traceId) {
        ErrorResponse error = new ErrorResponse(errorCode, message);
        error.setPath(request.getRequestURI());
        error.setMethod(request.getMethod());
        error.setTimestamp(Instant.now());
        error.setTraceId(traceId);
        return error;
    }

    private ErrorResponse.FieldError mapFieldError(FieldError fieldError) {
        return new ErrorResponse.FieldError(
                fieldError.getField(),
                fieldError.getRejectedValue(),
                fieldError.getDefaultMessage()
        );
    }

    private String generateTraceId() {
        return UUID.randomUUID().toString().substring(0, 8);
    }

    private void logError(Exception ex, String traceId, String context) {
        MDC.put("traceId", traceId);

        if (ex instanceof BusinessException) {
            log.warn("[{}] {}: {}", traceId, context, ex.getMessage());
        } else {
            log.error("[{}] {}: {}", traceId, context, ex.getMessage(), ex);
        }

        MDC.clear();
    }
}