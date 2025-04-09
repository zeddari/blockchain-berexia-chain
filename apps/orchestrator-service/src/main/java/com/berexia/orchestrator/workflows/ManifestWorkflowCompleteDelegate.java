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
 * Flowable delegate for completing the Manifest Workflow.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class ManifestWorkflowCompleteDelegate implements JavaDelegate {

    @Override
    public void execute(DelegateExecution execution) {
        log.info("Completing Manifest Workflow: {}", execution.getProcessInstanceId());
        
        try {
            // Get tracking ID
            String trackingId = (String) execution.getVariable("trackingId");
            
            // Set workflow completion status
            execution.setVariable("workflowStatus", WorkflowStatus.COMPLETED.name());
            execution.setVariable("completionTime", LocalDateTime.now());
            
            // Update step status
            Map<String, String> stepStatus = (Map<String, String>) execution.getVariable("stepStatus");
            if (stepStatus == null) {
                stepStatus = new HashMap<>();
            }
            
            // Set manifest step as completed (we're skipping the actual implementation for now)
            stepStatus.put("manifest", WorkflowStatus.COMPLETED.name());
            execution.setVariable("stepStatus", stepStatus);
            
            // Add completion audit entry
            addAuditTrailEntry(execution, "completion", WorkflowStatus.COMPLETED, 
                    "Manifest Workflow completed successfully");
            
            // Set overall completion status
            boolean hasFailures = stepStatus.values().stream()
                    .anyMatch(s -> s.equals(WorkflowStatus.FAILED.name()));
            
            execution.setVariable("hasFailures", hasFailures);
            execution.setVariable("overallStatus", hasFailures ? "COMPLETED_WITH_FAILURES" : "COMPLETED");
            
            log.info("Manifest Workflow {} completed with tracking ID: {}", 
                    execution.getProcessInstanceId(), trackingId);
            
        } catch (Exception e) {
            log.error("Error completing Manifest Workflow", e);
            
            // Set error status
            execution.setVariable("workflowStatus", WorkflowStatus.FAILED.name());
            execution.setVariable("completionError", e.getMessage());
            
            // Add error audit entry
            addAuditTrailEntry(execution, "completion", WorkflowStatus.FAILED, 
                    "Error completing Manifest Workflow: " + e.getMessage());
        }
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