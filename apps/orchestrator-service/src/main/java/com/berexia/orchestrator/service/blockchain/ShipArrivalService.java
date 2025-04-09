package com.berexia.orchestrator.service.blockchain;

import com.berexia.business.event.manager.blockchain.contracts.ShipArrivalContract;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.tx.gas.ContractGasProvider;

import java.math.BigInteger;
import java.time.Instant;
import java.util.concurrent.CompletableFuture;

/**
 * Service for managing ship arrival events on the blockchain.
 * This service interacts with the ShipArrivalContract to record and query ship arrivals.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ShipArrivalService {

    private final Web3j web3j;
    private final Credentials credentials;
    private final ContractGasProvider gasProvider;

    @Value("${blockchain.ethereum.contracts.ship-arrival-address}")
    private String shipArrivalContractAddress;

    private ShipArrivalContract shipArrivalContract;

    /**
     * Gets the ship arrival contract address.
     * @return The contract address
     */
    public String getShipArrivalContractAddress() {
        return shipArrivalContractAddress;
    }

    /**
     * Initializes the ShipArrivalContract instance upon first use.
     */
    private void initializeContract() {
        if (shipArrivalContract == null) {
            log.info("Initializing ShipArrivalContract at address: {}", shipArrivalContractAddress);
            shipArrivalContract = ShipArrivalContract.load(
                    shipArrivalContractAddress,
                    web3j,
                    credentials,
                    gasProvider
            );
            log.info("ShipArrivalContract initialized successfully");
        }
    }

    /**
     * Records a new ship arrival event on the blockchain.
     * 
     * @param shipId The unique identifier of the ship
     * @param shipName The name of the ship
     * @param callId The call identifier for this ship arrival
     * @param portId The port identifier where the ship is arriving
     * @param portName The name of the port
     * @param operatorName The name of the port operator
     * @param expectedArrivalTime The expected time of arrival (Unix timestamp)
     * @param actualArrivalTime The actual time of arrival (Unix timestamp), can be 0 if not arrived yet
     * @return A CompletableFuture with the transaction receipt
     */
    public CompletableFuture<TransactionReceipt> recordShipArrival(
            String shipId,
            String shipName,
            String callId,
            String portId,
            String portName,
            String operatorName,
            long expectedArrivalTime,
            long actualArrivalTime
    ) {
        initializeContract();
        
        log.info("Recording ship arrival for ship ID: {}, call ID: {}", shipId, callId);
        
        return shipArrivalContract.recordShipArrival(
                shipId,
                shipName,
                callId,
                portId,
                portName,
                operatorName,
                "PENDING", // Initial status
                BigInteger.valueOf(expectedArrivalTime),
                BigInteger.valueOf(actualArrivalTime)
        ).sendAsync()
        .thenApply(receipt -> {
            log.info("Successfully recorded ship arrival. Transaction hash: {}", 
                    receipt.getTransactionHash());
            return receipt;
        })
        .exceptionally(ex -> {
            log.error("Failed to record ship arrival", ex);
            throw new RuntimeException("Ship arrival recording failed", ex);
        });
    }

    /**
     * Updates an existing ship arrival with the actual arrival time.
     * 
     * @param callId The call identifier for this ship arrival
     * @param expectedArrivalTime The expected time of arrival (Unix timestamp)
     * @param actualArrivalTime The actual time of arrival (Unix timestamp)
     * @param status The current status of the ship arrival
     * @return A CompletableFuture with the transaction receipt
     */
    public CompletableFuture<TransactionReceipt> updateShipArrivalTime(
            String callId,
            long expectedArrivalTime,
            long actualArrivalTime,
            String status
    ) {
        initializeContract();
        
        log.info("Updating ship arrival time for call ID: {} to {}", 
                callId, Instant.ofEpochSecond(actualArrivalTime));
        
        return shipArrivalContract.updateShipArrival(
                callId,
                BigInteger.valueOf(expectedArrivalTime),
                BigInteger.valueOf(actualArrivalTime),
                status
        ).sendAsync()
        .thenApply(receipt -> {
            log.info("Successfully updated ship arrival time. Transaction hash: {}", 
                    receipt.getTransactionHash());
            return receipt;
        })
        .exceptionally(ex -> {
            log.error("Failed to update ship arrival time", ex);
            throw new RuntimeException("Ship arrival time update failed", ex);
        });
    }

    /**
     * Updates the status of a ship arrival event.
     * 
     * @param callId The call identifier for this ship arrival
     * @param expectedArrivalTime The expected time of arrival (Unix timestamp)
     * @param actualArrivalTime The actual time of arrival (Unix timestamp)
     * @param newStatus The new status to set
     * @return A CompletableFuture with the transaction receipt
     */
    public CompletableFuture<TransactionReceipt> updateShipArrivalStatus(
            String callId,
            long expectedArrivalTime,
            long actualArrivalTime,
            String newStatus
    ) {
        initializeContract();
        
        log.info("Updating ship arrival status for call ID: {} to {}", callId, newStatus);
        
        return shipArrivalContract.updateShipArrival(
                callId,
                BigInteger.valueOf(expectedArrivalTime),
                BigInteger.valueOf(actualArrivalTime),
                newStatus
        ).sendAsync()
        .thenApply(receipt -> {
            log.info("Successfully updated ship arrival status. Transaction hash: {}", 
                    receipt.getTransactionHash());
            return receipt;
        })
        .exceptionally(ex -> {
            log.error("Failed to update ship arrival status", ex);
            throw new RuntimeException("Ship arrival status update failed", ex);
        });
    }

    /**
     * Retrieves a ship arrival event by ship ID.
     * 
     * @param shipId The unique identifier of the ship
     * @return A CompletableFuture with the transaction receipt
     */
    public CompletableFuture<TransactionReceipt> getShipArrivalByShipId(String shipId) {
        initializeContract();
        
        log.info("Retrieving ship arrival for ship ID: {}", shipId);
        
        return shipArrivalContract.getShipArrivalByCallId(shipId).sendAsync()
                .thenApply(receipt -> {
                    log.info("Successfully retrieved ship arrival for ship ID: {}", shipId);
                    return receipt;
                })
                .exceptionally(ex -> {
                    log.error("Failed to retrieve ship arrival", ex);
                    throw new RuntimeException("Ship arrival retrieval failed", ex);
                });
    }

    /**
     * Retrieves a ship arrival event by call ID.
     * 
     * @param callId The call identifier for this ship arrival
     * @return A CompletableFuture with an array of ship details
     */
    public CompletableFuture<TransactionReceipt> getShipArrivalByCallId(String callId) {
        initializeContract();
        
        log.info("Retrieving ship arrival for call ID: {}", callId);
        
        return shipArrivalContract.getShipArrivalByCallId(callId).sendAsync()
                .thenApply(receipt -> {
                    log.info("Successfully retrieved ship arrival for call ID: {}", callId);
                    return receipt;
                })
                .exceptionally(ex -> {
                    log.error("Failed to retrieve ship arrival", ex);
                    throw new RuntimeException("Ship arrival retrieval failed", ex);
                });
    }
} 