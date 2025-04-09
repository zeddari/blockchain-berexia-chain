package com.berexia.business.event.manager.service;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.berexia.business.event.manager.blockchain.BlockchainConnector;
import com.berexia.business.event.manager.dto.DAPEvent;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Service for processing DAP (Demande d'Autorisation Préalable) events.
 * Implements the EventProcessor interface for DAP events.
 */
@Service
@Slf4j
@RequiredArgsConstructor
@ConditionalOnProperty(name = "berexia.event.manager.blockchain.enabled", havingValue = "true", matchIfMissing = false)
public class DapRequestProcessor implements EventProcessor<DAPEvent> {

    private final BlockchainConnector blockchainConnector;

    /**
     * Process a DAP event by saving or updating the corresponding entity in the database.
     * If blockchain integration is enabled, also records the event on the blockchain.
     *
     * @param event the DAP event to process
     * @return true if the event was processed successfully, false otherwise
     */
    @Override
    @Transactional
    public boolean process(DAPEvent event) {
        try {
            log.debug("Processing DAP event: {}", event);
            
            // Record on blockchain
            if (blockchainConnector != null) {
                try {
                    blockchainConnector.recordDAP(event)
                        .thenAccept(txHash -> log.debug("DAP recorded on blockchain with tx hash: {}", txHash))
                        .exceptionally(ex -> {
                            log.error("Failed to record DAP on blockchain: {}", ex.getMessage(), ex);
                            return null;
                        });
                } catch (Exception e) {
                    log.error("Error recording DAP on blockchain: {}", e.getMessage(), e);
                }
            }
            
            log.debug("DAP event processed successfully");
            return true;
        } catch (Exception e) {
            log.error("Error processing DAP event: {}", e.getMessage(), e);
            return false;
        }
    }
} 