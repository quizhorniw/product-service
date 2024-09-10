package com.drevotiuk.repository;

import com.drevotiuk.model.Product;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional(readOnly = true)
public interface ProductRepository extends MongoRepository<Product, ObjectId> {
}
