package com.drevotiuk.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.bson.types.ObjectId;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import com.drevotiuk.model.OrderItem;
import com.drevotiuk.model.Product;
import com.drevotiuk.model.ProductView;
import com.drevotiuk.repository.ProductRepository;
import com.mongodb.MongoException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class ProductManagementService {
  @Value("${services.product.collection}")
  private String productCollectionName;

  private final ProductRepository repository;
  private final ProductServiceUtils serviceUtils;
  private final MongoTemplate mongoTemplate;

  public List<ProductView> findAll() {
    List<ProductView> products = repository.findAll().stream()
        .map(ProductView::new)
        .collect(Collectors.toList());
    log.info("Fetched {} products", products.size());
    return products;
  }

  public ProductView find(ObjectId productId) {
    log.info("Fetching product with ID {}", productId);
    return new ProductView(repository.findById(productId).orElseThrow(
        () -> serviceUtils.createEntityNotFoundException(productId)));
  }

  public ProductView create(Product product) {
    product.setId(ObjectId.get());
    log.info("Adding new product: {}", product);
    repository.save(product);
    return new ProductView(product);
  }

  private void updateFields(Product initial, Product updated) {
    Optional.ofNullable(updated.getName())
        .filter(name -> !name.isEmpty())
        .ifPresent(name -> {
          log.info("Updated name for product with ID {}: {}", initial.getId(), name);
          initial.setName(name);
        });
    Optional.ofNullable(updated.getCategory())
        .ifPresent(category -> {
          log.info("Updated category for product with ID {}: {}", initial.getId(), category);
          initial.setCategory(category);
        });
    Optional.ofNullable(updated.getPrice())
        .filter(price -> price.doubleValue() > 0)
        .ifPresent(price -> {
          log.info("Updated price for product with ID {}: {}", initial.getId(), price);
          initial.setPrice(price);
        });
    Optional.ofNullable(updated.getQty())
        .filter(qty -> qty > 0)
        .ifPresent(qty -> {
          log.info("Updated quantity for product with ID {}: {}", initial.getId(), qty);
          initial.setQty(qty);
        });
  }

  public ProductView update(ObjectId productId, Product product) {
    log.info("Updating product with ID: {}", productId);
    Product initialProduct = repository.findById(productId).orElseThrow(
        () -> serviceUtils.createEntityNotFoundException(productId));
    updateFields(initialProduct, product);
    repository.save(initialProduct);
    return new ProductView(initialProduct);
  }

  public void delete(ObjectId productId) {
    log.info("Deleting product with ID: {}", productId);
    if (!repository.existsById(productId))
      throw serviceUtils.createEntityNotFoundException(productId);

    repository.deleteById(productId);
  }

  public boolean hasAccess(String role) {
    return role.equals("ADMIN");
  }

  @RabbitListener(queues = { "${rabbitmq.queue.fetch-qty}" })
  public void consumeFetchQty(List<OrderItem> orderItems) {
    log.info("Received message for fetching quantity: {}", orderItems);
    updateProductQuantities(orderItems, false);
  }

  @RabbitListener(queues = { "${rabbitmq.queue.restore-qty}" })
  public void consumeRestoreQty(List<OrderItem> orderItems) {
    log.info("Received message for restoring quantity: {}", orderItems);
    updateProductQuantities(orderItems, true);
  }

  private void updateProductQuantities(List<OrderItem> orderItems, boolean isRestoring) {
    try {
      orderItems.forEach(item -> {
        ObjectId id = new ObjectId(item.getProductId());
        Query query = new Query(Criteria.where("_id").is(id));
        ProductView product = find(id);
        int updatedQty = isRestoring
            ? product.getQty() + item.getQty()
            : product.getQty() - item.getQty();

        Update update = new Update().set("qty", updatedQty);
        mongoTemplate.findAndModify(query, update, Product.class, productCollectionName);
      });
    } catch (MongoException e) {
      log.error("Database exception", e);
    }
  }
}
