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
import java.util.UUID;

/**
 * Flowable delegate for initializing the Manifest Workflow.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class ManifestWorkflowInitializeDelegate implements JavaDelegate {

    @Override
    public void execute(DelegateExecution execution) {
        log.info("Initializing Manifest Workflow: {}", execution.getProcessInstanceId());
        
        try {
            // Generate a workflow tracking ID
            String trackingId = "MWF-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
            execution.setVariable("trackingId", trackingId);
            
            // Set initial workflow status
            execution.setVariable("workflowStatus", WorkflowStatus.IN_PROGRESS.name());
            
            // Set timestamps
            if (execution.getVariable("startTime") == null) {
                execution.setVariable("startTime", LocalDateTime.now());
            }
            execution.setVariable("lastUpdatedTime", LocalDateTime.now());
            
            // Initialize step status tracking
            Map<String, String> stepStatus = new HashMap<>();
            stepStatus.put("initialization", WorkflowStatus.COMPLETED.name());
            stepStatus.put("shipArrival", WorkflowStatus.INITIATED.name());
            stepStatus.put("dap", WorkflowStatus.INITIATED.name());
            stepStatus.put("manifest", WorkflowStatus.INITIATED.name());
            execution.setVariable("stepStatus", stepStatus);
            
            // Create an audit trail entry
            Map<String, Object> auditEntry = new HashMap<>();
            auditEntry.put("timestamp", LocalDateTime.now());
            auditEntry.put("step", "initialization");
            auditEntry.put("status", WorkflowStatus.COMPLETED.name());
            auditEntry.put("message", "Workflow initialized successfully");
            execution.setVariable("auditTrail", new Object[]{auditEntry});
            
            log.info("Manifest Workflow initialized with tracking ID: {}", trackingId);
        } catch (Exception e) {
            log.error("Error initializing Manifest Workflow", e);
            execution.setVariable("workflowStatus", WorkflowStatus.FAILED.name());
            execution.setVariable("errorMessage", "Initialization failed: " + e.getMessage());
        }
    }
} 