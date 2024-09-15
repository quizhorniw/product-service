package com.drevotiuk.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.bson.types.ObjectId;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import com.drevotiuk.model.OrderItem;
import com.drevotiuk.model.Product;
import com.drevotiuk.model.ProductView;
import com.drevotiuk.model.exception.ProductExistsException;
import com.drevotiuk.repository.ProductRepository;
import com.mongodb.MongoException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Service class for managing product-related operations with MongoDB and
 * RabbitMQ integration.
 * <p>
 * Provides methods for product retrieval, creation, updating, and deletion.
 * Handles product quantity updates through RabbitMQ messages.
 * </p>
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ProductManagementService {
  private final ProductRepository repository;
  private final ProductServiceUtils serviceUtils;
  private final MongoTemplate mongoTemplate;

  /**
   * Retrieves a list of all products.
   * <p>
   * Logs the number of products fetched.
   * </p>
   * 
   * @return a {@link List} of {@link ProductView} objects representing all
   *         products.
   */
  public List<ProductView> findAll() {
    List<ProductView> products = repository.findAll().stream()
        .map(ProductView::new)
        .collect(Collectors.toList());
    log.info("Fetched {} products", products.size());
    return products;
  }

  /**
   * Retrieves a specific product by its ID.
   * <p>
   * Logs the ID of the product being fetched.
   * </p>
   * 
   * @param productId the ID of the product to retrieve, must not be {@code null}.
   * @return a {@link ProductView} object representing the product.
   */
  public ProductView find(ObjectId productId) {
    log.info("Fetching product with ID {}", productId);
    return new ProductView(findById(productId));
  }

  /**
   * Creates a new product and saves it to the repository.
   * <p>
   * Logs the details of the new product being added.
   * </p>
   * 
   * @param product the {@link Product} object to create, must be valid.
   * @return a {@link ProductView} object representing the created product.
   */
  public ProductView create(Product product) {
    product.setId(ObjectId.get());
    log.info("Adding new product: {}", product);
    if (repository.existsByName(product.getName())) {
      log.warn("Product with name {} already exists", product.getName());
      throw new ProductExistsException(String.format("Product with name %s already exists", product.getName()));
    }

    repository.save(product);
    return new ProductView(product);
  }

  /**
   * Updates an existing product with new details.
   * <p>
   * Logs the details of the update process.
   * </p>
   * 
   * @param productId the ID of the product to update, must not be {@code null}.
   * @param product   the {@link Product} object with updated details, must be
   *                  valid.
   * @return a {@link ProductView} object representing the updated product.
   */
  public ProductView update(ObjectId productId, Product product) {
    log.info("Updating product with ID: {}", productId);
    Product initialProduct = findById(productId);
    updateFields(initialProduct, product);
    repository.save(initialProduct);
    return new ProductView(initialProduct);
  }

  /**
   * Deletes a product by its ID.
   * <p>
   * Logs the ID of the product being deleted. Throws an exception if the product
   * does not exist.
   * </p>
   * 
   * @param productId the ID of the product to delete, must not be {@code null}.
   * @throws ProductNotFoundException if the product with the given ID does not
   *                                  exist.
   */
  public void delete(ObjectId productId) {
    log.info("Deleting product with ID: {}", productId);
    if (!repository.existsById(productId))
      throw serviceUtils.createProductNotFoundException(productId);

    repository.deleteById(productId);
  }

  /**
   * Consumes messages from the fetch quantity queue and updates product
   * quantities.
   * <p>
   * Logs details of the received message and processes the quantity updates.
   * </p>
   * 
   * @param orderItems a {@link List} of {@link OrderItem} objects containing
   *                   product IDs and quantities.
   */
  @RabbitListener(queues = { "${rabbitmq.queue.fetch-qty}" })
  public void consumeFetchQty(List<OrderItem> orderItems) {
    log.info("Received message for fetching quantity: {}", orderItems);
    updateProductQuantities(orderItems, false);
  }

  /**
   * Consumes messages from the restore quantity queue and restores product
   * quantities.
   * <p>
   * Logs details of the received message and processes the quantity restorations.
   * </p>
   * 
   * @param orderItems a {@link List} of {@link OrderItem} objects containing
   *                   product IDs and quantities.
   */
  @RabbitListener(queues = { "${rabbitmq.queue.restore-qty}" })
  public void consumeRestoreQty(List<OrderItem> orderItems) {
    log.info("Received message for restoring quantity: {}", orderItems);
    updateProductQuantities(orderItems, true);
  }

  /**
   * Retrieves a {@link Product} by its ID.
   * <p>
   * Throws a {@link ProductNotFoundException} if the product with the given ID
   * does not exist.
   * </p>
   * 
   * @param productId the ID of the product to retrieve, must not be {@code null}.
   * @return the {@link Product} object with the given ID.
   * @throws ProductNotFoundException if the product with the given ID does not
   *                                  exist.
   */
  private Product findById(ObjectId productId) {
    return repository.findById(productId)
        .orElseThrow(() -> serviceUtils.createProductNotFoundException(productId));
  }

  /**
   * Updates the fields of the initial product with values from the updated
   * product.
   * <p>
   * Only non-null and valid values from the {@code updated} product are used to
   * update the {@code initial}
   * product. Fields that are updated include name, category, price, and quantity.
   * Logs details of each field that is updated.
   * </p>
   * 
   * @param initial the {@link Product} object to be updated, must not be
   *                {@code null}.
   * @param updated the {@link Product} object containing the new values, must not
   *                be {@code null}.
   */
  private void updateFields(Product initial, Product updated) {
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

  /**
   * Updates product quantities based on the provided order items.
   * <p>
   * Performs either a quantity increase or decrease based on the
   * {@code isRestoring} flag.
   * </p>
   * 
   * @param orderItems  a {@link List} of {@link OrderItem} objects containing
   *                    product IDs and quantities.
   * @param isRestoring {@code true} if quantities are being restored,
   *                    {@code false} if being fetched.
   */
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
        mongoTemplate.updateFirst(query, update, Product.class);
      });
    } catch (MongoException e) {
      log.error("Database exception", e);
    }
  }
}
