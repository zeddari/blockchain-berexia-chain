// SPDX-License-Identifier: UNLICENSED
pragma solidity ^0.8.17;

import "./PortnetEventBase.sol";
import "./access/Ownable.sol";

/**
 * @title ShipArrivalContract
 * @dev Contract for recording ship arrival events from RabbitMQ
 */
contract ShipArrivalContract is PortnetEventBase, Ownable {
    // Events specific to ship arrivals
    event ShipArrivalRecorded(
        uint256 numeroAvis,
        string numeroEscale,
        string nomPort,
        string codePort,
        string nomNavire,
        string etat,
        uint256 timestamp
    );
    
    event ShipArrivalTimeUpdated(
        bytes32 indexed eventId,
        uint256 expectedArrivalTime,
        uint256 actualArrivalTime
    );
    
    // Ship arrival specific properties
    struct ShipArrival {
        uint256 numeroAvis;
        string numeroEscale;
        string nomPort;
        string codePort;
        string nomNavire;
        string etat;
        uint256 timestamp;
        bool exists;
    }
    
    // Mapping to store all ship arrivals by their event ID
    mapping(uint256 => ShipArrival) private shipArrivals;
    
    // Mapping to track ship IDs to event IDs for quick lookup
    mapping(string => bytes32[]) private _shipIdToEvents;
    
    // Mapping to track call IDs to event IDs for quick lookup
    mapping(string => bytes32) private _callIdToEvent;
    
    // Mapping to track port IDs to ship arrival event IDs for quick lookup
    mapping(string => bytes32[]) private _portIdToEvents;
    
    /**
     * @dev Constructor sets up the contract with ownership
     */
    constructor() PortnetEventBase(0) Ownable(msg.sender) {
        // Initialize contract
    }
    
    /**
     * @dev Records a new ship arrival event on the blockchain
     * @param numeroAvis Unique identifier for the ship
     * @param numeroEscale Name of the ship
     * @param nomPort Name of the port
     * @param codePort Code of the port
     * @param nomNavire Name of the ship
     * @param etat Current status of the ship arrival (e.g., "EXPECTED", "ARRIVED")
     * @return eventId The ID of the recorded event
     */
    function recordShipArrival(
        uint256 numeroAvis,
        string memory numeroEscale,
        string memory nomPort,
        string memory codePort,
        string memory nomNavire,
        string memory etat
    ) public returns (bytes32) {
        // Generate a unique event ID based on the notice number (assuming it's unique)
        bytes32 eventId = keccak256(abi.encodePacked(numeroAvis, block.timestamp));
        
        // Store the ship arrival data
        ShipArrival memory arrival = ShipArrival({
            numeroAvis: numeroAvis,
            numeroEscale: numeroEscale,
            nomPort: nomPort,
            codePort: codePort,
            nomNavire: nomNavire,
            etat: etat,
            timestamp: block.timestamp,
            exists: true
        });
        
        // Update the existing ship arrival or create a new one
        shipArrivals[numeroAvis] = arrival;
        
        // Update the mappings
        _shipIdToEvents[numeroEscale].push(eventId);
        _callIdToEvent[numeroEscale] = eventId;
        _portIdToEvents[codePort].push(eventId);
        
        // Set the initial status
        _updateStatus(etat);
        
        // Record the event
        _recordEvent();
        
        // Emit the ship arrival specific event
        emit ShipArrivalRecorded(
            numeroAvis,
            numeroEscale,
            nomPort,
            codePort,
            nomNavire,
            etat,
            block.timestamp
        );
        
        return eventId;
    }
    
    /**
     * @dev Updates an existing ship arrival's arrival times
     * @param _numeroAvis The ship ID of the ship arrival to update
     * @param _numeroEscale New name of the ship
     * @param _nomPort New name of the port
     * @param _codePort New code of the port
     * @param _nomNavire New name of the ship
     * @param _etat New status of the ship arrival
     */
    function updateShipArrival(
        uint256 _numeroAvis,
        string memory _numeroEscale,
        string memory _nomPort,
        string memory _codePort,
        string memory _nomNavire,
        string memory _etat
    ) public onlyOwner {
        require(shipArrivals[_numeroAvis].exists, "Ship arrival not found");

        ShipArrival storage arrival = shipArrivals[_numeroAvis];
        arrival.numeroEscale = _numeroEscale;
        arrival.nomPort = _nomPort;
        arrival.codePort = _codePort;
        arrival.nomNavire = _nomNavire;
        arrival.etat = _etat;
        arrival.timestamp = block.timestamp;

        emit ShipArrivalRecorded(
            _numeroAvis,
            _numeroEscale,
            _nomPort,
            _codePort,
            _nomNavire,
            _etat,
            block.timestamp
        );
    }
    
    /**
     * @dev Gets a ship arrival by ship ID
     * @param _numeroAvis The ship ID to look up
     * @return numeroAvis Ship identifier
     * @return numeroEscale Name of the ship
     * @return nomPort Name of the port
     * @return codePort Code of the port
     * @return nomNavire Name of the ship
     * @return etat Current status
     * @return timestamp Timestamp of the arrival
     */
    function getShipArrival(uint256 _numeroAvis) public view returns (
        uint256,
        string memory,
        string memory,
        string memory,
        string memory,
        string memory,
        uint256
    ) {
        require(shipArrivals[_numeroAvis].exists, "Ship arrival not found");
        ShipArrival memory arrival = shipArrivals[_numeroAvis];
        return (
            arrival.numeroAvis,
            arrival.numeroEscale,
            arrival.nomPort,
            arrival.codePort,
            arrival.nomNavire,
            arrival.etat,
            arrival.timestamp
        );
    }
    
    /**
     * @dev Gets all event IDs for a ship
     * @param shipId The ship ID to look up
     * @return An array of event IDs
     */
    function getShipEvents(string memory shipId) external view returns (bytes32[] memory) {
        return _shipIdToEvents[shipId];
    }
    
    /**
     * @dev Gets all event IDs for a port
     * @param portId The port ID to look up
     * @return An array of event IDs
     */
    function getPortEvents(string memory portId) external view returns (bytes32[] memory) {
        return _portIdToEvents[portId];
    }
    
    /**
     * @dev Checks if a ship arrival exists
     * @param _numeroAvis The ship ID to check
     * @return True if the ship arrival exists, false otherwise
     */
    function shipArrivalExists(uint256 _numeroAvis) public view returns (bool) {
        return shipArrivals[_numeroAvis].exists;
    }
} 