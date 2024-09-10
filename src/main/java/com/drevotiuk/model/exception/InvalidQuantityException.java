package com.drevotiuk.model.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class InvalidQuantityException extends RuntimeException {
  private String message;
}
