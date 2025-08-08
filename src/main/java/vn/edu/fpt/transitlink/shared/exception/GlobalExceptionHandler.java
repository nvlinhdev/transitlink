package vn.edu.fpt.transitlink.shared.exception;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.TypeMismatchException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import vn.edu.fpt.transitlink.shared.dto.ErrorResponse;
import vn.edu.fpt.transitlink.shared.dto.ValidationError;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Global exception handler for the application.
 * This class handles various exceptions and returns standardized error responses.
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    // Handle business exceptions
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ErrorResponse> handleBusinessException(BusinessException ex, HttpServletRequest request) {
        log.info("Business exception - Code: {}, Path: {}, Message: {}",
                ex.getErrorCode().getCode(),
                request.getRequestURI(),
                ex.getMessage());

        ErrorResponse errorResponse = new ErrorResponse(
                ex.getMessage(),
                ex.getErrorCode().getCode(),
                request.getRequestURI(),
                ex.getErrorCode().getHttpStatus().value()
        );

        return ResponseEntity.status(ex.getErrorCode().getHttpStatus()).body(errorResponse);
    }

    @ExceptionHandler(SystemException.class)
    public ResponseEntity<ErrorResponse> handleSystemException(SystemException ex, HttpServletRequest request) {
        log.error("System exception - Path: {}, Code: {}, Message: {}",
                request.getRequestURI(),
                ex.getErrorCode().getCode(),
                ex.getMessage(), ex);

        ErrorResponse errorResponse = new ErrorResponse(
                ex.getMessage(),
                ex.getErrorCode().getCode(),
                request.getRequestURI(),
                ex.getErrorCode().getHttpStatus().value()
        );

        return ResponseEntity.status(ex.getErrorCode().getHttpStatus()).body(errorResponse);
    }

    @ExceptionHandler(ThirdPartyException.class)
    public ResponseEntity<ErrorResponse> handleThirdPartyException(ThirdPartyException ex, HttpServletRequest request) {
        log.warn("Third-party exception - Path: {}, Code: {}, Message: {}",
                request.getRequestURI(),
                ex.getErrorCode().getCode(),
                ex.getMessage(), ex);

        ErrorResponse errorResponse = new ErrorResponse(
                ex.getMessage(),
                ex.getErrorCode().getCode(),
                request.getRequestURI(),
                ex.getErrorCode().getHttpStatus().value()
        );

        return ResponseEntity.status(ex.getErrorCode().getHttpStatus()).body(errorResponse);
    }


    // Handle Unexpected exceptions
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleUnexpectedException(Exception ex, HttpServletRequest request) {
        // Unexpected exceptions are serious issues, use ERROR level with full stack trace
        log.error("Unexpected exception occurred - Path: {}, Exception: {}",
                request.getRequestURI(), ex.getClass().getSimpleName(), ex);

        ErrorResponse errorResponse = new ErrorResponse(
                "Unexpected exception occurred",
                ex.getClass().getSimpleName(),
                request.getRequestURI(),
                HttpStatus.INTERNAL_SERVER_ERROR.value()
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    // Handle validation errors (@Valid annotation)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException ex, HttpServletRequest request) {
        // Validation errors are client errors, use INFO level
        log.info("Validation failed - Path: {}, Fields: {}",
                request.getRequestURI(),
                ex.getBindingResult().getFieldErrors().stream()
                        .map(error -> error.getField())
                        .collect(Collectors.joining(", ")));

        List<ValidationError> validationErrors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> new ValidationError(
                        error.getField(),
                        error.getDefaultMessage(),
                        error.getRejectedValue()
                ))
                .toList();

        // Also create a summary message
        String errorSummary = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.joining("; "));

        ErrorResponse errorResponse = new ErrorResponse(
                "Validation failed: " + errorSummary,
                "MethodArgumentNotValidException",
                request.getRequestURI(),
                HttpStatus.BAD_REQUEST.value(),
                validationErrors
        );

        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    // Handle constraint violations (@Validated annotation)
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponse> handleConstraintViolation(ConstraintViolationException ex, HttpServletRequest request) {
        // Constraint violations are client errors, use INFO level
        log.info("Constraint violation - Path: {}, Violations: {}",
                request.getRequestURI(),
                ex.getConstraintViolations().stream()
                        .map(cv -> cv.getPropertyPath().toString())
                        .collect(Collectors.joining(", ")));

        List<ValidationError> validationErrors = ex.getConstraintViolations()
                .stream()
                .map(cv -> new ValidationError(
                        cv.getPropertyPath().toString(),
                        cv.getMessage(),
                        cv.getInvalidValue()
                ))
                .toList();

        String errorSummary = ex.getConstraintViolations()
                .stream()
                .map(cv -> cv.getPropertyPath() + ": " + cv.getMessage())
                .collect(Collectors.joining("; "));

        ErrorResponse errorResponse = new ErrorResponse(
                "Validation failed: " + errorSummary,
                "ConstraintViolationException",
                request.getRequestURI(),
                HttpStatus.BAD_REQUEST.value(),
                validationErrors
        );

        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    // Handle Spring Security Access Denied
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccessDenied(AccessDeniedException ex, HttpServletRequest request) {
        // Security violations should be logged for monitoring, use WARN level
        log.warn("Access denied - Path: {}, User: {}, Message: {}",
                request.getRequestURI(),
                request.getRemoteUser() != null ? request.getRemoteUser() : "anonymous",
                ex.getMessage());

        ErrorResponse errorResponse = new ErrorResponse(
                "Access denied - insufficient permissions",
                "AccessDeniedException",
                request.getRequestURI(),
                HttpStatus.FORBIDDEN.value()
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.FORBIDDEN);
    }

    // Handle Spring Security Authorization Denied
    @ExceptionHandler(AuthorizationDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAuthorizationDenied(AuthorizationDeniedException ex, HttpServletRequest request) {
        // Security violations should be logged for monitoring, use WARN level
        log.warn("Authorization denied - Path: {}, User: {}, Message: {}",
                request.getRequestURI(),
                request.getRemoteUser() != null ? request.getRemoteUser() : "anonymous",
                ex.getMessage());

        ErrorResponse errorResponse = new ErrorResponse(
                ex.getMessage() != null ? ex.getMessage() : "Authorization denied",
                "AuthorizationDeniedException",
                request.getRequestURI(),
                HttpStatus.FORBIDDEN.value()
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.FORBIDDEN);
    }

    // Handle unsupported HTTP methods
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ErrorResponse> handleMethodNotSupported(HttpRequestMethodNotSupportedException ex, HttpServletRequest request) {
        // Client configuration errors, use INFO level
        log.info("Method not supported - Path: {}, Method: {}, Supported: {}",
                request.getRequestURI(),
                ex.getMethod(),
                ex.getSupportedHttpMethods());

        String supportedMethods = ex.getSupportedHttpMethods() != null ?
                ex.getSupportedHttpMethods().toString() : "N/A";
        String message = String.format("Method '%s' not supported. Supported methods: %s",
                ex.getMethod(), supportedMethods);

        ErrorResponse errorResponse = new ErrorResponse(
                message,
                "HttpRequestMethodNotSupportedException",
                request.getRequestURI(),
                HttpStatus.METHOD_NOT_ALLOWED.value()
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.METHOD_NOT_ALLOWED);
    }

    // Handle malformed JSON
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleHttpMessageNotReadable(HttpMessageNotReadableException ex, HttpServletRequest request) {
        // Client input errors, use INFO level
        log.info("Malformed JSON request - Path: {}, Cause: {}",
                request.getRequestURI(),
                ex.getCause() != null ? ex.getCause().getClass().getSimpleName() : "Unknown");

        String message = "Malformed JSON request or invalid data format";
        if (ex.getCause() instanceof JsonParseException) {
            message = "Invalid JSON format: " + ex.getCause().getMessage();
        } else if (ex.getCause() instanceof JsonMappingException) {
            message = "JSON mapping error: " + ex.getCause().getMessage();
        }

        ErrorResponse errorResponse = new ErrorResponse(
                message,
                "HttpMessageNotReadableException",
                request.getRequestURI(),
                HttpStatus.BAD_REQUEST.value()
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    // Handle missing request parameter
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ErrorResponse> handleMissingParams(MissingServletRequestParameterException ex, HttpServletRequest request) {
        // Client input errors, use INFO level
        log.info("Missing required parameter - Path: {}, Parameter: {} ({})",
                request.getRequestURI(),
                ex.getParameterName(),
                ex.getParameterType());

        String message = String.format("Missing required parameter: '%s' of type '%s'",
                ex.getParameterName(), ex.getParameterType());

        ErrorResponse errorResponse = new ErrorResponse(
                message,
                "MissingServletRequestParameterException",
                request.getRequestURI(),
                HttpStatus.BAD_REQUEST.value()
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    // Handle type mismatch (e.g., wrong param type)
    @ExceptionHandler(TypeMismatchException.class)
    public ResponseEntity<ErrorResponse> handleTypeMismatch(TypeMismatchException ex, HttpServletRequest request) {
        // Client input errors, use INFO level
        log.info("Type mismatch - Path: {}, Parameter: {}, Value: {}, Expected: {}",
                request.getRequestURI(),
                ex.getPropertyName(),
                ex.getValue(),
                ex.getRequiredType() != null ? ex.getRequiredType().getSimpleName() : "unknown");

        String message = String.format("Invalid value '%s' for parameter '%s'. Expected type: %s",
                ex.getValue(), ex.getPropertyName(),
                ex.getRequiredType() != null ? ex.getRequiredType().getSimpleName() : "unknown");

        ErrorResponse errorResponse = new ErrorResponse(
                message,
                "TypeMismatchException",
                request.getRequestURI(),
                HttpStatus.BAD_REQUEST.value()
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    // Handle illegal argument exceptions
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgument(IllegalArgumentException ex, HttpServletRequest request) {
        // Could be client or server issue, use WARN level
        log.warn("Illegal argument - Path: {}, Message: {}",
                request.getRequestURI(),
                ex.getMessage());

        ErrorResponse errorResponse = new ErrorResponse(
                ex.getMessage(),
                "IllegalArgumentException",
                request.getRequestURI(),
                HttpStatus.BAD_REQUEST.value()
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    // Handle data integrity violations
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponse> handleDataIntegrityViolation(DataIntegrityViolationException ex, HttpServletRequest request) {
        // Database issues should be monitored, use WARN level
        log.warn("Data integrity violation - Path: {}, Cause: {}",
                request.getRequestURI(),
                ex.getCause() != null ? ex.getCause().getClass().getSimpleName() : "Unknown");

        String message = "Data integrity violation";
        if (ex.getCause() instanceof ConstraintViolationException) {
            message = "Database constraint violation - possible duplicate entry or foreign key constraint";
        }

        ErrorResponse errorResponse = new ErrorResponse(
                message,
                "DataIntegrityViolationException",
                request.getRequestURI(),
                HttpStatus.CONFLICT.value()
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.CONFLICT);
    }

    // Handle HTTP media type not supported
    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public ResponseEntity<ErrorResponse> handleMediaTypeNotSupported(HttpMediaTypeNotSupportedException ex, HttpServletRequest request) {
        // Client configuration errors, use INFO level
        log.info("Media type not supported - Path: {}, Content-Type: {}, Supported: {}",
                request.getRequestURI(),
                ex.getContentType(),
                ex.getSupportedMediaTypes());

        String supportedTypes = ex.getSupportedMediaTypes().toString();
        String message = String.format("Media type '%s' not supported. Supported types: %s",
                ex.getContentType(), supportedTypes);

        ErrorResponse errorResponse = new ErrorResponse(
                message,
                "HttpMediaTypeNotSupportedException",
                request.getRequestURI(),
                HttpStatus.UNSUPPORTED_MEDIA_TYPE.value()
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.UNSUPPORTED_MEDIA_TYPE);
    }
}