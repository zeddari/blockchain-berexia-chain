package com.berexia.shipmanagement.configuration;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfiguration {

    @Value("${rabbitmq.aa.queue}")
    private String queue;

    @Value("${rabbitmq.aa.exchange}")
    private String exchange;

    @Value("${rabbitmq.aa.binding}")
    private String binding;


    @Bean
    public Queue avisArriveeQueue() {
        return new Queue(queue, true);
    }

    @Bean
    public TopicExchange avisArriveExchange() {
        return new TopicExchange(exchange);
    }

    @Bean
    public Binding binding(Queue aaQueue, TopicExchange aaExchange) {
        return BindingBuilder.bind(aaQueue).to(aaExchange).with(binding + ".#");
    }
}
