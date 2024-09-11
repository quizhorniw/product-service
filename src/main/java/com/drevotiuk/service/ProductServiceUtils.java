package com.drevotiuk.service;

import org.bson.types.ObjectId;
import org.springframework.stereotype.Service;

import com.drevotiuk.model.Product;
import com.drevotiuk.model.exception.ProductNotFoundException;
import com.drevotiuk.repository.ProductRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Utility class for product-related operations.
 * <p>
 * Provides helper methods for product retrieval and exception creation.
 * </p>
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ProductServiceUtils {
  private final ProductRepository repository;

  /**
   * Creates a {@link ProductNotFoundException} with a detailed message about the
   * missing product.
   * <p>
   * Logs a warning message indicating that the product was not found.
   * </p>
   * 
   * @param productId the ID of the product that was not found.
   * @return a {@link ProductNotFoundException} instance with a message indicating
   *         the product ID.
   */
  public ProductNotFoundException createProductNotFoundException(ObjectId productId) {
    log.warn("Product not found with ID: {}", productId);
    return new ProductNotFoundException("Product not found with ID: " + productId);
  }

  /**
   * Retrieves a {@link Product} by its ID.
   * <p>
   * Throws a {@link ProductNotFoundException} if the product with the given ID
   * does not exist.
   * </p>
   * 
   * @param productId the ID of the product to retrieve, must not be {@code null}.
   * @return the {@link Product} object with the given ID.
   * @throws ProductNotFoundException if the product with the given ID does not
   *                                  exist.
   */
  public Product findById(ObjectId productId) {
    return repository.findById(productId).orElseThrow(() -> createProductNotFoundException(productId));
  }
}
