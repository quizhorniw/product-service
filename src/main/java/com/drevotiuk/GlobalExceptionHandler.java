package com.drevotiuk;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.drevotiuk.model.exception.EntityNotFoundException;
import com.drevotiuk.model.exception.InvalidQuantityException;

import lombok.extern.slf4j.Slf4j;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
  @ExceptionHandler(MethodArgumentNotValidException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public ResponseEntity<Map<String, String>> handleValidationException(
      MethodArgumentNotValidException e) {
    Map<String, String> errors = new HashMap<>();
    e.getBindingResult().getAllErrors().forEach(error -> {
      String fieldName = ((FieldError) error).getField();
      String errorMessage = error.getDefaultMessage();
      log.warn("Validation exception in field {} with message: {}", fieldName, errorMessage);
      errors.put(fieldName, errorMessage);
    });
    return ResponseEntity.badRequest().body(errors);
  }

  @ExceptionHandler(EntityNotFoundException.class)
  @ResponseStatus(HttpStatus.NOT_FOUND)
  public ResponseEntity<EntityNotFoundException> handleNotFoundException(EntityNotFoundException e) {
    log.warn("EntityNotFoundException thrown", e);
    return new ResponseEntity<>(e, HttpStatus.NOT_FOUND);
  }

  @ExceptionHandler(InvalidQuantityException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public ResponseEntity<InvalidQuantityException> handleInvalidQtyException(InvalidQuantityException e) {
    log.warn("InvalidQuantityException thrown", e);
    return ResponseEntity.badRequest().body(e);
  }

  @ExceptionHandler(IllegalArgumentException.class)
  @ResponseStatus(HttpStatus.FORBIDDEN)
  public ResponseEntity<Map<String, String>> handleIllegalArgException(IllegalArgumentException e) {
    log.warn("IllegalArgumentException thrown", e);
    Map<String, String> error = new HashMap<>();
    error.put("message", e.getMessage());
    return new ResponseEntity<>(error, HttpStatus.FORBIDDEN);
  }

  @ExceptionHandler(Exception.class)
  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  public ResponseEntity<Map<String, String>> handleGlobalException(Exception e) {
    log.warn("Unexpected exception", e);
    Map<String, String> error = new HashMap<>();
    error.put("message", e.getMessage());
    return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
  }
}
