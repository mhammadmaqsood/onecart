package com.cartify.authservice.controller;

import com.cartify.common.Constants;
import com.cartify.common.error.BadRequestException;
import com.cartify.common.error.ErrorCode;
import com.cartify.common.error.ErrorResponse;
import com.cartify.common.error.UnauthorizedException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.slf4j.MDC;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.net.BindException;

@ControllerAdvice
public class GlobalExceptionHandler {
    private static String reqId() {
        return MDC.get(Constants.MDC_REQUEST_ID);
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ErrorResponse> handleBadRequest(BadRequestException ex, HttpServletRequest req) {
        var body = new ErrorResponse(req.getRequestURI(), ex.getCode(), ex.getMessage(), reqId());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<ErrorResponse> handleUnauthorized(UnauthorizedException ex, HttpServletRequest req) {
        var body = new ErrorResponse(req.getRequestURI(), ex.getCode(), ex.getMessage(), reqId());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(body);
    }

    @ExceptionHandler({ MethodArgumentNotValidException.class, BindException.class })
    public ResponseEntity<ErrorResponse> handleValidation(Exception ex, HttpServletRequest req) {
        var body = new ErrorResponse(req.getRequestURI(), ErrorCode.VALIDATION_FAILED, "Validation failed", reqId());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArg(IllegalArgumentException ex, HttpServletRequest req) {
        var body = new ErrorResponse(req.getRequestURI(), ErrorCode.BAD_REQUEST, ex.getMessage(), reqId());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleFallback(Exception ex, HttpServletRequest req) {
        var body = new ErrorResponse(req.getRequestURI(), ErrorCode.INTERNAL_ERROR, "Unexpected error", reqId());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(body);
    }
}
