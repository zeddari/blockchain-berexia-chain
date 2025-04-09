package com.berexia.orchestrator.listener.blockchain;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.flowable.engine.RuntimeService;
import org.flowable.engine.runtime.Execution;
import org.flowable.engine.runtime.ProcessInstance;
import org.springframework.stereotype.Component;
import org.web3j.protocol.Web3j;

import com.berexia.orchestrator.service.blockchain.DAPService;
import com.berexia.orchestrator.service.blockchain.ShipArrivalService;

import io.reactivex.disposables.Disposable;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Component that listens for events from the blockchain contracts and initiates Flowable processes.
 * Currently only supporting the manifest workflow.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class BlockchainEventListener {

    private final Web3j web3j;
    private final ShipArrivalService shipArrivalService;
    private final DAPService dapService;
    private final RuntimeService runtimeService;

    private final List<Disposable> subscriptions = new ArrayList<>();

    /**
     * Initializes the event listeners when the application starts.
     */
    @PostConstruct
    public void init() {
        try {
            log.info("Initializing blockchain event listeners for manifest workflow");
            // Here we would set up event listeners specific to the manifest workflow
            // Currently, there are no specific blockchain events to listen to for the manifest workflow
            
            log.info("Blockchain event listeners initialized successfully for manifest workflow");
        } catch (Exception e) {
            log.error("Failed to initialize blockchain event listeners", e);
        }
    }

    /**
     * Clean up the event subscriptions when the application shuts down.
     */
    @PreDestroy
    public void cleanup() {
        log.info("Disposing blockchain event subscriptions");
        subscriptions.forEach(Disposable::dispose);
    }

    /**
     * Helper method to create process variables for the manifest workflow.
     * @param manifestId The unique identifier for the manifest
     * @param variables Additional variables to include
     * @return A map of process variables
     */
    private Map<String, Object> createManifestWorkflowVariables(String manifestId, Map<String, Object> variables) {
        Map<String, Object> processVariables = new HashMap<>(variables);
        processVariables.put("manifestId", manifestId);
        processVariables.put("startedAt", System.currentTimeMillis());
        return processVariables;
    }

    /**
     * Start a manifest workflow process.
     * @param manifestId The unique identifier for the manifest
     * @param variables The variables to start the process with
     */
    public void startManifestWorkflow(String manifestId, Map<String, Object> variables) {
        try {
            Map<String, Object> processVariables = createManifestWorkflowVariables(manifestId, variables);
            
            // Start the process instance
            runtimeService.startProcessInstanceByKey(
                    "manifestWorkflow", 
                    manifestId,  // Business key
                    processVariables
            );
            
            log.info("Started manifest workflow for manifest ID: {}", manifestId);
        } catch (Exception e) {
            log.error("Failed to start manifest workflow for manifest ID: {}", manifestId, e);
        }
    }

    /**
     * Signal a running manifest workflow process.
     * @param manifestId The unique identifier for the manifest
     * @param messageName The name of the message to send
     * @param variables The variables to update in the process
     */
    public void signalManifestWorkflow(String manifestId, String signalName, Map<String, Object> variables) {
        try {
            // 1. Get active process instance by business key
            List<ProcessInstance> processInstances = runtimeService.createProcessInstanceQuery()
                    .processInstanceBusinessKey(manifestId)
                    .active()
                    .list();
    
            if (processInstances.isEmpty()) {
                log.warn("⚠️ No active process instance found for manifest ID: {}", manifestId);
                return;
            }
    
            String processInstanceId = processInstances.get(0).getId();
    
            // 2. Find the execution subscribed to the signal event
            Execution signalExecution = runtimeService.createExecutionQuery()
                        .processInstanceId(processInstanceId)
                        .signalEventSubscriptionName("shipArrivalTimeUpdated")
                        .singleResult();
    
            // 3. Optional: Debug log all executions in the process
            List<Execution> executions = runtimeService.createExecutionQuery()
                    .processInstanceId(processInstanceId)
                    .list();
    
            executions.forEach(exec -> {
                log.debug("Execution ID: {} at activity: {}", exec.getId(), exec.getActivityId());
            });
    
            // 4. If found, send the signal to that execution
            if (signalExecution != null) {
                runtimeService.signalEventReceived(signalName, signalExecution.getId(), variables);
                log.info("✅ Signaled execution ID {} in process {} with signal '{}'", 
                    signalExecution.getId(), processInstanceId, signalName);
            } else {
                log.warn("⚠️ No execution waiting for signal '{}' in process instance ID: {}", 
                    signalName, processInstanceId);
            }
    
        } catch (Exception e) {
            log.error("❌ Failed to signal manifest workflow for manifest ID: {} with signal: {}", 
                manifestId, signalName, e);
        }
    }
} 