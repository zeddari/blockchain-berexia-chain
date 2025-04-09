package com.berexia.orchestrator.controller;

import com.berexia.orchestrator.listener.blockchain.BlockchainEventListener;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.TaskService;
import org.flowable.engine.runtime.ProcessInstance;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * Controller for managing manifest workflows.
 */
@RestController
@RequestMapping("/api/workflow/manifest")
@RequiredArgsConstructor
@Slf4j
public class ManifestWorkflowController {

    private final RuntimeService runtimeService;
    private final TaskService taskService;
    private final BlockchainEventListener blockchainEventListener;

    /**
     * Start a new manifest workflow.
     * @param manifestId The unique identifier for the manifest
     * @param shipId The ID of the ship
     * @param shipName The name of the ship
     * @param callId The call ID for the ship arrival
     * @param portId The port ID
     * @param portName The port name
     * @param operatorName The operator name
     * @param noticeNumber The DAP notice number
     * @param portCode The port code
     * @param expectedArrivalTime The expected arrival time of the ship in milliseconds since the Unix epoch
     * @return Information about the started workflow
     */
    @PostMapping("/start")
    public ResponseEntity<Map<String, Object>> startManifestWorkflow(
            @RequestBody Map<String, Object> request
    ) {
        log.info("Starting manifest workflow");
        
        String manifestId = (String) request.get("manifestId");
        Long avisNumber = Long.parseLong((String)request.get("avisNumber"));
        String escaleNumber = (String) request.get("escaleNumber");
        String portName = (String) request.get("portName");
        String portCode = (String) request.get("portCode");
        String navireName = (String) request.get("navireName");
        
        String status = (String) request.get("status");
        String operatorName = (String) request.get("operatorName");
        String noticeNumber = (String) request.get("noticeNumber");
        Long expectedArrivalTime = (Long) request.get("expectedArrivalTime");
        
        // Prepare variables for the workflow
        Map<String, Object> variables = new HashMap<>();
        variables.put("manifestId", manifestId);
        variables.put("avisNumber", avisNumber);
        variables.put("escaleNumber", escaleNumber);
        variables.put("portName", portName);
        variables.put("operatorName", operatorName);
        variables.put("noticeNumber", noticeNumber);
        variables.put("portCode", portCode);
        variables.put("navireName", navireName);
        
        if (expectedArrivalTime != null) {
            variables.put("expectedArrivalTime", expectedArrivalTime);
        } else {
            // Default to 24 hours from now if not provided
            variables.put("expectedArrivalTime", System.currentTimeMillis() / 1000 + 86400);
        }
        
        // Start the workflow
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey(
                "manifestWorkflow", 
                manifestId, // Use manifestId as business key
                variables
        );
        
        // Prepare response
        Map<String, Object> response = new HashMap<>();
        response.put("manifestId", manifestId);
        response.put("processInstanceId", processInstance.getId());
        response.put("status", "STARTED");
        response.put("startTime", System.currentTimeMillis());
        
        log.info("Manifest workflow started for manifest ID: {}, process instance ID: {}", 
                manifestId, processInstance.getId());
        
        return ResponseEntity.ok(response);
    }

    /**
     * Signal a ship arrival event in a running manifest workflow.
     * @param manifestId The unique identifier for the manifest
     * @param actualArrivalTime The actual arrival time of the ship
     * @return Status of the signal operation
     */
    @PostMapping("/{manifestId}/ship-arrival")
    public ResponseEntity<Map<String, Object>> signalShipArrival(
            @PathVariable String manifestId,
            @RequestParam Long actualArrivalTime
    ) {
        log.info("Signaling ship arrival for manifest ID: {}", manifestId);
        
        // Prepare variables for the message
        Map<String, Object> variables = new HashMap<>();
        variables.put("actualArrivalTime", actualArrivalTime);
        variables.put("status", "ARRIVED");
        
        // Send the message to the workflow
        blockchainEventListener.signalManifestWorkflow(manifestId, "shipArrivalTimeUpdated", variables);
        
        // Prepare response
        Map<String, Object> response = new HashMap<>();
        response.put("manifestId", manifestId);
        response.put("status", "SIGNAL_SENT");
        response.put("signalTime", System.currentTimeMillis());
        response.put("actualArrivalTime", actualArrivalTime);
        
        log.info("Ship arrival signal sent for manifest ID: {}", manifestId);
        
        return ResponseEntity.ok(response);
    }

    /**
     * Signal a DAP status update in a running manifest workflow.
     * @param manifestId The unique identifier for the manifest
     * @param status The new status of the DAP
     * @return Status of the signal operation
     */
    @PostMapping("/{manifestId}/dap-status")
    public ResponseEntity<Map<String, Object>> signalDAPStatusUpdate(
            @PathVariable String manifestId,
            @RequestParam String status
    ) {
        log.info("Signaling DAP status update for manifest ID: {}", manifestId);
        
        // Prepare variables for the message
        Map<String, Object> variables = new HashMap<>();
        variables.put("status", status);
        
        // Send the message to the workflow
        blockchainEventListener.signalManifestWorkflow(manifestId, "dapStatusUpdated", variables);
        
        // Prepare response
        Map<String, Object> response = new HashMap<>();
        response.put("manifestId", manifestId);
        response.put("status", "SIGNAL_SENT");
        response.put("signalTime", System.currentTimeMillis());
        response.put("dapStatus", status);
        
        log.info("DAP status update signal sent for manifest ID: {}", manifestId);
        
        return ResponseEntity.ok(response);
    }

    /**
     * Get the status of a manifest workflow.
     * @param manifestId The unique identifier for the manifest
     * @return Status information for the workflow
     */
    @GetMapping("/{manifestId}")
    public ResponseEntity<Map<String, Object>> getManifestWorkflowStatus(
            @PathVariable String manifestId
    ) {
        log.info("Getting manifest workflow status for manifest ID: {}", manifestId);
        
        // Find the process instance by business key
        ProcessInstance processInstance = runtimeService.createProcessInstanceQuery()
                .processInstanceBusinessKey(manifestId)
                .singleResult();
        
        if (processInstance == null) {
            log.warn("No process instance found for manifest ID: {}", manifestId);
            return ResponseEntity.notFound().build();
        }
        
        // Get workflow variables
        Map<String, Object> variables = runtimeService.getVariables(processInstance.getId());
        
        // Prepare response
        Map<String, Object> response = new HashMap<>();
        response.put("manifestId", manifestId);
        response.put("processInstanceId", processInstance.getId());
        response.put("status", variables.getOrDefault("workflowStatus", "UNKNOWN"));
        response.put("currentTasks", taskService.createTaskQuery()
                .processInstanceId(processInstance.getId())
                .list().stream()
                .map(task -> Map.of(
                        "id", task.getId(),
                        "name", task.getName(),
                        "createTime", task.getCreateTime()
                ))
                .toList());
        
        // Add relevant variables to the response
        response.put("shipArrivalStatus", variables.getOrDefault("shipArrivalStatus", "UNKNOWN"));
        response.put("dapStatus", variables.getOrDefault("dapStatus", "UNKNOWN"));
        response.put("shipInfo", Map.of(
                "shipId", variables.getOrDefault("shipId", ""),
                "shipName", variables.getOrDefault("shipName", ""),
                "callId", variables.getOrDefault("callId", "")
        ));
        response.put("dapInfo", Map.of(
                "noticeNumber", variables.getOrDefault("noticeNumber", ""),
                "callNumber", variables.getOrDefault("callNumber", ""),
                "approved", variables.getOrDefault("dapApproved", false)
        ));
        
        log.info("Retrieved manifest workflow status for manifest ID: {}", manifestId);
        
        return ResponseEntity.ok(response);
    }
}