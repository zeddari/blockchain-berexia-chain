package com.berexia.orchestrator.workflows;

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

/**
 * Flowable delegate for processing the DAP event received from RabbitMQ.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class ManifestWorkflowDapProcessorDelegate implements JavaDelegate {

    @Override
    public void execute(DelegateExecution execution) {
        log.info("Processing DAP event for process: {}", execution.getProcessInstanceId());
        
        try {
            // Update step status
            updateStepStatus(execution, "dap", WorkflowStatus.IN_PROGRESS);
            
            // Get the DAP event from the process variables
            DAPEvent dapEvent = (DAPEvent) execution.getVariable("dapEvent");
            
            if (dapEvent == null) {
                log.error("DAP event not found in process variables");
                throw new IllegalStateException("DAP event not found in process variables");
            }
            
            log.info("Processing DAP event: {}", dapEvent);
            
            // Store relevant information in process variables
            execution.setVariable("dapStatus", dapEvent.getStatus());
            execution.setVariable("dapEventProcessedTime", LocalDateTime.now());
            
            // Validate the event
            Long expectedNoticeNumber = (Long) execution.getVariable("dapNoticeNumber");
            String expectedCallNumber = (String) execution.getVariable("callId");
            
            if (!dapEvent.getNoticeNumber().equals(expectedNoticeNumber)) {
                log.warn("Notice number mismatch: expected {}, got {}", expectedNoticeNumber, dapEvent.getNoticeNumber());
                execution.setVariable("noticeNumberMismatch", true);
            }
            
            if (!dapEvent.getCallNumber().equals(expectedCallNumber)) {
                log.warn("Call number mismatch: expected {}, got {}", expectedCallNumber, dapEvent.getCallNumber());
                execution.setVariable("callNumberMismatch", true);
            }
            
            // Update step status
            updateStepStatus(execution, "dap", WorkflowStatus.COMPLETED);
            
            // Add audit trail entry
            addAuditTrailEntry(execution, "dap", WorkflowStatus.COMPLETED, 
                    "DAP event processed successfully");
            
            log.info("DAP event processed successfully");
            
        } catch (Exception e) {
            log.error("Error processing DAP event", e);
            
            // Update step status
            updateStepStatus(execution, "dap", WorkflowStatus.FAILED);
            
            // Add audit trail entry
            addAuditTrailEntry(execution, "dap", WorkflowStatus.FAILED, 
                    "Failed to process DAP event: " + e.getMessage());
            
            // Set error variables
            execution.setVariable("dapProcessingError", e.getMessage());
            
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