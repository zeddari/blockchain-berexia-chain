package com.berexia.orchestrator.workflows;

import com.berexia.business.event.manager.blockchain.BlockchainConnector;
import com.berexia.business.event.manager.dto.DAPEvent;
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
 * Flowable delegate for recording DAP on the blockchain.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class ManifestWorkflowBlockchainDapRecordDelegate implements JavaDelegate {

    private final BlockchainConnector blockchainConnector;

    @Override
    public void execute(DelegateExecution execution) {
        log.info("Recording DAP on blockchain for process: {}", execution.getProcessInstanceId());
        
        try {
            // Update step status
            updateStepStatus(execution, "dapBlockchain", WorkflowStatus.IN_PROGRESS);
            
            // Get the DAP event from the process variables
            DAPEvent dapEvent = (DAPEvent) execution.getVariable("dapEvent");
            
            if (dapEvent == null) {
                // If the event is not available, create it from process variables
                dapEvent = createDapEventFromVariables(execution);
            }
            
            log.info("Recording DAP event on blockchain: {}", dapEvent);
            
            // Call the blockchain connector to record the event
            CompletableFuture<String> future = blockchainConnector.recordDAP(dapEvent);
            
            // Wait for the blockchain transaction to complete (with timeout)
            String transactionHash = future.get(30, TimeUnit.SECONDS);
            
            // Store the transaction hash in the process context
            execution.setVariable("dapBlockchainTxHash", transactionHash);
            execution.setVariable("dapBlockchainRecordedTime", LocalDateTime.now());
            
            // Update step status
            updateStepStatus(execution, "dapBlockchain", WorkflowStatus.COMPLETED);
            
            // Add audit trail entry
            addAuditTrailEntry(execution, "dapBlockchain", WorkflowStatus.COMPLETED, 
                    "DAP recorded on blockchain successfully with transaction hash: " + transactionHash);
            
            log.info("DAP recorded on blockchain with transaction hash: {}", transactionHash);
            
        } catch (Exception e) {
            log.error("Error recording DAP on blockchain", e);
            
            // Update step status
            updateStepStatus(execution, "dapBlockchain", WorkflowStatus.FAILED);
            
            // Add audit trail entry
            addAuditTrailEntry(execution, "dapBlockchain", WorkflowStatus.FAILED, 
                    "Failed to record DAP on blockchain: " + e.getMessage());
            
            // Set error variables
            execution.setVariable("dapBlockchainError", e.getMessage());
            
            // We don't throw an exception here to allow the process to continue
        }
    }
    
    /**
     * Create a DAPEvent from process variables.
     */
    private DAPEvent createDapEventFromVariables(DelegateExecution execution) {
        Long noticeNumber = (Long) execution.getVariable("dapNoticeNumber");
        String callNumber = (String) execution.getVariable("callId");
        String portName = (String) execution.getVariable("portName");
        String portCode = (String) execution.getVariable("portId");
        String shipName = (String) execution.getVariable("shipName");
        String operatorName = (String) execution.getVariable("operatorName");
        String status = (String) execution.getVariable("dapStatus");
        LocalDateTime requestDate = (LocalDateTime) execution.getVariable("dapApiCallTimestamp");
        
        DAPEvent event = new DAPEvent();
        event.setNoticeNumber(noticeNumber);
        event.setCallNumber(callNumber);
        event.setPortName(portName);
        event.setPortCode(portCode);
        event.setShipName(shipName);
        event.setOperatorName(operatorName);
        event.setStatus(status != null ? status : "PENDING");
        event.setRequestDate(requestDate != null ? requestDate : LocalDateTime.now());
        
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