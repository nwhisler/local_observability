package com.nwhisler.local_observability.api;

import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import jakarta.validation.ConstraintViolationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.validation.BindException;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {
    
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<?> handleBadRequest(IllegalArgumentException ex) {
        Map<String, String> errorMap = new HashMap<>();
        errorMap.put("error", "BAD_REQUEST");
        errorMap.put("message", "from must be <= to" );

        return ResponseEntity.badRequest().body(errorMap);

    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleBadRequest(MethodArgumentNotValidException ex) {
        Map<String, String> errorMap = new HashMap<>();
        errorMap.put("error", "BAD_REQUEST");
        String msg = (ex.getBindingResult().getFieldError() != null)
                ? "Invalid value for parameter: " + ex.getBindingResult().getFieldError().getField()
                : "Invalid request body";

        errorMap.put("message", msg);

        return ResponseEntity.badRequest().body(errorMap);

    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<?> handleBadRequest(MethodArgumentTypeMismatchException ex) {
        Map<String, String> errorMap = new HashMap<>();
        errorMap.put("error", "BAD_REQUEST");
        errorMap.put("message", "Invalid value for parameter: " + ex.getName());

        return ResponseEntity.badRequest().body(errorMap);

    }


}