package com.drevotyuk.controller;

import com.drevotyuk.model.Product;
import com.drevotyuk.repository.ProductRepository;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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

    @GetMapping
    public Iterable<Product> getAllProducts() {
        return repository.findAll();
    }

    @GetMapping(params = "name")
    public ResponseEntity<Product> getProduct(@RequestParam String name) {
        Optional<Product> optProduct = repository.findByName(name);
        if (!optProduct.isPresent())
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

        return new ResponseEntity<>(optProduct.get(), HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<Product> addProduct(@RequestBody Product product) {
        if (repository.findByName(product.getName()).isPresent())
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

        return new ResponseEntity<>(repository.save(product), HttpStatus.CREATED);
    }

    @PutMapping
    public ResponseEntity<Product> updateProduct(
            @RequestParam String name, @RequestBody Product product) {
        Optional<Product> optInitialProduct = repository.findByName(name);
        if (!optInitialProduct.isPresent())
            return new ResponseEntity<>(repository.save(product), HttpStatus.CREATED);

        Product initialProduct = optInitialProduct.get();
        initialProduct.setName(product.getName());
        initialProduct.setDescription(product.getName());
        initialProduct.setPrice(product.getPrice());
        initialProduct.setQuantity(product.getQuantity());

        return new ResponseEntity<>(repository.save(initialProduct), HttpStatus.OK);
    }

    @DeleteMapping
    public ResponseEntity<Product> deleteProduct(@RequestParam String name) {
        Optional<Product> optProduct = repository.findByName(name);
        if (!optProduct.isPresent())
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

        repository.delete(optProduct.get());
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
