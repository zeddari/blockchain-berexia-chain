package com.berexia.orchestrator.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * Response object for workflow operations.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WorkflowResponse {
    
    /**
     * The workflow instance ID.
     */
    private String workflowInstanceId;
    
    /**
     * The workflow name.
     */
    private String workflowName;
    
    /**
     * The current status of the workflow.
     */
    private WorkflowStatus status;
    
    /**
     * The timestamp when the workflow was initiated.
     */
    private LocalDateTime startTime;
    
    /**
     * The timestamp when the workflow was last updated.
     */
    private LocalDateTime lastUpdatedTime;
    
    /**
     * Optional message with additional information.
     */
    private String message;
    
    /**
     * Map of key-value pairs with additional context information.
     */
    private Map<String, Object> context;
} 