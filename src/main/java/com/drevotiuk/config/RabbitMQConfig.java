package com.drevotiuk.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

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

  @Bean
  public DirectExchange productServiceExchange() {
    return new DirectExchange(productServiceExchange);
  }

  @Bean
  public Queue totalPriceQueue() {
    return new Queue(totalPriceQueue);
  }

  @Bean
  public Queue fetchQtyQueue() {
    return new Queue(fetchQtyQueue);
  }

  @Bean
  public Queue restoreQtyQueue() {
    return new Queue(restoreQtyQueue);
  }

  @Bean
  public Binding totalPriceBinding() {
    return BindingBuilder.bind(totalPriceQueue()).to(productServiceExchange())
        .with(totalPriceRoutingKey);
  }

  @Bean
  public Binding fetchQtyBinding() {
    return BindingBuilder.bind(fetchQtyQueue()).to(productServiceExchange())
        .with(fetchQtyRoutingKey);
  }

  @Bean
  public Binding restoreQtyBinding() {
    return BindingBuilder.bind(restoreQtyQueue()).to(productServiceExchange())
        .with(restoreQtyRoutingKey);
  }
}
