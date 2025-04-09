package com.berexia.business.event.manager.listener;

import com.berexia.business.event.manager.dto.ShipArrivalEvent;
import com.berexia.business.event.manager.service.EventProcessor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

/**
 * RabbitMQ listener for ship arrival events.
 */
@Component
@RequiredArgsConstructor
@Slf4j
@ConditionalOnProperty(prefix = "berexia.event.manager.consumers", name = "ship-arrival.enabled", havingValue = "true", matchIfMissing = true)
public class ShipArrivalListener {

    private final EventProcessor<ShipArrivalEvent> shipArrivalProcessor;

    /**
     * Handle ship arrival messages from RabbitMQ.
     *
     * @param event the ship arrival event
     */
    @RabbitListener(queues = "${berexia.event.manager.rabbitmq.ship-arrival.queue:ship-arrival-queue}")
    public void handleMessage(ShipArrivalEvent event) {
        log.info("Received ship arrival event: {}", event);
        
        try {
            boolean processed = shipArrivalProcessor.process(event);
            
            if (processed) {
                log.info("Ship arrival event processed successfully");
            } else {
                log.warn("Failed to process ship arrival event");
            }
        } catch (Exception e) {
            log.error("Error handling ship arrival event: {}", e.getMessage(), e);
        }
    }
} 