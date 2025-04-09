package com.berexia.orchestrator.service.blockchain;

import com.berexia.business.event.manager.blockchain.contracts.DAPContract;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.tx.gas.ContractGasProvider;

import java.math.BigInteger;
import java.util.concurrent.CompletableFuture;

/**
 * Service for managing DAP (Demande d'Autorisation Préalable) events on the blockchain.
 * This service interacts with the DAPContract to record and query DAP events.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class DAPService {

    private final Web3j web3j;
    private final Credentials credentials;
    private final ContractGasProvider gasProvider;

    @Value("${blockchain.ethereum.contracts.dap-address}")
    private String dapContractAddress;

    private DAPContract dapContract;

    /**
     * Gets the DAP contract address.
     * @return The contract address
     */
    public String getDapContractAddress() {
        return dapContractAddress;
    }

    /**
     * Initializes the DAPContract instance upon first use.
     */
    private void initializeContract() {
        if (dapContract == null) {
            log.info("Initializing DAPContract at address: {}", dapContractAddress);
            dapContract = DAPContract.load(
                    dapContractAddress,
                    web3j,
                    credentials,
                    gasProvider
            );
            log.info("DAPContract initialized successfully");
        }
    }

    /**
     * Records a new DAP event on the blockchain.
     * 
     * @param noticeNumber The notice number of the DAP
     * @param callNumber The call number associated with the DAP
     * @param portName The name of the port
     * @param portCode The code of the port
     * @param shipName The name of the ship
     * @param operatorName The name of the operator
     * @param requestDate The date of the DAP request (Unix timestamp)
     * @return A CompletableFuture with the transaction receipt
     */
    public CompletableFuture<TransactionReceipt> recordDAP(
            String noticeNumber,
            String callNumber,
            String portName,
            String portCode,
            String shipName,
            String operatorName,
            long requestDate
    ) {
        initializeContract();
        
        log.info("Recording DAP with notice number: {}, call number: {}", noticeNumber, callNumber);
        
        return dapContract.recordDAP(
                BigInteger.valueOf(Long.parseLong(noticeNumber)),
                callNumber,
                portName,
                portCode,
                shipName,
                operatorName,
                "PENDING", // Initial status
                BigInteger.valueOf(requestDate)
        ).sendAsync()
        .thenApply(receipt -> {
            log.info("Successfully recorded DAP. Transaction hash: {}", 
                    receipt.getTransactionHash());
            return receipt;
        })
        .exceptionally(ex -> {
            log.error("Failed to record DAP", ex);
            throw new RuntimeException("DAP recording failed", ex);
        });
    }

    /**
     * Updates the status of a DAP event.
     * 
     * @param noticeNumber The notice number of the DAP
     * @param newStatus The new status to set
     * @return A CompletableFuture with the transaction receipt
     */
    public CompletableFuture<TransactionReceipt> updateDAPStatus(
            String noticeNumber,
            String newStatus
    ) {
        initializeContract();
        
        log.info("Updating DAP status for notice number: {} to {}", noticeNumber, newStatus);
        
        return dapContract.updateDAPStatus(
                BigInteger.valueOf(Long.parseLong(noticeNumber)),
                newStatus
        ).sendAsync()
        .thenApply(receipt -> {
            log.info("Successfully updated DAP status. Transaction hash: {}", 
                    receipt.getTransactionHash());
            return receipt;
        })
        .exceptionally(ex -> {
            log.error("Failed to update DAP status", ex);
            throw new RuntimeException("DAP status update failed", ex);
        });
    }
} 