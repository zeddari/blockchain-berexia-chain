// SPDX-License-Identifier: UNLICENSED
pragma solidity ^0.8.17;

import "./PortnetEventBase.sol";
import "./access/Ownable.sol";

/**
 * @title DAPContract
 * @dev Contract for recording DAP (Demande d'Autorisation Préalable) events from RabbitMQ
 */
contract DAPContract is PortnetEventBase, Ownable {
    // Events specific to DAP
    event DAPRecorded(
        uint256 noticeNumber,
        string callNumber,
        string portCode,
        string shipName,
        string operatorName
    );
    
    event DAPStatusUpdated(
        uint256 indexed noticeNumber,
        string oldStatus,
        string newStatus
    );
    
    // DAP specific properties
    struct DAP {
        uint256 noticeNumber;
        string callNumber;
        string portName;
        string portCode;
        string shipName;
        string operatorName;
        uint256 requestDate;
        bool isRegistered;
    }
    
    // Mapping to store all DAPs by their event ID
    mapping(bytes32 => DAP) private _daps;
    
    // Mapping to track notice numbers to event IDs for quick lookup
    mapping(uint256 => bytes32) private _noticeNumberToEvent;
    
    // Mapping to track call numbers to event IDs for quick lookup
    mapping(string => bytes32[]) private _callNumberToEvents;
    
    // Mapping to track port codes to DAP event IDs for quick lookup
    mapping(string => bytes32[]) private _portCodeToEvents;
    
    /**
     * @dev Constructor sets up the contract with ownership
     */
    constructor() PortnetEventBase(0) Ownable(msg.sender) {
        // Initialize contract
    }
    
    /**
     * @dev Records a new DAP event on the blockchain
     * @param noticeNumber Unique identifier for the notice
     * @param callNumber Port call number associated with the notice
     * @param portName Name of the port
     * @param portCode Code of the port
     * @param shipName Name of the ship
     * @param operatorName Name of the ship operator
     * @param status Current status of the DAP (e.g., "PENDING", "APPROVED", "REJECTED")
     * @param requestDate Time when the request was made (Unix timestamp)
     * @return The generated event ID
     */
    function recordDAP(
        uint256 noticeNumber,
        string memory callNumber,
        string memory portName,
        string memory portCode,
        string memory shipName,
        string memory operatorName,
        string memory status,
        uint256 requestDate
    ) external onlyOwner returns (bytes32) {
        // Generate a unique event ID based on the notice number (assuming it's unique)
        bytes32 eventId = keccak256(abi.encodePacked(noticeNumber, block.timestamp));
        
        // Check if a DAP with this notice number already exists
        require(_noticeNumberToEvent[noticeNumber] == bytes32(0), "DAP with this notice number already registered");
        
        // Store the DAP data
        DAP memory dap = DAP({
            noticeNumber: noticeNumber,
            callNumber: callNumber,
            portName: portName,
            portCode: portCode,
            shipName: shipName,
            operatorName: operatorName,
            requestDate: requestDate,
            isRegistered: true
        });
        
        _daps[eventId] = dap;
        _noticeNumberToEvent[noticeNumber] = eventId;
        _callNumberToEvents[callNumber].push(eventId);
        _portCodeToEvents[portCode].push(eventId);
        
        // Set the initial status
        _updateStatus(status);
        
        // Record the event
        _recordEvent();
        
        // Emit the DAP specific event
        emit DAPRecorded(
            noticeNumber,
            callNumber,
            portCode,
            shipName,
            operatorName
        );
        
        return eventId;
    }
    
    /**
     * @dev Updates an existing DAP's status
     * @param noticeNumber The notice number of the DAP to update
     * @param newStatus New status of the DAP
     */
    function updateDAPStatus(
        uint256 noticeNumber,
        string memory newStatus
    ) external onlyOwner {
        // Get the event ID for this notice number
        bytes32 eventId = _noticeNumberToEvent[noticeNumber];
        require(eventId != bytes32(0), "DAP not found for this notice number");
        
        // Get the DAP
        DAP storage dap = _daps[eventId];
        require(dap.isRegistered, "DAP not registered");
        
        // Get the old status
        string memory oldStatus = this.getStatus();
        
        // Update the status
        _updateStatus(newStatus);
        
        // Emit the update event
        emit DAPStatusUpdated(
            noticeNumber,
            oldStatus,
            newStatus
        );
    }
    
    /**
     * @dev Gets a DAP by notice number
     * @param noticeNumber The notice number to look up
     * @return callNumber Call number associated with the notice
     * @return portName Name of the port
     * @return portCode Code of the port
     * @return shipName Name of the ship
     * @return operatorName Name of the operator
     * @return status Current status
     * @return requestDate Time when the request was made
     */
    function getDAPByNoticeNumber(uint256 noticeNumber) external view returns (
        string memory callNumber,
        string memory portName,
        string memory portCode,
        string memory shipName,
        string memory operatorName,
        string memory status,
        uint256 requestDate
    ) {
        bytes32 eventId = _noticeNumberToEvent[noticeNumber];
        require(eventId != bytes32(0), "DAP not found for this notice number");
        
        DAP storage dap = _daps[eventId];
        require(dap.isRegistered, "DAP not registered");
        
        return (
            dap.callNumber,
            dap.portName,
            dap.portCode,
            dap.shipName,
            dap.operatorName,
            this.getStatus(),
            dap.requestDate
        );
    }
    
    /**
     * @dev Gets all event IDs for a call number
     * @param callNumber The call number to look up
     * @return An array of event IDs
     */
    function getCallNumberEvents(string memory callNumber) external view returns (bytes32[] memory) {
        return _callNumberToEvents[callNumber];
    }
    
    /**
     * @dev Gets all event IDs for a port code
     * @param portCode The port code to look up
     * @return An array of event IDs
     */
    function getPortCodeEvents(string memory portCode) external view returns (bytes32[] memory) {
        return _portCodeToEvents[portCode];
    }
    
    /**
     * @dev Checks if a DAP exists for a notice number
     * @param noticeNumber The notice number to check
     * @return True if a DAP exists, false otherwise
     */
    function dapExists(uint256 noticeNumber) external view returns (bool) {
        return _noticeNumberToEvent[noticeNumber] != bytes32(0);
    }
} 