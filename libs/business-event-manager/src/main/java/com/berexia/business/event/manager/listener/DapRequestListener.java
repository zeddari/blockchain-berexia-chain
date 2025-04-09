package com.berexia.business.event.manager.listener;

import com.berexia.business.event.manager.dto.DAPEvent;
import com.berexia.business.event.manager.service.EventProcessor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

/**
 * Listener for DAP (Demande d'Autorisation Préalable) events from RabbitMQ.
 * Processes incoming DAP request messages and handles them using the appropriate event processor.
 */
@Component
@RequiredArgsConstructor
@Slf4j
@ConditionalOnProperty(name = "berexia.event.manager.consumers.dap-request.enabled", havingValue = "true", matchIfMissing = true)
public class DapRequestListener {

    private final EventProcessor<DAPEvent> dapRequestProcessor;

    /**
     * Handle incoming DAP request messages from RabbitMQ.
     *
     * @param event the DAP event received from RabbitMQ
     */
    @RabbitListener(queues = "${berexia.event.manager.rabbit.dap-request.queue-name:dap-request-queue}")
    public void handleMessage(DAPEvent event) {
        log.info("Received DAP request event: {}", event);
        
        try {
            boolean processed = dapRequestProcessor.process(event);
            
            if (processed) {
                log.info("Successfully processed DAP request event with notice number: {}", event.getNoticeNumber());
            } else {
                log.warn("Failed to process DAP request event with notice number: {}", event.getNoticeNumber());
            }
        } catch (Exception e) {
            log.error("Error processing DAP request event: {}", e.getMessage(), e);
        }
    }
} 