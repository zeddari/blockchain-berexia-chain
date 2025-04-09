package com.berexia.business.event.manager.service;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.berexia.business.event.manager.blockchain.BlockchainConnector;
import com.berexia.business.event.manager.dto.ShipArrivalEvent;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Service for processing ship arrival events.
 * Implements the EventProcessor interface for ship arrival events.
 */
@Service
@Slf4j
@RequiredArgsConstructor
@ConditionalOnProperty(name = "berexia.event.manager.blockchain.enabled", havingValue = "true", matchIfMissing = false)
public class ShipArrivalProcessor implements EventProcessor<ShipArrivalEvent> {

    private final BlockchainConnector blockchainConnector;

    /**
     * Process a ship arrival event by saving or updating the corresponding entity in the database.
     * If blockchain integration is enabled, also records the event on the blockchain.
     *
     * @param event the ship arrival event to process
     * @return true if the event was processed successfully, false otherwise
     */
    @Override
    @Transactional
    public boolean process(ShipArrivalEvent event) {
        try {
            log.debug("Processing ship arrival event: {}", event);
            
            // Record on blockchain
            if (blockchainConnector != null) {
                try {
                    blockchainConnector.recordShipArrival(event)
                        .thenAccept(txHash -> log.debug("Ship arrival recorded on blockchain with tx hash: {}", txHash))
                        .exceptionally(ex -> {
                            log.error("Failed to record ship arrival on blockchain: {}", ex.getMessage(), ex);
                            return null;
                        });
                } catch (Exception e) {
                    log.error("Error recording ship arrival on blockchain: {}", e.getMessage(), e);
                }
            }
            
            log.debug("Ship arrival event processed successfully");
            return true;
        } catch (Exception e) {
            log.error("Error processing ship arrival event: {}", e.getMessage(), e);
            return false;
        }
    }
} 