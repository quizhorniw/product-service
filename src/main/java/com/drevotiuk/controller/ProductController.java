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

@RestController
@RequestMapping("/api/${api.version}/products")
@RequiredArgsConstructor
public class ProductController {
  private final ProductService service;

  @GetMapping
  public ResponseEntity<List<ProductView>> findAll() {
    List<ProductView> products = service.findAll();
    return ResponseEntity.ok(products);
  }

  @GetMapping("/{productId}")
  public ResponseEntity<ProductView> find(@PathVariable ObjectId productId) {
    ProductView product = service.find(productId);
    return ResponseEntity.ok(product);
  }
}
