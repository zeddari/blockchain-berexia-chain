// SPDX-License-Identifier: UNLICENSED
pragma solidity ^0.8.17;

import "./ShipArrivalContract.sol";
import "./DAPContract.sol";
import "./access/Ownable.sol";

/**
 * @title PortnetContractFactory
 * @dev Factory contract to deploy and manage ShipArrival and DAP contracts
 */
contract PortnetContractFactory is Ownable {
    // Events
    event ShipArrivalContractDeployed(address indexed contractAddress, uint256 deployedAt);
    event DAPContractDeployed(address indexed contractAddress, uint256 deployedAt);
    
    // Track deployed contracts
    address public shipArrivalContract;
    address public dapContract;
    
    /**
     * @dev Constructor sets up the factory with ownership
     */
    constructor() Ownable(msg.sender) {
        // Initialize factory
    }
    
    /**
     * @dev Deploys a new ShipArrivalContract
     * @return The address of the deployed contract
     */
    function deployShipArrivalContract() public onlyOwner returns (address) {
        ShipArrivalContract newContract = new ShipArrivalContract();
        
        // Transfer ownership of the contract to the caller
        newContract.transferOwnership(msg.sender);
        
        // Update the address of the deployed contract
        shipArrivalContract = address(newContract);
        
        // Emit event
        emit ShipArrivalContractDeployed(address(newContract), block.timestamp);
        
        return address(newContract);
    }
    
    /**
     * @dev Deploys a new DAPContract
     * @return The address of the deployed contract
     */
    function deployDAPContract() public onlyOwner returns (address) {
        DAPContract newContract = new DAPContract();
        
        // Transfer ownership of the contract to the caller
        newContract.transferOwnership(msg.sender);
        
        // Update the address of the deployed contract
        dapContract = address(newContract);
        
        // Emit event
        emit DAPContractDeployed(address(newContract), block.timestamp);
        
        return address(newContract);
    }
    
    /**
     * @dev Deploys both ShipArrival and DAP contracts
     * @return shipArrivalAddress The address of the deployed ShipArrivalContract
     * @return dapAddress The address of the deployed DAPContract
     */
    function deployAllContracts() external onlyOwner returns (address shipArrivalAddress, address dapAddress) {
        shipArrivalAddress = deployShipArrivalContract();
        dapAddress = deployDAPContract();
        
        return (shipArrivalAddress, dapAddress);
    }
} 