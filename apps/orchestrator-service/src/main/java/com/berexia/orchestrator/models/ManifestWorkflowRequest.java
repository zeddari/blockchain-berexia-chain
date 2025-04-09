package com.berexia.orchestrator.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO for initiating a Manifest Workflow.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ManifestWorkflowRequest {
    
    /**
     * Ship identifier
     */
    private String shipId;
    
    /**
     * Ship name
     */
    private String shipName;
    
    /**
     * Port of call identifier
     */
    private String portId;
    
    /**
     * Port name
     */
    private String portName;
    
    /**
     * Call identifier (usually an identifier for the ship's visit to a port)
     */
    private String callId;
    
    /**
     * Expected arrival date and time of the ship
     */
    private LocalDateTime expectedArrivalTime;
    
    /**
     * Shipping operator name
     */
    private String operatorName;
} 