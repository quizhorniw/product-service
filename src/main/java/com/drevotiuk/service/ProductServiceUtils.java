package com.drevotiuk.service;

import org.bson.types.ObjectId;
import org.springframework.stereotype.Service;

import com.drevotiuk.model.exception.EntityNotFoundException;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class ProductServiceUtils {
  public EntityNotFoundException createEntityNotFoundException(ObjectId productId) {
    log.warn("Product not found with ID: {}", productId);
    return new EntityNotFoundException("Product not found with ID: " + productId);
  }
}
