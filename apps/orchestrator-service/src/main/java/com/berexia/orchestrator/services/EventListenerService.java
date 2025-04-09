package com.berexia.orchestrator.services;

import com.berexia.business.event.manager.dto.DAPEvent;
import com.berexia.business.event.manager.dto.ShipArrivalEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.runtime.Execution;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Service that listens for events from RabbitMQ and signals the workflow process to continue.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class EventListenerService {

    private final RuntimeService runtimeService;

    /**
     * Listens for Ship Arrival events from RabbitMQ and signals the workflow process to continue.
     *
     * @param event The ship arrival event
     */
    @RabbitListener(queues = "${berexia.event.manager.rabbit.ship-arrival.queue-name:ship-arrival-queue}")
    public void handleShipArrivalEvent(ShipArrivalEvent event) {
        log.info("Received Ship Arrival event: {}", event);
        
        try {
            // Find workflow executions waiting for this event
            Long numeroAvis = event.getNumeroAvis();
            String numeroEscale = event.getNumeroEscale();
            
            log.info("Finding workflow instances waiting for Ship Arrival event with numeroAvis: {} and numeroEscale: {}", 
                    numeroAvis, numeroEscale);
            
            List<Execution> waitingExecutions = runtimeService.createExecutionQuery()
                    .activityId("waitForShipArrivalEvent")
                    .list();
            
            log.info("Found {} workflow executions waiting for Ship Arrival events", waitingExecutions.size());
            
            boolean eventHandled = false;
            
            // Loop through waiting executions and find a match
            for (Execution execution : waitingExecutions) {
                String processInstanceId = execution.getProcessInstanceId();
                
                // Get variables from the process instance
                Map<String, Object> variables = runtimeService.getVariables(processInstanceId);
                Long numeroAvisWf = (Long) variables.get("numeroAvis");
                String numeroEscaleWf = (String) variables.get("numeroEscale");
                
                // Check if this execution is waiting for this event
                if (numeroAvis.equals(numeroAvisWf) && numeroEscale.equals(numeroEscaleWf)) {
                    log.info("Found matching workflow for Ship Arrival event: {}", processInstanceId);
                    
                    // Prepare variables to pass to the process
                    Map<String, Object> processVariables = new HashMap<>();
                    processVariables.put("shipArrivalEvent", event);
                    processVariables.put("shipArrivalEventReceivedTime", java.time.LocalDateTime.now());
                    
                    // Signal the execution to continue
                    runtimeService.signalEventReceived("shipArrivalTimeUpdated", execution.getId(), processVariables);
                    log.info("Signaled workflow {} to continue with Ship Arrival event", processInstanceId);
                    
                    eventHandled = true;
                    break;
                }
            }
            
            if (!eventHandled) {
                log.warn("No matching workflow found for Ship Arrival event with numeroAvis: {} and numeroEscale: {}", 
                        numeroAvis, numeroEscale);
            }
            
        } catch (Exception e) {
            log.error("Error handling Ship Arrival event", e);
        }
    }
    
    /**
     * Listens for DAP events from RabbitMQ and signals the workflow process to continue.
     *
     * @param event The DAP event
     */
    @RabbitListener(queues = "${berexia.event.manager.rabbit.dap-request.queue-name:dap-request-queue}")
    public void handleDAPEvent(DAPEvent event) {
        log.info("Received DAP event: {}", event);
        
        try {
            // Find workflow executions waiting for this event
            Long noticeNumber = event.getNoticeNumber();
            String callNumber = event.getCallNumber();
            
            log.info("Finding workflow instances waiting for DAP event with noticeNumber: {} and callNumber: {}", 
                    noticeNumber, callNumber);
            
            List<Execution> waitingExecutions = runtimeService.createExecutionQuery()
                    .activityId("waitForDapEvent")
                    .list();
            
            log.info("Found {} workflow executions waiting for DAP events", waitingExecutions.size());
            
            boolean eventHandled = false;
            
            // Loop through waiting executions and find a match
            for (Execution execution : waitingExecutions) {
                String processInstanceId = execution.getProcessInstanceId();
                
                // Get variables from the process instance
                Map<String, Object> variables = runtimeService.getVariables(processInstanceId);
                Long noticeNumberWf = (Long) variables.get("noticeNumber");
                String callNumberWf = (String) variables.get("callNumber");
                
                // Check if this execution is waiting for this event
                if (noticeNumber.equals(noticeNumberWf) && callNumber.equals(callNumberWf)) {
                    log.info("Found matching workflow for DAP event: {}", processInstanceId);
                    
                    // Prepare variables to pass to the process
                    Map<String, Object> processVariables = new HashMap<>();
                    processVariables.put("dapEvent", event);
                    processVariables.put("dapEventReceivedTime", java.time.LocalDateTime.now());
                    
                    // Signal the execution to continue
                    runtimeService.signalEventReceived("dapStatusUpdated", execution.getId(), processVariables);
                    log.info("Signaled workflow {} to continue with DAP event", processInstanceId);
                    
                    eventHandled = true;
                    break;
                }
            }
            
            if (!eventHandled) {
                log.warn("No matching workflow found for DAP event with noticeNumber: {} and callNumber: {}", 
                        noticeNumber, callNumber);
            }
            
        } catch (Exception e) {
            log.error("Error handling DAP event", e);
        }
    }
} 