package com.berexia.orchestrator.workflows;

import com.berexia.orchestrator.services.ShipManagementService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.Map;

/**
 * Flowable delegate for calling the Ship Arrival API and setting up the signal event subscription.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ManifestWorkflowShipArrivalDelegate implements JavaDelegate {

    private final ShipManagementService shipManagementService;

    @Override
    public void execute(DelegateExecution execution) {
        log.info("Executing Ship Arrival Delegate for process: {}", execution.getProcessInstanceId());
        
        // Get variables from the workflow
        String manifestId = (String) execution.getVariable("manifestId");
        Long avisNumber = (Long) execution.getVariable("avisNumber");
        String escaleNumber = (String) execution.getVariable("escaleNumber");
        String portName = (String) execution.getVariable("portName");
        String portCode = (String) execution.getVariable("portCode");
        String navireName = (String) execution.getVariable("navireName");
        Long expectedArrivalTime = (Long) execution.getVariable("expectedArrivalTime");
        
        // Convert timestamp to LocalDateTime
        LocalDateTime arrivalDateTime;
        if (expectedArrivalTime != null) {
            arrivalDateTime = LocalDateTime.ofInstant(
                java.time.Instant.ofEpochMilli(expectedArrivalTime),
                ZoneId.systemDefault()
            );
        } else {
            arrivalDateTime = LocalDateTime.now();
        }

        // Prepare the request payload
        Map<String, Object> requestPayload = new HashMap<>();
        requestPayload.put("numeroAvis", avisNumber);
        requestPayload.put("numeroEscale", escaleNumber);
        requestPayload.put("nomPort", portName);
        requestPayload.put("codePort", portCode);
        requestPayload.put("nomNavire", navireName);
        requestPayload.put("dateArrivee", arrivalDateTime.format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
        requestPayload.put("etat", "EXPECTED");

        try {
            // Call the Ship Management Service API
            ResponseEntity<String> response = shipManagementService.notifyShipArrival(requestPayload);
            log.info("Ship arrival notification sent successfully for manifest ID: {}, response: {}", manifestId, response.getBody());
            
            // Update workflow variables with the response
            execution.setVariable("shipArrivalStatus", "NOTIFIED");
            execution.setVariable("shipArrivalResponse", response.getBody());
            execution.setVariable("shipArrivalStatusCode", response.getStatusCodeValue());
            
            // Set up variables for the signal event
            execution.setVariable("waitingForShipArrival", true);
            execution.setVariable("shipArrivalSubscriptionTime", LocalDateTime.now());
            execution.setVariable("shipArrivalExpectedTime", arrivalDateTime);
            
            // Store the identifiers for the RabbitMQ listener to match
            execution.setVariable("numeroAvis", avisNumber);
            execution.setVariable("numeroEscale", escaleNumber);
            
            log.info("Workflow is now waiting for ship arrival event for manifest ID: {}", manifestId);
            
        } catch (Exception e) {
            log.error("Failed to notify ship arrival for manifest ID: {}", manifestId, e);
            execution.setVariable("shipArrivalStatus", "ERROR");
            execution.setVariable("shipArrivalError", e.getMessage());
            throw new RuntimeException("Failed to notify ship arrival: " + e.getMessage(), e);
        }
    }
} 