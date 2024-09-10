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

@RestController
@RequestMapping("/api/${api.version}/management/products")
@RequiredArgsConstructor
public class ProductManagementController {
  private static final String ACCESS_DENIED_MESSAGE = "Access denied";
  private final ProductManagementService managementService;

  @GetMapping
  public ResponseEntity<List<ProductView>> findAll(@RequestHeader("X-User-Role") String role) {
    Assert.isTrue(managementService.hasAccess(role), ACCESS_DENIED_MESSAGE);
    List<ProductView> products = managementService.findAll();
    return ResponseEntity.ok(products);
  }

  @GetMapping("/{productId}")
  public ResponseEntity<ProductView> find(@PathVariable ObjectId productId,
      @RequestHeader("X-User-Role") String role) {
    Assert.isTrue(managementService.hasAccess(role), ACCESS_DENIED_MESSAGE);
    ProductView product = managementService.find(productId);
    return ResponseEntity.ok(product);
  }

  @PostMapping
  public ResponseEntity<ProductView> create(@Valid @RequestBody Product product,
      @RequestHeader("X-User-Role") String role) {
    Assert.isTrue(managementService.hasAccess(role), ACCESS_DENIED_MESSAGE);
    ProductView createdProduct = managementService.create(product);
    return ResponseEntity.ok(createdProduct);
  }

  @PutMapping("/{productId}")
  public ResponseEntity<ProductView> update(@PathVariable ObjectId productId,
      @RequestBody Product product, @RequestHeader("X-User-Role") String role) {
    Assert.isTrue(managementService.hasAccess(role), ACCESS_DENIED_MESSAGE);
    ProductView updatedProduct = managementService.update(productId, product);
    return ResponseEntity.ok(updatedProduct);
  }

  @DeleteMapping("/{productId}")
  public ResponseEntity<Void> delete(@PathVariable ObjectId productId,
      @RequestHeader("X-User-Role") String role) {
    Assert.isTrue(managementService.hasAccess(role), ACCESS_DENIED_MESSAGE);
    managementService.delete(productId);
    return ResponseEntity.noContent().build();
  }
}
