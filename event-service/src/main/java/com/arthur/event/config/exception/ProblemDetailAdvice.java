package com.arthur.event.config.exception;

import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.net.URI;
import java.util.Map;
import java.util.UUID;

@Slf4j
@RestControllerAdvice
public class ProblemDetailAdvice {

    private static final URI TYPE_NOT_FOUND   = URI.create("https://errors.event-booking/not-found");
    private static final URI TYPE_VALIDATION  = URI.create("https://errors.event-booking/validation");
    private static final URI TYPE_CONFLICT    = URI.create("https://errors.event-booking/conflict");
    private static final URI TYPE_INTERNAL    = URI.create("https://errors.event-booking/internal");

    @ExceptionHandler({EntityNotFoundException.class, NoResourceFoundException.class})
    public ProblemDetail handleNotFound(Exception ex, HttpServletRequest req) {
        ProblemDetail pd = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getMessage());
        pd.setType(TYPE_NOT_FOUND);
        pd.setTitle("Not Found");
        pd.setInstance(URI.create(req.getRequestURI()));
        pd.setProperty("errorCode", "RESOURCE_NOT_FOUND");
        log.warn("404 Not Found: {}", ex.getMessage());
        return pd;
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ProblemDetail handleArgumentNotValid(MethodArgumentNotValidException ex, HttpServletRequest req) {
        ProblemDetail pd = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        pd.setType(TYPE_VALIDATION);
        pd.setTitle("Validation error");
        pd.setDetail("Request body validation failed");
        pd.setInstance(URI.create(req.getRequestURI()));
        pd.setProperty("errorCode", "VALIDATION_FAILED");

        var errors = ex.getBindingResult().getFieldErrors().stream()
                .map(fe -> Map.of("field", fe.getField(), "message", fe.getDefaultMessage()))
                .toList();
        pd.setProperty("errors", errors);

        log.warn("400 Validation error: {}", errors);
        return pd;
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ProblemDetail handleConstraintViolation(ConstraintViolationException ex, HttpServletRequest req) {
        ProblemDetail pd = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        pd.setType(TYPE_VALIDATION);
        pd.setTitle("Validation error");
        pd.setDetail("Request parameter validation failed");
        pd.setInstance(URI.create(req.getRequestURI()));
        pd.setProperty("errorCode", "VALIDATION_FAILED");

        var errors = ex.getConstraintViolations().stream()
                .map(cv -> Map.of("param", cv.getPropertyPath().toString(), "message", cv.getMessage()))
                .toList();
        pd.setProperty("errors", errors);

        log.warn("400 Constraint violation: {}", errors);
        return pd;
    }

    @ExceptionHandler(BusinessValidationException.class)
    public ProblemDetail handleBusinessValidation(BusinessValidationException ex, HttpServletRequest req) {
        ProblemDetail pd = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        pd.setType(URI.create("https://errors.event-booking/validation"));
        pd.setTitle("Validation error");
        pd.setDetail(ex.getMessage());
        pd.setInstance(URI.create(req.getRequestURI()));
        pd.setProperty("errorCode", "BUSINESS_VALIDATION_FAILED");
        log.warn("400 Business validation error: {}", ex.getMessage());
        return pd;
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ProblemDetail handleDataConflict(DataIntegrityViolationException ex, HttpServletRequest req) {
        ProblemDetail pd = ProblemDetail.forStatus(HttpStatus.CONFLICT);
        pd.setType(TYPE_CONFLICT);
        pd.setTitle("Data conflict");
        pd.setDetail("Database constraint violated");
        pd.setInstance(URI.create(req.getRequestURI()));
        pd.setProperty("errorCode", "DATA_CONFLICT");
        log.warn("409 Conflict: {}", ex.getMessage());
        return pd;
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ProblemDetail handleNotReadable(HttpMessageNotReadableException ex, HttpServletRequest req) {
        ProblemDetail pd = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        pd.setType(TYPE_VALIDATION);
        pd.setTitle("Invalid request");
        pd.setDetail("Malformed JSON or unreadable request body");
        pd.setInstance(URI.create(req.getRequestURI()));
        pd.setProperty("errorCode", "REQUEST_NOT_READABLE");
        log.warn("400 Request not readable: {}", ex.getMessage());
        return pd;
    }

    @ExceptionHandler(Exception.class)
    public ProblemDetail handleInternal(Exception ex, HttpServletRequest req) {
        String correlationId = UUID.randomUUID().toString();

        ProblemDetail pd = ProblemDetail.forStatus(HttpStatus.INTERNAL_SERVER_ERROR);
        pd.setType(TYPE_INTERNAL);
        pd.setTitle("Internal server error");
        pd.setDetail("Unexpected error");
        pd.setInstance(URI.create(req.getRequestURI()));
        pd.setProperty("errorCode", "INTERNAL_ERROR");
        pd.setProperty("correlationId", correlationId);

        log.error("500 Internal error, correlationId={}", correlationId, ex);
        return pd;
    }
}
