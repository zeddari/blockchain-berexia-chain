// SPDX-License-Identifier: UNLICENSED
pragma solidity ^0.8.17;

import "./IPortnetEvent.sol";

/**
 * @title PortnetEventBase
 * @dev Base contract for Portnet events
 */
abstract contract PortnetEventBase is IPortnetEvent {
    // Event emitted when an event is recorded
    event EventRecorded(bytes32 indexed eventId, address indexed recorder, uint256 timestamp);
    
    // Event emitted when an event's status is updated
    event StatusUpdated(bytes32 indexed eventId, string oldStatus, string newStatus);
    
    // Common properties for all events
    bytes32 private _eventId;
    uint256 private _timestamp;
    string private _status;
    address private _recorder;
    
    /**
     * @dev Constructor to set common properties
     * @param eventId The unique identifier for the event
     */
    constructor(bytes32 eventId) {
        _eventId = eventId;
        _timestamp = block.timestamp;
        _recorder = msg.sender;
    }
    
    /**
     * @dev Returns the event ID
     * @return The unique identifier for the event
     */
    function getEventId() external view override returns (bytes32) {
        return _eventId;
    }
    
    /**
     * @dev Returns the timestamp when the event was recorded on chain
     * @return The timestamp
     */
    function getTimestamp() external view override returns (uint256) {
        return _timestamp;
    }
    
    /**
     * @dev Returns the status of the event
     * @return The status string
     */
    function getStatus() external view override returns (string memory) {
        return _status;
    }
    
    /**
     * @dev Returns the address that recorded this event
     * @return The recorder's address
     */
    function getRecorder() external view returns (address) {
        return _recorder;
    }
    
    /**
     * @dev Updates the event's status
     * @param newStatus The new status to set
     */
    function _updateStatus(string memory newStatus) internal {
        string memory oldStatus = _status;
        _status = newStatus;
        emit StatusUpdated(_eventId, oldStatus, newStatus);
    }
    
    /**
     * @dev Records the event on chain
     * Should be called by child contracts after setting specific properties
     */
    function _recordEvent() internal {
        emit EventRecorded(_eventId, _recorder, _timestamp);
    }
} 