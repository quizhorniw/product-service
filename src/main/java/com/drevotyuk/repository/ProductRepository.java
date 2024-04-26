package com.drevotyuk.repository;

import com.drevotyuk.model.Product;
import java.util.Optional;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends CrudRepository<Product, Integer> {
    Optional<Product> findByName(String name);
}
