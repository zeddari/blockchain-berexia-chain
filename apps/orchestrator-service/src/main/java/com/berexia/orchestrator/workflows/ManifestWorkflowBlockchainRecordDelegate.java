package com.berexia.orchestrator.workflows;

import com.berexia.business.event.manager.blockchain.BlockchainConnector;
import com.berexia.business.event.manager.dto.ShipArrivalEvent;
import com.berexia.orchestrator.models.WorkflowStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

/**
 * Flowable delegate for recording Ship Arrival on the blockchain.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class ManifestWorkflowBlockchainRecordDelegate implements JavaDelegate {

    private final BlockchainConnector blockchainConnector;

    @Override
    public void execute(DelegateExecution execution) {
        log.info("Recording Ship Arrival on blockchain for process: {}", execution.getProcessInstanceId());
        
        try {
            // Update step status
            updateStepStatus(execution, "shipArrivalBlockchain", WorkflowStatus.IN_PROGRESS);
            
            // Get the ship arrival event from the process variables
            ShipArrivalEvent shipArrivalEvent = (ShipArrivalEvent) execution.getVariable("shipArrivalEvent");
            
            if (shipArrivalEvent == null) {
                // If the event is not available, create it from process variables
                shipArrivalEvent = createShipArrivalEventFromVariables(execution);
            }
            
            log.info("Recording Ship Arrival event on blockchain: {}", shipArrivalEvent);
            
            // Call the blockchain connector to record the event
            CompletableFuture<String> future = blockchainConnector.recordShipArrival(shipArrivalEvent);
            
            // Wait for the blockchain transaction to complete (with timeout)
            String transactionHash = future.get(30, TimeUnit.SECONDS);
            
            // Store the transaction hash in the process context
            execution.setVariable("shipArrivalBlockchainTxHash", transactionHash);
            execution.setVariable("shipArrivalBlockchainRecordedTime", LocalDateTime.now());
            
            // Update step status
            updateStepStatus(execution, "shipArrivalBlockchain", WorkflowStatus.COMPLETED);
            
            // Add audit trail entry
            addAuditTrailEntry(execution, "shipArrivalBlockchain", WorkflowStatus.COMPLETED, 
                    "Ship Arrival recorded on blockchain successfully with transaction hash: " + transactionHash);
            
            log.info("Ship Arrival recorded on blockchain with transaction hash: {}", transactionHash);
            
        } catch (Exception e) {
            log.error("Error recording Ship Arrival on blockchain", e);
            
            // Update step status
            updateStepStatus(execution, "shipArrivalBlockchain", WorkflowStatus.FAILED);
            
            // Add audit trail entry
            addAuditTrailEntry(execution, "shipArrivalBlockchain", WorkflowStatus.FAILED, 
                    "Failed to record Ship Arrival on blockchain: " + e.getMessage());
            
            // Set error variables
            execution.setVariable("shipArrivalBlockchainError", e.getMessage());
            
            // We don't throw an exception here to allow the process to continue
        }
    }
    
    /**
     * Create a ShipArrivalEvent from process variables.
     */
    private ShipArrivalEvent createShipArrivalEventFromVariables(DelegateExecution execution) {
        Long numeroAvis = (Long) execution.getVariable("numeroAvis");
        String numeroEscale = (String) execution.getVariable("numeroEscale");
        String nomPort = (String) execution.getVariable("nomPort");
        String codePort = (String) execution.getVariable("codePort");
        String nomNavire = (String) execution.getVariable("nomNavire");
        LocalDateTime expectedArrivalTime = (LocalDateTime) execution.getVariable("dateArrivee");
        LocalDateTime actualArrivalTime = (LocalDateTime) execution.getVariable("dateArrivee");
        String operatorName = (String) execution.getVariable("operatorName");
        String etat = (String) execution.getVariable("etat");
        
        ShipArrivalEvent event = new ShipArrivalEvent();
        event.setNumeroAvis(numeroAvis);
        event.setNumeroEscale(numeroEscale);
        event.setNomPort(nomPort);
        event.setCodePort(codePort);
        event.setNomNavire(nomNavire);
        event.setDateArrivee(expectedArrivalTime);
        event.setEtat(etat);
        
        return event;
    }
    
    /**
     * Update the status of a specific workflow step.
     */
    @SuppressWarnings("unchecked")
    private void updateStepStatus(DelegateExecution execution, String step, WorkflowStatus status) {
        Map<String, String> stepStatus = (Map<String, String>) execution.getVariable("stepStatus");
        if (stepStatus == null) {
            stepStatus = new HashMap<>();
        }
        stepStatus.put(step, status.name());
        execution.setVariable("stepStatus", stepStatus);
        execution.setVariable("lastUpdatedTime", LocalDateTime.now());
    }
    
    /**
     * Add an entry to the workflow audit trail.
     */
    @SuppressWarnings("unchecked")
    private void addAuditTrailEntry(DelegateExecution execution, String step, WorkflowStatus status, String message) {
        Object[] auditTrail = (Object[]) execution.getVariable("auditTrail");
        
        Map<String, Object> auditEntry = new HashMap<>();
        auditEntry.put("timestamp", LocalDateTime.now());
        auditEntry.put("step", step);
        auditEntry.put("status", status.name());
        auditEntry.put("message", message);
        
        if (auditTrail == null) {
            auditTrail = new Object[]{auditEntry};
        } else {
            Object[] newAuditTrail = new Object[auditTrail.length + 1];
            System.arraycopy(auditTrail, 0, newAuditTrail, 0, auditTrail.length);
            newAuditTrail[auditTrail.length] = auditEntry;
            auditTrail = newAuditTrail;
        }
        
        execution.setVariable("auditTrail", auditTrail);
    }
} 