package com.bfhl.api.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.NoHandlerFoundException;

import com.bfhl.api.config.BfhlProperties;
import com.bfhl.api.dto.ApiErrorResponse;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private final BfhlProperties properties;

    public GlobalExceptionHandler(BfhlProperties properties) {
        this.properties = properties;
    }

    @ExceptionHandler(ApiException.class)
    public ResponseEntity<ApiErrorResponse> handleApi(ApiException ex) {
        HttpStatus status = HttpStatus.valueOf(ex.getStatusCode());
        return ResponseEntity.status(status)
                .body(new ApiErrorResponse(properties.getOfficialEmail(), ex.getMessage()));
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiErrorResponse> handleBadJson(HttpMessageNotReadableException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ApiErrorResponse(properties.getOfficialEmail(), "Invalid JSON payload."));
    }

    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleNotFound(NoHandlerFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ApiErrorResponse(properties.getOfficialEmail(), "Route not found."));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorResponse> handleGeneric(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ApiErrorResponse(properties.getOfficialEmail(), "Internal server error."));
    }
}
