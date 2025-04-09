# Berexia Chain Smart Contracts

This directory contains the Solidity smart contracts for the Berexia Trade blockchain integration, specifically for recording and tracking Ship Arrivals and DAP (Demande d'Autorisation Préalable) events.

## Smart Contracts Overview

The following smart contracts are implemented:

1. **IPortnetEvent**: Interface defining common functionality for Portnet events.
2. **PortnetEventBase**: Abstract base contract implementing common event functionality.
3. **ShipArrivalContract**: Contract for recording and tracking ship arrival events.
4. **DAPContract**: Contract for recording and tracking DAP events.
5. **PortnetContractFactory**: Factory contract for deploying ship arrival and DAP contracts.

## Blockchain Endpoints

The project is configured to work with the following blockchain endpoint:

- **JSON-RPC HTTP service**: http://157.173.119.195:8545
- **JSON-RPC WebSocket service**: ws://157.173.119.195:8546
- **Web block explorer**: http://157.173.119.195:25000/explorer/nodes
- **Chainlens**: http://157.173.119.195:8081/
- **Prometheus**: http://157.173.119.195:9090/graph
- **Grafana**: http://157.173.119.195:3000/d/XE4V0WGZz/besu-overview
- **Logs (Grafana)**: http://157.173.119.195:3000/d/Ak6eXLsPxFemKYKEXfcH/quorum-logs-loki
- **Logs (Kibana)**: http://157.173.119.195:5601/app/kibana#/discover

## Prerequisites

- Node.js 16+ and npm
- Solidity ^0.8.17
- Truffle 5.11+
- Access to the Berexia blockchain network

## Installation

1. Install dependencies:

```bash
npm install
```

2. Ensure the root `.env` file is configured with the correct values:

```
# Blockchain Configuration
BLOCKCHAIN_RPC_URL=http://157.173.119.195:8545
BLOCKCHAIN_PRIVATE_KEY=your-private-key-here
```

## Compilation

Compile the smart contracts:

```bash
npm run compile
```

This will generate compiled contract artifacts in the `build/contracts` directory.

## Deployment

There are two ways to deploy the contracts:

### 1. Using Truffle

Deploy the contracts to the Berexia blockchain network:

```bash
npx truffle migrate --network berexia
```

### 2. Using the deployment script

Run the deployment script directly:

```bash
npm run deploy
```

Or with a private key as argument:

```bash
node scripts/deploy.js your-private-key
```

The deployment process will:
1. Deploy the `PortnetContractFactory` contract
2. Use the factory to deploy both `ShipArrivalContract` and `DAPContract`
3. Update the `.env` file with the deployed contract addresses
4. Create a `deployment-info.json` file with deployment details

## Verification

After deployment, you can verify the contracts on the block explorer:

1. Go to http://157.173.119.195:25000/explorer/nodes
2. Navigate to the deployed contract addresses
3. Ensure the contract functions are accessible

## Contract Functions

### ShipArrivalContract

- `recordShipArrival`: Records a new ship arrival event
- `updateShipArrival`: Updates an existing ship arrival event
- `getShipArrivalByCallId`: Gets a ship arrival by its call ID
- `shipArrivalExists`: Checks if a ship arrival exists

### DAPContract

- `recordDAP`: Records a new DAP event
- `updateDAPStatus`: Updates the status of an existing DAP
- `getDAPByNoticeNumber`: Gets a DAP by its notice number
- `dapExists`: Checks if a DAP exists

## Integration with Business Event Manager

After deployment, the Business Event Manager can be configured to interact with these contracts. The configuration is in the `.env` file, which is automatically updated by the deployment scripts.

Make sure to restart the Business Event Manager services after deployment to pick up the new contract addresses.

## Security Considerations

- Private keys should be kept secure and never committed to version control
- Consider using a key management service for production deployments
- Contract ownership should be transferred to a secure multisig wallet after deployment

## License

This project is proprietary and confidential. All rights reserved. 