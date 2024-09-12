package com.drevotiuk.service;

import com.drevotiuk.model.exception.ProductNotFoundException;
import com.drevotiuk.model.exception.InvalidQuantityException;
import com.drevotiuk.model.OrderItem;
import com.drevotiuk.model.Product;
import com.drevotiuk.model.ProductView;
import com.drevotiuk.repository.ProductRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.bson.types.ObjectId;
import org.springframework.amqp.AmqpRejectAndDontRequeueException;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

/**
 * Service class for handling product-related business logic.
 * <p>
 * This service manages product retrieval and processing, including interaction
 * with the product repository and message consumption for calculating total
 * prices.
 * </p>
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ProductService {
  private final ProductRepository repository;
  private final ProductServiceUtils serviceUtils;

  /**
   * Retrieves all products and maps them to {@link ProductView} objects.
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
   * Logs the ID of the product being fetched and throws an exception if the
   * product is not found.
   * </p>
   * 
   * @param productId the ID of the product to retrieve, must not be {@code null}.
   * @return a {@link ProductView} object representing the product.
   * @throws ProductNotFoundException if the product with the given ID does not
   *                                  exist.
   */
  public ProductView find(ObjectId productId) {
    log.info("Fetching product with ID {}", productId);
    return new ProductView(serviceUtils.findById(productId));
  }

  /**
   * Consumes messages from the total price queue to calculate the total price for
   * an order item.
   * <p>
   * Logs details of the received message and any exceptions encountered during
   * processing.
   * </p>
   * 
   * @param item the {@link OrderItem} containing product ID and quantity for
   *             which to calculate the total price.
   * @return the total price if successful, or {@code null} if an error occurs.
   * @throws AmqpRejectAndDontRequeueException if unexpected error happened
   */
  @RabbitListener(queues = { "${rabbitmq.queue.total-price}" })
  public BigDecimal consumeAndProduceTotalPrice(OrderItem item) {
    try {
      log.info("Received message for calculating total price: {}", item);
      ObjectId productId = new ObjectId(item.getProductId());
      Product product = serviceUtils.findById(productId);

      if (product.getQty() < item.getQty())
        throw createInvalidQuantityException(productId, product.getQty(), item.getQty());

      return product.getPrice().multiply(BigDecimal.valueOf(item.getQty()));
    } catch (ProductNotFoundException | InvalidQuantityException e) {
      log.warn("Invalid product or quantity");
    } catch (Exception e) {
      log.warn("Unexcepted error happened while calculating total price");
      throw new AmqpRejectAndDontRequeueException(e);
    }

    return null; // Signals that something went wrong
  }

  /**
   * Creates an {@link InvalidQuantityException} with a detailed message about the
   * invalid quantity.
   * 
   * @param productId   the ID of the product with the invalid quantity.
   * @param currentQty  the current quantity of the product.
   * @param expectedQty the expected quantity for the order.
   * @return an {@link InvalidQuantityException} instance with the detailed
   *         message.
   */
  private InvalidQuantityException createInvalidQuantityException(ObjectId productId,
      int currentQty, int expectedQty) {
    log.warn("Not enough quantity of product with ID {}: {}; expected: {}", productId, currentQty, expectedQty);
    String formattedMessage = String.format("Not enough quantity: %d; expected: %d", currentQty, expectedQty);
    return new InvalidQuantityException(formattedMessage);
  }
}
