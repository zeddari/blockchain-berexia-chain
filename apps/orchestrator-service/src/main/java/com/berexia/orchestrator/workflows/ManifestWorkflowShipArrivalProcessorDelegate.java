package com.berexia.orchestrator.workflows;

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

/**
 * Flowable delegate for processing the Ship Arrival event received from RabbitMQ.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class ManifestWorkflowShipArrivalProcessorDelegate implements JavaDelegate {

    @Override
    public void execute(DelegateExecution execution) {
        log.info("Processing Ship Arrival event for process: {}", execution.getProcessInstanceId());
        
        try {
            // Update step status
            updateStepStatus(execution, "shipArrival", WorkflowStatus.IN_PROGRESS);
            
            // Get the ship arrival event from the process variables
            ShipArrivalEvent shipArrivalEvent = (ShipArrivalEvent) execution.getVariable("shipArrivalEvent");
            
            if (shipArrivalEvent == null) {
                log.error("Ship Arrival event not found in process variables");
                throw new IllegalStateException("Ship Arrival event not found in process variables");
            }
            
            log.info("Processing Ship Arrival event: {}", shipArrivalEvent);
            
            // Store relevant information in process variables
            execution.setVariable("actualArrivalTime", shipArrivalEvent.getDateArrivee());
            execution.setVariable("shipStatus", shipArrivalEvent.getEtat());
            execution.setVariable("shipArrivalEventProcessedTime", LocalDateTime.now());
            
            // Validate the event
            String numeroAvis = (String) execution.getVariable("numeroAvis");
            String numeroEscale = (String) execution.getVariable("numeroEscale");
            
            if (!shipArrivalEvent.getNumeroAvis().equals(numeroAvis)) {
                log.warn("Numero Avis mismatch: expected {}, got {}", numeroAvis, shipArrivalEvent.getNumeroAvis());
                execution.setVariable("numeroAvisMismatch", true);
            }
            
            if (!shipArrivalEvent.getNumeroEscale().equals(numeroEscale)) {
                log.warn("Call ID mismatch: expected {}, got {}", numeroEscale, shipArrivalEvent.getNumeroEscale());
                execution.setVariable("callIdMismatch", true);
            }
            
            // Update step status
            updateStepStatus(execution, "shipArrival", WorkflowStatus.COMPLETED);
            
            // Add audit trail entry
            addAuditTrailEntry(execution, "shipArrival", WorkflowStatus.COMPLETED, 
                    "Ship Arrival event processed successfully");
            
            log.info("Ship Arrival event processed successfully");
            
        } catch (Exception e) {
            log.error("Error processing Ship Arrival event", e);
            
            // Update step status
            updateStepStatus(execution, "shipArrival", WorkflowStatus.FAILED);
            
            // Add audit trail entry
            addAuditTrailEntry(execution, "shipArrival", WorkflowStatus.FAILED, 
                    "Failed to process Ship Arrival event: " + e.getMessage());
            
            // Set error variables
            execution.setVariable("shipArrivalProcessingError", e.getMessage());
            
            // We don't throw an exception here to allow the process to continue
        }
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