package com.drevotiuk.config;

import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration class for setting up Spring application beans.
 * <p>
 * This configuration class defines beans for message conversion and AMQP
 * template.
 * </p>
 */
@Configuration
public class AppConfig {
  /**
   * Creates a {@link MessageConverter} bean for converting messages to and from
   * JSON format.
   * 
   * @return a {@link Jackson2JsonMessageConverter} instance configured for JSON
   *         conversion.
   */
  @Bean
  public MessageConverter messageConverter() {
    return new Jackson2JsonMessageConverter();
  }

  /**
   * Creates an {@link AmqpTemplate} bean for interacting with RabbitMQ.
   * <p>
   * Configures the {@link RabbitTemplate} with the provided
   * {@link ConnectionFactory} and sets the message converter to be used for
   * converting messages.
   * </p>
   * 
   * @param connectionFactory the {@link ConnectionFactory} to be used by the
   *                          {@link RabbitTemplate}.
   * @return an {@link AmqpTemplate} instance configured with the given
   *         {@link ConnectionFactory} and message converter.
   */
  @Bean
  public AmqpTemplate amqpTemplate(ConnectionFactory connectionFactory) {
    RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
    rabbitTemplate.setMessageConverter(messageConverter());
    return rabbitTemplate;
  }
}
