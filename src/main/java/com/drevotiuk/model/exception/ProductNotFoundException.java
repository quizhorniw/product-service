package com.drevotiuk.model.exception;

/**
 * Custom exception class that indicates a product was not found.
 * This exception is typically thrown when a requested product does not exist in
 * the system.
 */
public class ProductNotFoundException extends RuntimeException {
  private static final long serialVersionUID = 8354260151224969784L;

  public ProductNotFoundException(String message) {
    super(message);
  }

  public ProductNotFoundException(String message, Throwable cause) {
    super(message, cause);
  }

  public ProductNotFoundException(Throwable cause) {
    super(cause);
  }
}
