package com.drevotyuk.controller;

import com.drevotyuk.model.Product;
import com.drevotyuk.repository.ProductRepository;
import com.drevotyuk.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/product")
public class ProductController {
    @Autowired
    private ProductRepository repository;
    @Autowired
    private ProductService service;

    @GetMapping
    public Iterable<Product> getAllProducts() {
        return repository.findAll();
    }

    @GetMapping(params = "name")
    public ResponseEntity<Product> getProduct(@RequestParam String name) {
        return service.getProduct(name);
    }

    @PostMapping
    public ResponseEntity<Product> addProduct(@RequestBody Product product) {
        return service.addProduct(product);
    }

    @PutMapping
    public ResponseEntity<Product> updateProduct(
            @RequestParam String name, @RequestBody Product product) {
        return service.updateProduct(name, product);
    }

    @DeleteMapping
    public ResponseEntity<Product> deleteProduct(@RequestParam String name) {
        return service.deleteProduct(name);
    }
}
