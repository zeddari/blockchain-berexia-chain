package com.berexia.orchestrator.workflows;

import com.berexia.orchestrator.models.WorkflowStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Flowable delegate for calling the DAP (Docking Request) API.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class ManifestWorkflowDapDelegate implements JavaDelegate {

    private final RestTemplate restTemplate;
    
    @Value("${services.ship-management.url}")
    private String shipManagementServiceUrl;

    @Override
    public void execute(DelegateExecution execution) {
        log.info("Calling DAP API for process: {}", execution.getProcessInstanceId());
        
        try {
            // Update step status
            updateStepStatus(execution, "dap", WorkflowStatus.IN_PROGRESS);
            
            // Extract required variables
            String shipId = (String) execution.getVariable("shipId");
            String shipName = (String) execution.getVariable("shipName");
            String portId = (String) execution.getVariable("portId");
            String portName = (String) execution.getVariable("portName");
            String callId = (String) execution.getVariable("callId");
            String operatorName = (String) execution.getVariable("operatorName");
            
            // Generate a notice number (unique identifier for the DAP request)
            Long noticeNumber = System.currentTimeMillis(); // Simple way to generate a unique number
            execution.setVariable("dapNoticeNumber", noticeNumber);
            
            // Prepare the request payload
            Map<String, Object> requestPayload = new HashMap<>();
            requestPayload.put("noticeNumber", noticeNumber);
            requestPayload.put("callNumber", callId);
            requestPayload.put("portName", portName);
            requestPayload.put("portCode", portId);
            requestPayload.put("shipName", shipName);
            requestPayload.put("operatorName", operatorName);
            requestPayload.put("status", "PENDING");
            requestPayload.put("requestDate", LocalDateTime.now());
            
            // Prepare headers
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            
            // Create the HTTP entity
            HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(requestPayload, headers);
            
            // Call the Ship Management Service API for DAP
            String apiUrl = shipManagementServiceUrl + "/api/docking-requests";
            log.info("Calling DAP API at: {}", apiUrl);
            Map<String, Object> response = restTemplate.postForObject(apiUrl, requestEntity, Map.class);
            
            // Process the response
            log.info("DAP API response: {}", response);
            
            // Store the response in the process context
            execution.setVariable("dapApiResponse", response);
            execution.setVariable("dapApiCallTimestamp", LocalDateTime.now());
            
            // Update step status
            updateStepStatus(execution, "dap", WorkflowStatus.WAITING);
            
            // Add audit trail entry
            addAuditTrailEntry(execution, "dap", WorkflowStatus.WAITING, 
                    "DAP API called successfully, waiting for event");
            
        } catch (Exception e) {
            log.error("Error calling DAP API", e);
            
            // Update step status
            updateStepStatus(execution, "dap", WorkflowStatus.FAILED);
            
            // Add audit trail entry
            addAuditTrailEntry(execution, "dap", WorkflowStatus.FAILED, 
                    "Failed to call DAP API: " + e.getMessage());
            
            // Set error variables
            execution.setVariable("dapApiError", e.getMessage());
            
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