package com.drevotiuk.repository;

import com.drevotiuk.model.Product;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for managing {@link Product} entities in MongoDB.
 * <p>
 * This interface extends {@link MongoRepository} to provide CRUD operations for
 * {@link Product} entities.
 * </p>
 */
@Repository
public interface ProductRepository extends MongoRepository<Product, ObjectId> {
  boolean existsByName(String name);
}
