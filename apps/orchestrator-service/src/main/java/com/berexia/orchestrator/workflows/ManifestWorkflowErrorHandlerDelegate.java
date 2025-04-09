package com.berexia.orchestrator.workflows;

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
 * Flowable delegate for handling errors in the Manifest Workflow.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class ManifestWorkflowErrorHandlerDelegate implements JavaDelegate {

    @Override
    public void execute(DelegateExecution execution) {
        log.error("Handling error in Manifest Workflow: {}", execution.getProcessInstanceId());
        
        try {
            // Get current error message
            String errorMessage = (String) execution.getVariable("errorMessage");
            if (errorMessage == null) {
                errorMessage = "Unknown error occurred";
            }
            
            // Update workflow status
            execution.setVariable("workflowStatus", WorkflowStatus.FAILED.name());
            execution.setVariable("errorHandledTime", LocalDateTime.now());
            
            // Determine which step failed based on the stack trace
            String failedStep = determineFailedStep(execution);
            log.info("Failed step identified as: {}", failedStep);
            
            // Update step status
            Map<String, String> stepStatus = (Map<String, String>) execution.getVariable("stepStatus");
            if (stepStatus == null) {
                stepStatus = new HashMap<>();
            }
            
            // Update the failed step status
            stepStatus.put(failedStep, WorkflowStatus.FAILED.name());
            execution.setVariable("stepStatus", stepStatus);
            
            // Add error handling audit entry
            addAuditTrailEntry(execution, "errorHandling", WorkflowStatus.FAILED, 
                    "Error in Manifest Workflow: " + errorMessage);
            
            // Set overall failure status
            execution.setVariable("overallStatus", "FAILED");
            execution.setVariable("errorHandled", true);
            
            log.info("Error handled for Manifest Workflow: {}", execution.getProcessInstanceId());
            
        } catch (Exception e) {
            log.error("Error in error handler delegate", e);
            execution.setVariable("errorHandlerError", e.getMessage());
        }
    }
    
    /**
     * Determine which step failed based on execution variables.
     */
    private String determineFailedStep(DelegateExecution execution) {
        // Check common error variables
        if (execution.getVariable("shipArrivalApiError") != null) {
            return "shipArrival";
        } else if (execution.getVariable("shipArrivalProcessingError") != null) {
            return "shipArrivalProcessing";
        } else if (execution.getVariable("shipArrivalBlockchainError") != null) {
            return "shipArrivalBlockchain";
        } else if (execution.getVariable("dapApiError") != null) {
            return "dap";
        } else if (execution.getVariable("dapProcessingError") != null) {
            return "dapProcessing";
        } else if (execution.getVariable("dapBlockchainError") != null) {
            return "dapBlockchain";
        }
        
        // Default to initialization if we can't determine
        return "initialization";
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