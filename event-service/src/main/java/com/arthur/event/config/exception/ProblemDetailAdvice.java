package com.arthur.event.config.exception;

import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
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

    private static final URI TYPE_NOT_FOUND = URI.create("https://errors.event-booking/not-found");
    private static final URI TYPE_VALIDATION = URI.create("https://errors.event-booking/validation");
    private static final URI TYPE_INTERNAL   = URI.create("https://errors.event-booking/internal");

    @ExceptionHandler(EntityNotFoundException.class)
    public ProblemDetail handleEntityNotFound(EntityNotFoundException ex) {
        ProblemDetail pd = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getMessage());
        pd.setType(TYPE_NOT_FOUND);
        pd.setTitle("Resource not found");
        log.warn("404 Not Found: {}", ex.getMessage());
        return pd;
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ProblemDetail handleArgumentNotValid(MethodArgumentNotValidException ex) {
        ProblemDetail pd = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        pd.setType(TYPE_VALIDATION);
        pd.setTitle("Validation error");
        pd.setDetail("Request body validation failed");

        var errors = ex.getBindingResult().getFieldErrors().stream()
                .map(fe -> Map.of("field", fe.getField(), "message", fe.getDefaultMessage()))
                .toList();
        pd.setProperty("errors", errors);

        log.warn("400 Validation error: {}", errors);
        return pd;
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ProblemDetail handleConstraintViolation(ConstraintViolationException ex) {
        ProblemDetail pd = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        pd.setType(TYPE_VALIDATION);
        pd.setTitle("Validation error");
        pd.setDetail("Request parameter validation failed");

        var errors = ex.getConstraintViolations().stream()
                .map(cv -> Map.of("param", cv.getPropertyPath().toString(), "message", cv.getMessage()))
                .toList();
        pd.setProperty("errors", errors);

        log.warn("400 Constraint violation: {}", errors);
        return pd;
    }

    @ExceptionHandler({
            HttpMessageNotReadableException.class, // кривий JSON/формат
            Exception.class
    })
    public ProblemDetail handleInternal(Exception ex) {
        String correlationId = UUID.randomUUID().toString();

        ProblemDetail pd = ProblemDetail.forStatus(HttpStatus.INTERNAL_SERVER_ERROR);
        pd.setType(TYPE_INTERNAL);
        pd.setTitle("Internal server error");
        pd.setDetail("Unexpected error");
        pd.setProperty("correlationId", correlationId);

        log.error("500 Internal error, correlationId={}", correlationId, ex);
        return pd;
    }

    @ExceptionHandler(org.springframework.web.servlet.resource.NoResourceFoundException.class)
    public ResponseEntity<ProblemDetail> handleNoResource(NoResourceFoundException ex, HttpServletRequest req) {
        var pd = ProblemDetail.forStatus(HttpStatus.NOT_FOUND);
        pd.setType(URI.create("https://errors.event-booking/not-found"));
        pd.setTitle("Not found");
        pd.setDetail(ex.getMessage());
        pd.setInstance(URI.create(req.getRequestURI()));
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(pd);
    }
}
