package com.drevotiuk.model.exception;

/**
 * Custom exception class that indicates an attempt to save a product with
 * the same name that already exists in the system.
 * This exception is typically thrown during product creation when a product
 * with the same name is found in the database.
 */
public class ProductExistsException extends RuntimeException {
  private static final long serialVersionUID = -7880171382684225562L;

  public ProductExistsException(String message) {
    super(message);
  }

  public ProductExistsException(String message, Throwable cause) {
    super(message, cause);
  }

  public ProductExistsException(Throwable cause) {
    super(cause);
  }
}
