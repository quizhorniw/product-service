package com.drevotiuk.model.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class EntityNotFoundException extends RuntimeException {
  private String message;
}
