package com.drevotiuk.controller;

import com.drevotiuk.model.ProductView;
import com.drevotiuk.service.ProductService;

import lombok.RequiredArgsConstructor;

import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller for managing products.
 * <p>
 * Provides endpoints for retrieving product information.
 * </p>
 */
@RestController
@RequestMapping("/api/${api.version}/products")
@RequiredArgsConstructor
public class ProductController {
  private final ProductService service;

  /**
   * Retrieves a list of all products.
   * 
   * @return a {@link ResponseEntity} containing a list of {@link ProductView}
   *         objects with HTTP status 200 OK.
   */
  @GetMapping
  public ResponseEntity<List<ProductView>> findAll() {
    List<ProductView> products = service.findAll();
    return ResponseEntity.ok(products);
  }

  /**
   * Retrieves a specific product by its ID.
   * 
   * @param productId the ID of the product to retrieve, must not be {@code null}.
   * @return a {@link ResponseEntity} containing the {@link ProductView} object
   *         with HTTP status 200 OK if found, or HTTP status 404 NOT FOUND if the
   *         product is not found.
   */
  @GetMapping("/{productId}")
  public ResponseEntity<ProductView> find(@PathVariable ObjectId productId) {
    ProductView product = service.find(productId);
    return ResponseEntity.ok(product);
  }
}
