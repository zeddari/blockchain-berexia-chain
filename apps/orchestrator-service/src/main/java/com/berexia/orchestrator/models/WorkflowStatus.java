package com.berexia.orchestrator.models;

/**
 * Enum representing the possible statuses of a workflow or workflow step.
 */
public enum WorkflowStatus {
    /**
     * The workflow or step has been initiated but not yet started.
     */
    INITIATED,

    /**
     * The workflow or step is currently in progress.
     */
    IN_PROGRESS,

    /**
     * The workflow or step has completed successfully.
     */
    COMPLETED,

    /**
     * The workflow or step has failed.
     */
    FAILED,

    /**
     * The workflow or step is waiting for an external event.
     */
    WAITING,

    /**
     * The workflow or step has been cancelled.
     */
    CANCELLED
} 