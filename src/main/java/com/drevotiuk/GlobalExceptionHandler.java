package com.drevotiuk;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageConversionException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import com.drevotiuk.model.exception.ProductNotFoundException;
import com.drevotiuk.model.exception.InvalidQuantityException;
import com.drevotiuk.model.exception.ProductExistsException;

import lombok.extern.slf4j.Slf4j;

/**
 * Global exception handler that intercepts and handles exceptions globally in
 * the application.
 * Provides specific handling for various exceptions and returns consistent
 * error responses.
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
  /**
   * Handles validation exceptions such as
   * {@link MethodArgumentNotValidException}.
   * 
   * @param e the {@link MethodArgumentNotValidException} thrown due to
   *          validation failure
   * @return a ResponseEntity containing a map of validation errors with field
   *         names as keys and error messages as values
   */
  @ExceptionHandler(MethodArgumentNotValidException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public ResponseEntity<Map<String, String>> handleValidationException(
      MethodArgumentNotValidException e) {
    Map<String, String> errors = new HashMap<>();
    e.getBindingResult().getAllErrors().forEach(error -> {
      String fieldName = ((FieldError) error).getField();
      String errorMessage = error.getDefaultMessage();
      errors.put(fieldName, errorMessage);
    });
    logException(e);
    return ResponseEntity.badRequest().body(errors);
  }

  /**
   * Handles the {@link MethodArgumentTypeMismatchException}.
   * 
   * @param e the {@link MethodArgumentTypeMismatchException} thrown when there is
   *          a type mismatch on conversion
   * @return a ResponseEntity containing a standardized error response
   */
  @ExceptionHandler(MethodArgumentTypeMismatchException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public ResponseEntity<Map<String, String>> handleTypeMismatchException(MethodArgumentTypeMismatchException e) {
    return buildErrorResponse(e, HttpStatus.BAD_REQUEST);
  }

  /**
   * Handles the {@link HttpMessageConversionException}.
   * 
   * @param e the {@link HttpMessageConversionException} thrown when there is
   *          a conversion error
   * @return a ResponseEntity containing a standardized error response
   */
  @ExceptionHandler(HttpMessageConversionException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public ResponseEntity<Map<String, String>> handleMessageConversionException(HttpMessageConversionException e) {
    return buildErrorResponse(e, HttpStatus.BAD_REQUEST);
  }

  /**
   * Handles the {@link InvalidQuantityException}.
   * 
   * @param e the {@link InvalidQuantityException} thrown when invalid quantity
   *          provided
   * @return a ResponseEntity containing a standardized error response
   */
  @ExceptionHandler(InvalidQuantityException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public ResponseEntity<Map<String, String>> handleInvalidQtyException(InvalidQuantityException e) {
    return buildErrorResponse(e, HttpStatus.BAD_REQUEST);
  }

  /**
   * Handles the {@link ProductExistsException}.
   * 
   * @param e the {@link ProductExistsException} thrown when product with same
   *          name already exists in the database
   * @return a ResponseEntity containing a standardized error response
   */
  @ExceptionHandler(InvalidQuantityException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public ResponseEntity<Map<String, String>> handleProductExistsException(ProductExistsException e) {
    return buildErrorResponse(e, HttpStatus.BAD_REQUEST);
  }

  /**
   * Handles the {@link ProductNotFoundException}.
   * 
   * @param e the {@link ProductNotFoundException} thrown when a product not found
   * @return a ResponseEntity containing a standardized error response
   */
  @ExceptionHandler(ProductNotFoundException.class)
  @ResponseStatus(HttpStatus.NOT_FOUND)
  public ResponseEntity<Map<String, String>> handleNotFoundException(ProductNotFoundException e) {
    return buildErrorResponse(e, HttpStatus.NOT_FOUND);
  }

  /**
   * Handles the {@link IllegalArgumentException}.
   * 
   * @param e the {@link IllegalArgumentException} thrown when an
   *          illegal argument is passed
   * @return a ResponseEntity containing a standardized error response
   */
  @ExceptionHandler(IllegalArgumentException.class)
  @ResponseStatus(HttpStatus.FORBIDDEN)
  public ResponseEntity<Map<String, String>> handleIllegalArgException(IllegalArgumentException e) {
    return buildErrorResponse(e, HttpStatus.FORBIDDEN);
  }

  /**
   * Handles global exceptions such as any uncaught exceptions.
   * 
   * @param e the {@link Exception} thrown when an unexpected error
   *          occurs
   * @return a ResponseEntity containing a standardized error response
   */
  @ExceptionHandler(Exception.class)
  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  public ResponseEntity<Map<String, String>> handleGlobalException(Exception e) {
    return buildErrorResponse(e, HttpStatus.INTERNAL_SERVER_ERROR);
  }

  /**
   * Logs the exception details for error tracking and debugging purposes,
   * including the class name and message.
   * 
   * @param e the {@link Exception} that occurred
   */
  private void logException(Exception e) {
    log.error("{} occurred: {}", e.getClass().getSimpleName(), e.getMessage());
  }

  /**
   * Builds a standardized error response for the given exception and HTTP status.
   * 
   * @param e      the {@link Exception} that occurred
   * @param status the HTTP status to return
   * @return a ResponseEntity containing the error message, status, and timestamp
   */
  private ResponseEntity<Map<String, String>> buildErrorResponse(Exception e, HttpStatus status) {
    logException(e);
    Map<String, String> error = new HashMap<>();
    error.put("error", e.getMessage());
    error.put("status", status.toString());
    error.put("timestamp", String.valueOf(System.currentTimeMillis()));
    return ResponseEntity.status(status).body(error);
  }
}
