package com.berexia.business.event.manager.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.*;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration class for RabbitMQ queues, exchanges, and bindings.
 * Queues will only be created if the corresponding consumer is enabled via configuration properties.
 */
@Slf4j
@Configuration
public class RabbitMQConfig {

    // Ship Arrival Queue Configuration
    @Bean
    @ConditionalOnProperty(name = "berexia.event.manager.consumers.ship-arrival.enabled", havingValue = "true", matchIfMissing = true)
    public Queue shipArrivalQueue(@Value("${berexia.event.manager.rabbit.ship-arrival.queue-name:ship-arrival-queue}") String queueName) {
        log.info("Creating Ship Arrival Queue: {}", queueName);
        return new Queue(queueName, true);
    }

    @Bean
    @ConditionalOnProperty(name = "berexia.event.manager.consumers.ship-arrival.enabled", havingValue = "true", matchIfMissing = true)
    public TopicExchange shipArrivalExchange(@Value("${berexia.event.manager.rabbit.ship-arrival.exchange-name:ship-arrival-exchange}") String exchangeName) {
        log.info("Creating Ship Arrival Exchange: {}", exchangeName);
        return new TopicExchange(exchangeName);
    }

    @Bean
    @ConditionalOnProperty(name = "berexia.event.manager.consumers.ship-arrival.enabled", havingValue = "true", matchIfMissing = true)
    public Binding shipArrivalBinding(Queue shipArrivalQueue, TopicExchange shipArrivalExchange,
                             @Value("${berexia.event.manager.rabbit.ship-arrival.routing-key:ship.arrival.#}") String routingKey) {
        log.info("Binding Ship Arrival Queue to Exchange with routing key: {}", routingKey);
        return BindingBuilder.bind(shipArrivalQueue).to(shipArrivalExchange).with(routingKey);
    }

    // Docking Request Queue Configuration
    @Bean
    @ConditionalOnProperty(name = "berexia.event.manager.consumers.dap-request.enabled", havingValue = "true", matchIfMissing = true)
    public Queue dapRequestQueue(@Value("${berexia.event.manager.rabbit.dap-request.queue-name:dap-request-queue}") String queueName) {
        log.info("Creating DAP Request Queue: {}", queueName);
        return new Queue(queueName, true);
    }

    @Bean
    @ConditionalOnProperty(name = "berexia.event.manager.consumers.dap-request.enabled", havingValue = "true", matchIfMissing = true)
    public TopicExchange dapRequestExchange(@Value("${berexia.event.manager.rabbit.dap-request.exchange-name:dap-request-exchange}") String exchangeName) {
        log.info("Creating DAP Request Exchange: {}", exchangeName);
        return new TopicExchange(exchangeName);
    }

    @Bean
    @ConditionalOnProperty(name = "berexia.event.manager.consumers.dap-request.enabled", havingValue = "true", matchIfMissing = true)
    public Binding dapRequestBinding(Queue dapRequestQueue, TopicExchange dapRequestExchange,
                          @Value("${berexia.event.manager.rabbit.dap-request.routing-key:dap.request.#}") String routingKey) {
        log.info("Binding DAP Request Queue to Exchange with routing key: {}", routingKey);
        return BindingBuilder.bind(dapRequestQueue).to(dapRequestExchange).with(routingKey);
    }
} 