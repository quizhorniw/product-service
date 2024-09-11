package com.drevotiuk.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration class for RabbitMQ setup.
 * <p>
 * This configuration class defines the RabbitMQ exchange, queues, and bindings
 * needed for communication between microservices.
 * </p>
 */
@Configuration
public class RabbitMQConfig {
  @Value("${rabbitmq.exchange.product-service}")
  private String productServiceExchange;

  @Value("${rabbitmq.queue.total-price}")
  private String totalPriceQueue;
  @Value("${rabbitmq.queue.fetch-qty}")
  private String fetchQtyQueue;
  @Value("${rabbitmq.queue.restore-qty}")
  private String restoreQtyQueue;

  @Value("${rabbitmq.routingkey.total-price}")
  private String totalPriceRoutingKey;
  @Value("${rabbitmq.routingkey.fetch-qty}")
  private String fetchQtyRoutingKey;
  @Value("${rabbitmq.routingkey.restore-qty}")
  private String restoreQtyRoutingKey;

  /**
   * Defines a {@link DirectExchange} bean for the product service.
   * 
   * @return a {@link DirectExchange} instance with the configured exchange name.
   */
  @Bean
  public DirectExchange productServiceExchange() {
    return new DirectExchange(productServiceExchange);
  }

  /**
   * Defines a {@link Queue} bean for the total price queue.
   * 
   * @return a {@link Queue} instance with the configured queue name.
   */
  @Bean
  public Queue totalPriceQueue() {
    return new Queue(totalPriceQueue);
  }

  /**
   * Defines a {@link Queue} bean for the fetch quantity queue.
   * 
   * @return a {@link Queue} instance with the configured queue name.
   */
  @Bean
  public Queue fetchQtyQueue() {
    return new Queue(fetchQtyQueue);
  }

  /**
   * Defines a {@link Queue} bean for the restore quantity queue.
   * 
   * @return a {@link Queue} instance with the configured queue name.
   */
  @Bean
  public Queue restoreQtyQueue() {
    return new Queue(restoreQtyQueue);
  }

  /**
   * Defines a {@link Binding} bean that binds the total price queue to the
   * product service exchange
   * using the total price routing key.
   * 
   * @return a {@link Binding} instance for the total price queue.
   */
  @Bean
  public Binding totalPriceBinding() {
    return BindingBuilder.bind(totalPriceQueue()).to(productServiceExchange())
        .with(totalPriceRoutingKey);
  }

  /**
   * Defines a {@link Binding} bean that binds the fetch quantity queue to the
   * product service exchange
   * using the fetch quantity routing key.
   * 
   * @return a {@link Binding} instance for the fetch quantity queue.
   */
  @Bean
  public Binding fetchQtyBinding() {
    return BindingBuilder.bind(fetchQtyQueue()).to(productServiceExchange())
        .with(fetchQtyRoutingKey);
  }

  /**
   * Defines a {@link Binding} bean that binds the restore quantity queue to the
   * product service exchange
   * using the restore quantity routing key.
   * 
   * @return a {@link Binding} instance for the restore quantity queue.
   */
  @Bean
  public Binding restoreQtyBinding() {
    return BindingBuilder.bind(restoreQtyQueue()).to(productServiceExchange())
        .with(restoreQtyRoutingKey);
  }
}
