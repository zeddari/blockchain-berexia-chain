// SPDX-License-Identifier: UNLICENSED
pragma solidity ^0.8.17;

/**
 * @title IPortnetEvent
 * @dev Interface for all Portnet events
 */
interface IPortnetEvent {
    /**
     * @dev Returns the event ID
     * @return The unique identifier for the event
     */
    function getEventId() external view returns (bytes32);
    
    /**
     * @dev Returns the timestamp when the event was recorded on chain
     * @return The timestamp
     */
    function getTimestamp() external view returns (uint256);
    
    /**
     * @dev Returns the status of the event
     * @return The status string
     */
    function getStatus() external view returns (string memory);
} 