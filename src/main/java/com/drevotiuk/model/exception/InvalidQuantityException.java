package com.drevotiuk.model.exception;

/**
 * Custom exception class that indicates that requested quantity is invalid.
 * This exception is typically thrown when requested quantity is greater than
 * current.
 */
public class InvalidQuantityException extends Exception {
  private static final long serialVersionUID = 4200912409075657488L;

  public InvalidQuantityException(String message) {
    super(message);
  }

  public InvalidQuantityException(String message, Throwable cause) {
    super(message, cause);
  }

  public InvalidQuantityException(Throwable cause) {
    super(cause);
  }
}
