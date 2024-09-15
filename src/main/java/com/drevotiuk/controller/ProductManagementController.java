package com.drevotiuk.controller;

import java.util.List;

import javax.validation.Valid;

import org.bson.types.ObjectId;
import org.springframework.http.ResponseEntity;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.drevotiuk.model.Product;
import com.drevotiuk.model.ProductView;
import com.drevotiuk.service.ProductManagementService;

import lombok.RequiredArgsConstructor;

/**
 * REST controller for managing product operations with access control.
 * <p>
 * Provides endpoints for retrieving, creating, updating, and deleting products.
 * Access to these operations is controlled based on the user's role.
 * </p>
 */
@RestController
@RequestMapping("/api/${api.version}/management/products")
@RequiredArgsConstructor
public class ProductManagementController {
  private static final String ACCESS_DENIED_MESSAGE = "Access denied";

  private final ProductManagementService managementService;

  /**
   * Retrieves a list of all products if the user has the required access role.
   * 
   * @param role the role of the user, used for access control.
   * @return a {@link ResponseEntity} containing a list of {@link ProductView}
   *         objects with HTTP status 200 OK if the user has access.
   * @throws IllegalArgumentException if the user does not have the required
   *                                  access role.
   */
  @GetMapping
  public ResponseEntity<List<ProductView>> findAll(@RequestHeader("X-User-Role") String role) {
    Assert.isTrue("ADMIN".equals(role), ACCESS_DENIED_MESSAGE);
    List<ProductView> products = managementService.findAll();
    return ResponseEntity.ok(products);
  }

  /**
   * Retrieves a specific product by its ID if the user has the required access
   * role.
   * 
   * @param productId the ID of the product to retrieve, must not be {@code null}.
   * @param role      the role of the user, used for access control.
   * @return a {@link ResponseEntity} containing the {@link ProductView} object
   *         with HTTP status 200 OK if the user has access.
   * @throws IllegalArgumentException if the user does not have the required
   *                                  access role.
   */
  @GetMapping("/{productId}")
  public ResponseEntity<ProductView> find(@PathVariable ObjectId productId,
      @RequestHeader("X-User-Role") String role) {
    Assert.isTrue("ADMIN".equals(role), ACCESS_DENIED_MESSAGE);
    ProductView product = managementService.find(productId);
    return ResponseEntity.ok(product);
  }

  /**
   * Creates a new product if the user has the required access role.
   * 
   * @param product the {@link Product} object to create, must be valid.
   * @param role    the role of the user, used for access control.
   * @return a {@link ResponseEntity} containing the created {@link ProductView}
   *         object with HTTP status 200 OK if the user has access.
   * @throws IllegalArgumentException if the user does not have the required
   *                                  access role.
   */
  @PostMapping
  public ResponseEntity<ProductView> create(@Valid @RequestBody Product product,
      @RequestHeader("X-User-Role") String role) {
    Assert.isTrue("ADMIN".equals(role), ACCESS_DENIED_MESSAGE);
    ProductView createdProduct = managementService.create(product);
    return ResponseEntity.ok(createdProduct);
  }

  /**
   * Updates an existing product by its ID if the user has the required access
   * role.
   * 
   * @param productId the ID of the product to update, must not be {@code null}.
   * @param product   the {@link Product} object with updated details, must be
   *                  valid.
   * @param role      the role of the user, used for access control.
   * @return a {@link ResponseEntity} containing the updated {@link ProductView}
   *         object with HTTP status 200 OK if the user has access.
   * @throws IllegalArgumentException if the user does not have the required
   *                                  access role.
   */
  @PutMapping("/{productId}")
  public ResponseEntity<ProductView> update(@PathVariable ObjectId productId,
      @RequestBody Product product, @RequestHeader("X-User-Role") String role) {
    Assert.isTrue("ADMIN".equals(role), ACCESS_DENIED_MESSAGE);
    ProductView updatedProduct = managementService.update(productId, product);
    return ResponseEntity.ok(updatedProduct);
  }

  /**
   * Deletes a product by its ID if the user has the required access role.
   * 
   * @param productId the ID of the product to delete, must not be {@code null}.
   * @param role      the role of the user, used for access control.
   * @return a {@link ResponseEntity} with HTTP status 204 No Content if the user
   *         has access and the product was deleted.
   * @throws IllegalArgumentException if the user does not have the required
   *                                  access role.
   */
  @DeleteMapping("/{productId}")
  public ResponseEntity<Void> delete(@PathVariable ObjectId productId,
      @RequestHeader("X-User-Role") String role) {
    Assert.isTrue("ADMIN".equals(role), ACCESS_DENIED_MESSAGE);
    managementService.delete(productId);
    return ResponseEntity.noContent().build();
  }
}
