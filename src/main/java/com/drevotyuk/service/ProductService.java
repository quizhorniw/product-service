package com.drevotyuk.service;

import com.drevotyuk.model.Product;
import com.drevotyuk.repository.ProductRepository;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@Service
public class ProductService {
    @Autowired
    private ProductRepository repository;

    public ResponseEntity<Product> getProduct(String name) {
        Optional<Product> optProduct = repository.findByName(name);
        if (!optProduct.isPresent())
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

        return new ResponseEntity<>(optProduct.get(), HttpStatus.OK);
    }

    public ResponseEntity<Product> addProduct(Product product) {
        if (repository.findByName(product.getName()).isPresent())
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

        if (product.getPrice() < 0 || product.getQuantity() < 0)
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

        return new ResponseEntity<>(repository.save(product), HttpStatus.CREATED);
    }

    public ResponseEntity<Product> updateProduct(String name, Product product) {
        Optional<Product> optInitialProduct = repository.findByName(name);
        if (!optInitialProduct.isPresent())
            return new ResponseEntity<>(repository.save(product), HttpStatus.CREATED);

        if (product.getPrice() < 0 || product.getQuantity() < 0)
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

        Product initialProduct = optInitialProduct.get();
        initialProduct.setDescription(product.getDescription());
        initialProduct.setPrice(product.getPrice());
        initialProduct.setQuantity(product.getQuantity());

        return new ResponseEntity<>(repository.save(initialProduct), HttpStatus.OK);
    }

    public ResponseEntity<Product> deleteProduct(String name) {
        Optional<Product> optProduct = repository.findByName(name);
        if (!optProduct.isPresent())
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

        repository.delete(optProduct.get());
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
