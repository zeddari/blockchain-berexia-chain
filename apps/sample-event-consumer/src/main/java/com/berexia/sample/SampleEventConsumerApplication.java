package com.berexia.sample;

import com.berexia.business.event.manager.dto.DAPEvent;
import com.berexia.business.event.manager.dto.ShipArrivalEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.time.LocalDateTime;
import java.util.Random;

/**
 * Sample application that demonstrates how to use the business-event-manager.
 * This application publishes sample events to RabbitMQ and consumes them using the event manager.
 */
@Slf4j
@SpringBootApplication
@EnableScheduling
public class SampleEventConsumerApplication {

    public static void main(String[] args) {
        SpringApplication.run(SampleEventConsumerApplication.class, args);
    }

    /**
     * Command line runner to send sample events to RabbitMQ
     */
    @Bean
    public CommandLineRunner runner(RabbitTemplate rabbitTemplate,
                                    @Value("${berexia.event.manager.rabbit.ship-arrival.exchange-name:ship-arrival-exchange}") String shipArrivalExchange,
                                    @Value("${berexia.event.manager.rabbit.ship-arrival.routing-key:ship.arrival.event}") String shipArrivalRoutingKey,
                                    @Value("${berexia.event.manager.rabbit.dap-request.exchange-name:dap-request-exchange}") String dapRequestExchange,
                                    @Value("${berexia.event.manager.rabbit.dap-request.routing-key:dap.request.event}") String dapRequestRoutingKey) {
        return args -> {
            sendShipArrivalEvent(rabbitTemplate, shipArrivalExchange, shipArrivalRoutingKey);
            sendDAPEvent(rabbitTemplate, dapRequestExchange, dapRequestRoutingKey);
        };
    }
    
    private void sendShipArrivalEvent(RabbitTemplate rabbitTemplate, String exchange, String routingKey) {
        LocalDateTime now = LocalDateTime.now();
        
        ShipArrivalEvent event = ShipArrivalEvent.builder()
                .shipId("SHIP-" + randomId())
                .shipName("Sample Ship")
                .callId("CALL-" + randomId())
                .portId("PORT-" + randomId())
                .portName("Sample Port")
                .operatorName("Sample Operator")
                .status("ARRIVED")
                .expectedArrivalTime(now.minusHours(1))
                .actualArrivalTime(now)
                .build();
        
        log.info("Sending ship arrival event to exchange {} with routing key {}: {}", exchange, routingKey, event);
        rabbitTemplate.convertAndSend(exchange, routingKey, event);
    }
    
    private void sendDAPEvent(RabbitTemplate rabbitTemplate, String exchange, String routingKey) {
        LocalDateTime now = LocalDateTime.now();
        
        DAPEvent event = DAPEvent.builder()
                .noticeNumber(randomLongId())
                .callNumber("CALL-" + randomId())
                .portName("Sample Port")
                .portCode("SP01")
                .shipName("Sample Ship")
                .operatorName("Sample Operator")
                .status("PENDING")
                .requestDate(now)
                .build();
        
        log.info("Sending DAP event to exchange {} with routing key {}: {}", exchange, routingKey, event);
        rabbitTemplate.convertAndSend(exchange, routingKey, event);
    }
    
    private String randomId() {
        return String.format("%04d", new Random().nextInt(10000));
    }
    
    private Long randomLongId() {
        return 100000L + new Random().nextInt(900000);
    }
} 