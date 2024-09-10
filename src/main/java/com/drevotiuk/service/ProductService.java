package com.drevotiuk.service;

import com.drevotiuk.model.exception.EntityNotFoundException;
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

@Service
@Slf4j
@RequiredArgsConstructor
public class ProductService {
  private final ProductRepository repository;
  private final ProductServiceUtils serviceUtils;

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

  @RabbitListener(queues = { "${rabbitmq.queue.total-price}" })
  public Optional<BigDecimal> consumeAndProduceTotalPrice(OrderItem item) {
    try {
      log.info("Received message for calculating total price: {}", item);
      ObjectId productId = new ObjectId(item.getProductId());
      Product product = repository.findById(productId).orElseThrow(
          () -> serviceUtils.createEntityNotFoundException(productId));

      if (product.getQty() < item.getQty())
        throw createInvalidQuantityException(productId, product.getQty(), item.getQty());

      return Optional.of(product.getPrice().multiply(BigDecimal.valueOf(item.getQty())));
    } catch (EntityNotFoundException | InvalidQuantityException e) {
      log.warn("Invalid product or quantity", e);
    } catch (RuntimeException e) {
      log.warn("Error while listening to ${rabbitmq.queue.total-price}");
      throw new AmqpRejectAndDontRequeueException(e);
    }

    return Optional.empty(); // Signals that something went wrong
  }

  private InvalidQuantityException createInvalidQuantityException(ObjectId productId,
      int currentQty, int expectedQty) {
    log.warn("Invalid quantity of product with ID {}", productId);
    return new InvalidQuantityException(String.format(
        "Not enough quantity of product with ID {}: {}; expected: {}",
        productId, currentQty, expectedQty));
  }
}
