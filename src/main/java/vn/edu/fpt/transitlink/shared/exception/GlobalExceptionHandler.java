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
import vn.edu.fpt.transitlink.shared.dto.*;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /* ========= Business / System / Third-party ========= */

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<SimpleErrorResponse> handleBusinessException(BusinessException ex, HttpServletRequest request) {
        log.info("Business exception - Code: {}, Path: {}, Message: {}", ex.getErrorCode().getCode(),
                request.getRequestURI(), ex.getMessage());

        return ResponseEntity
                .status(ex.getErrorCode().getHttpStatus())
                .body(new SimpleErrorResponse(ex.getMessage(), ex.getErrorCode().getCode(), request.getRequestURI()));
    }

    @ExceptionHandler(SystemException.class)
    public ResponseEntity<SimpleErrorResponse> handleSystemException(SystemException ex, HttpServletRequest request) {
        log.error("System exception - Path: {}, Code: {}, Message: {}", request.getRequestURI(),
                ex.getErrorCode().getCode(), ex.getMessage(), ex);

        return ResponseEntity
                .status(ex.getErrorCode().getHttpStatus())
                .body(new SimpleErrorResponse(ex.getMessage(), ex.getErrorCode().getCode(), request.getRequestURI()));
    }

    @ExceptionHandler(ThirdPartyException.class)
    public ResponseEntity<SimpleErrorResponse> handleThirdPartyException(ThirdPartyException ex, HttpServletRequest request) {
        log.warn("Third-party exception - Path: {}, Code: {}, Message: {}", request.getRequestURI(),
                ex.getErrorCode().getCode(), ex.getMessage(), ex);

        return ResponseEntity
                .status(ex.getErrorCode().getHttpStatus())
                .body(new SimpleErrorResponse(ex.getMessage(), ex.getErrorCode().getCode(), request.getRequestURI()));
    }

    /* ========= Unexpected ========= */

    @ExceptionHandler(Exception.class)
    public ResponseEntity<SimpleErrorResponse> handleUnexpectedException(Exception ex, HttpServletRequest request) {
        log.error("Unexpected exception occurred - Path: {}, Exception: {}", request.getRequestURI(),
                ex.getClass().getSimpleName(), ex);

        return new ResponseEntity<>(
                new SimpleErrorResponse("Unexpected exception occurred",
                        ex.getClass().getSimpleName(),
                        request.getRequestURI()),
                HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /* ========= Validation (MethodArgumentNotValid) ========= */

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ValidationErrorResponse> handleValidation(MethodArgumentNotValidException ex, HttpServletRequest request) {
        log.info("Validation failed - Path: {}, Fields: {}", request.getRequestURI(),
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

        String errorSummary = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.joining("; "));

        return new ResponseEntity<>(
                new ValidationErrorResponse(
                        "Validation failed: " + errorSummary,
                        "MethodArgumentNotValidException",
                        request.getRequestURI(),
                        validationErrors
                ),
                HttpStatus.BAD_REQUEST
        );
    }

    /* ========= ConstraintViolation (e.g. @Validated on params) ========= */

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ValidationErrorResponse> handleConstraintViolation(ConstraintViolationException ex, HttpServletRequest request) {
        log.info("Constraint violation - Path: {}, Violations: {}", request.getRequestURI(),
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

        return new ResponseEntity<>(
                new ValidationErrorResponse(
                        "Validation failed: " + errorSummary,
                        "ConstraintViolationException",
                        request.getRequestURI(),
                        validationErrors
                ),
                HttpStatus.BAD_REQUEST
        );
    }

    /* ========= Security ========= */

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<SimpleErrorResponse> handleAccessDenied(AccessDeniedException ex, HttpServletRequest request) {
        log.warn("Access denied - Path: {}, User: {}, Message: {}",
                request.getRequestURI(),
                request.getRemoteUser() != null ? request.getRemoteUser() : "anonymous",
                ex.getMessage());

        return new ResponseEntity<>(
                new SimpleErrorResponse("Access denied - insufficient permissions",
                        "AccessDeniedException",
                        request.getRequestURI()),
                HttpStatus.FORBIDDEN
        );
    }

    @ExceptionHandler(AuthorizationDeniedException.class)
    public ResponseEntity<SimpleErrorResponse> handleAuthorizationDenied(AuthorizationDeniedException ex, HttpServletRequest request) {
        log.warn("Authorization denied - Path: {}, User: {}, Message: {}",
                request.getRequestURI(),
                request.getRemoteUser() != null ? request.getRemoteUser() : "anonymous",
                ex.getMessage());

        return new ResponseEntity<>(
                new SimpleErrorResponse(
                        ex.getMessage() != null ? ex.getMessage() : "Authorization denied",
                        "AuthorizationDeniedException",
                        request.getRequestURI()),
                HttpStatus.FORBIDDEN
        );
    }

    /* ========= HTTP specifics ========= */

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<SimpleErrorResponse> handleMethodNotSupported(HttpRequestMethodNotSupportedException ex, HttpServletRequest request) {
        log.info("Method not supported - Path: {}, Method: {}, Supported: {}",
                request.getRequestURI(), ex.getMethod(), ex.getSupportedHttpMethods());

        String supportedMethods = ex.getSupportedHttpMethods() != null ?
                ex.getSupportedHttpMethods().toString() : "N/A";
        String message = String.format("Method '%s' not supported. Supported methods: %s",
                ex.getMethod(), supportedMethods);

        return new ResponseEntity<>(
                new SimpleErrorResponse(message, "HttpRequestMethodNotSupportedException", request.getRequestURI()),
                HttpStatus.METHOD_NOT_ALLOWED
        );
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<SimpleErrorResponse> handleHttpMessageNotReadable(HttpMessageNotReadableException ex, HttpServletRequest request) {
        log.info("Malformed JSON request - Path: {}, Cause: {}",
                request.getRequestURI(),
                ex.getCause() != null ? ex.getCause().getClass().getSimpleName() : "Unknown");

        String message = "Malformed JSON request or invalid data format";
        if (ex.getCause() instanceof JsonParseException) {
            message = "Invalid JSON format: " + ex.getCause().getMessage();
        } else if (ex.getCause() instanceof JsonMappingException) {
            message = "JSON mapping error: " + ex.getCause().getMessage();
        }

        return new ResponseEntity<>(
                new SimpleErrorResponse(message, "HttpMessageNotReadableException", request.getRequestURI()),
                HttpStatus.BAD_REQUEST
        );
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<SimpleErrorResponse> handleMissingParams(MissingServletRequestParameterException ex, HttpServletRequest request) {
        log.info("Missing required parameter - Path: {}, Parameter: {} ({})",
                request.getRequestURI(),
                ex.getParameterName(),
                ex.getParameterType());

        String message = String.format("Missing required parameter: '%s' of type '%s'",
                ex.getParameterName(), ex.getParameterType());

        return new ResponseEntity<>(
                new SimpleErrorResponse(message, "MissingServletRequestParameterException", request.getRequestURI()),
                HttpStatus.BAD_REQUEST
        );
    }

    @ExceptionHandler(TypeMismatchException.class)
    public ResponseEntity<SimpleErrorResponse> handleTypeMismatch(TypeMismatchException ex, HttpServletRequest request) {
        log.info("Type mismatch - Path: {}, Parameter: {}, Value: {}, Expected: {}",
                request.getRequestURI(), ex.getPropertyName(), ex.getValue(),
                ex.getRequiredType() != null ? ex.getRequiredType().getSimpleName() : "unknown");

        String message = String.format("Invalid value '%s' for parameter '%s'. Expected type: %s",
                ex.getValue(), ex.getPropertyName(),
                ex.getRequiredType() != null ? ex.getRequiredType().getSimpleName() : "unknown");

        return new ResponseEntity<>(
                new SimpleErrorResponse(message, "TypeMismatchException", request.getRequestURI()),
                HttpStatus.BAD_REQUEST
        );
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<SimpleErrorResponse> handleIllegalArgument(IllegalArgumentException ex, HttpServletRequest request) {
        log.warn("Illegal argument - Path: {}, Message: {}", request.getRequestURI(), ex.getMessage());

        return new ResponseEntity<>(
                new SimpleErrorResponse(ex.getMessage(), "IllegalArgumentException", request.getRequestURI()),
                HttpStatus.BAD_REQUEST
        );
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<SimpleErrorResponse> handleDataIntegrityViolation(DataIntegrityViolationException ex, HttpServletRequest request) {
        log.warn("Data integrity violation - Path: {}, Cause: {}",
                request.getRequestURI(),
                ex.getCause() != null ? ex.getCause().getClass().getSimpleName() : "Unknown");

        String message = "Data integrity violation";
        if (ex.getCause() instanceof ConstraintViolationException) {
            message = "Database constraint violation - possible duplicate entry or foreign key constraint";
        }

        return new ResponseEntity<>(
                new SimpleErrorResponse(message, "DataIntegrityViolationException", request.getRequestURI()),
                HttpStatus.CONFLICT
        );
    }

    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public ResponseEntity<SimpleErrorResponse> handleMediaTypeNotSupported(HttpMediaTypeNotSupportedException ex, HttpServletRequest request) {
        log.info("Media type not supported - Path: {}, Content-Type: {}, Supported: {}",
                request.getRequestURI(),
                ex.getContentType(),
                ex.getSupportedMediaTypes());

        String supportedTypes = ex.getSupportedMediaTypes().toString();
        String message = String.format("Media type '%s' not supported. Supported types: %s",
                ex.getContentType(), supportedTypes);

        return new ResponseEntity<>(
                new SimpleErrorResponse(message, "HttpMediaTypeNotSupportedException", request.getRequestURI()),
                HttpStatus.UNSUPPORTED_MEDIA_TYPE
        );
    }
}