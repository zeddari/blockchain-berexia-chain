package com.berexia.orchestrator.services;

import com.berexia.orchestrator.models.ManifestWorkflowRequest;
import com.berexia.orchestrator.models.WorkflowResponse;
import com.berexia.orchestrator.models.WorkflowStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.runtime.ProcessInstance;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Service for managing the Manifest Workflow.
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class ManifestWorkflowService {

    private final RuntimeService runtimeService;

    /**
     * Start a new Manifest Workflow process.
     *
     * @param request The workflow request containing required data
     * @return WorkflowResponse with process instance details
     */
    public WorkflowResponse startWorkflow(ManifestWorkflowRequest request) {
        log.info("Starting Manifest Workflow with request: {}", request);
        
        try {
            // Create variables map for the process
            Map<String, Object> variables = new HashMap<>();
            variables.put("shipId", request.getShipId());
            variables.put("shipName", request.getShipName());
            variables.put("portId", request.getPortId());
            variables.put("portName", request.getPortName());
            variables.put("callId", request.getCallId());
            variables.put("expectedArrivalTime", request.getExpectedArrivalTime());
            variables.put("operatorName", request.getOperatorName());
            variables.put("startTime", LocalDateTime.now());
            variables.put("workflowStatus", WorkflowStatus.INITIATED.name());
            
            // Start the process instance
            ProcessInstance processInstance = runtimeService.startProcessInstanceByKey(
                    "manifestWorkflow", 
                    "MANIFEST_" + request.getShipId() + "_" + request.getCallId(),
                    variables);
            
            log.info("Manifest Workflow started with process instance ID: {}", processInstance.getId());
            
            // Return the workflow response
            return WorkflowResponse.builder()
                    .workflowInstanceId(processInstance.getId())
                    .workflowName("Manifest Workflow")
                    .status(WorkflowStatus.INITIATED)
                    .startTime(LocalDateTime.now())
                    .lastUpdatedTime(LocalDateTime.now())
                    .message("Manifest Workflow initiated successfully")
                    .build();
            
        } catch (Exception e) {
            log.error("Error starting Manifest Workflow", e);
            return WorkflowResponse.builder()
                    .workflowName("Manifest Workflow")
                    .status(WorkflowStatus.FAILED)
                    .startTime(LocalDateTime.now())
                    .lastUpdatedTime(LocalDateTime.now())
                    .message("Failed to start Manifest Workflow: " + e.getMessage())
                    .build();
        }
    }
    
    /**
     * Get the current status of a workflow instance.
     *
     * @param processInstanceId The process instance ID
     * @return WorkflowResponse with current status
     */
    public WorkflowResponse getWorkflowStatus(String processInstanceId) {
        log.info("Getting status for workflow with process instance ID: {}", processInstanceId);
        
        try {
            ProcessInstance processInstance = runtimeService.createProcessInstanceQuery()
                    .processInstanceId(processInstanceId)
                    .singleResult();
            
            if (processInstance == null) {
                log.warn("Process instance not found: {}", processInstanceId);
                return WorkflowResponse.builder()
                        .workflowInstanceId(processInstanceId)
                        .workflowName("Manifest Workflow")
                        .status(WorkflowStatus.FAILED)
                        .lastUpdatedTime(LocalDateTime.now())
                        .message("Process instance not found")
                        .build();
            }
            
            // Get workflow variables
            Map<String, Object> variables = runtimeService.getVariables(processInstanceId);
            WorkflowStatus status = WorkflowStatus.valueOf((String) variables.getOrDefault("workflowStatus", WorkflowStatus.IN_PROGRESS.name()));
            LocalDateTime startTime = (LocalDateTime) variables.getOrDefault("startTime", LocalDateTime.now());
            
            return WorkflowResponse.builder()
                    .workflowInstanceId(processInstanceId)
                    .workflowName("Manifest Workflow")
                    .status(status)
                    .startTime(startTime)
                    .lastUpdatedTime(LocalDateTime.now())
                    .message("Workflow is " + status.name().toLowerCase())
                    .context(variables)
                    .build();
            
        } catch (Exception e) {
            log.error("Error getting workflow status", e);
            return WorkflowResponse.builder()
                    .workflowInstanceId(processInstanceId)
                    .workflowName("Manifest Workflow")
                    .status(WorkflowStatus.FAILED)
                    .lastUpdatedTime(LocalDateTime.now())
                    .message("Error getting workflow status: " + e.getMessage())
                    .build();
        }
    }
    
    /**
     * Signal a receive task to continue the workflow.
     *
     * @param processInstanceId The process instance ID
     * @param receiveTaskId The ID of the receive task
     * @param variables Variables to pass to the task
     * @return true if signaled successfully, false otherwise
     */
    public boolean signalReceiveTask(String processInstanceId, String receiveTaskId, Map<String, Object> variables) {
        log.info("Signaling receive task {} for process instance {}", receiveTaskId, processInstanceId);
        
        try {
            // Set variables in the execution
            if (variables != null && !variables.isEmpty()) {
                runtimeService.setVariables(processInstanceId, variables);
            }
            
            // Trigger the receive task
            runtimeService.trigger(processInstanceId, variables);
            log.info("Receive task signaled successfully");
            return true;
        } catch (Exception e) {
            log.error("Error signaling receive task", e);
            return false;
        }
    }
} 