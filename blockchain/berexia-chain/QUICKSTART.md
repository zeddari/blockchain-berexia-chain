# Blockchain Integration Quick Start Guide

This guide provides a quick overview of how to deploy and integrate the blockchain smart contracts with the Business Event Manager.

## Prerequisites

- Node.js and npm installed
- Access to the Berexia blockchain network
- A funded account with Ether for gas fees

## Step 1: Clone the Repository

```bash
git clone <repository-url>
cd berexia-trade
```

## Step 2: Install Dependencies

```bash
cd blockchain/berexia-chain
npm install
```

## Step 3: Configure Environment

Create a `.env` file in the root directory of the project:

```bash
cp .env.example .env
```

Edit the `.env` file to add your private key:

```
# Blockchain Configuration
BLOCKCHAIN_RPC_URL=http://157.173.119.195:8545
BLOCKCHAIN_WS_URL=ws://157.173.119.195:8546
BLOCKCHAIN_PRIVATE_KEY=your-private-key-here  # <-- Replace this with your actual private key
```

## Step 4: Compile Smart Contracts

```bash
npm run compile
```

## Step 5: Deploy Smart Contracts

```bash
npm run deploy
```

This will:
1. Deploy the PortnetContractFactory
2. Deploy the ShipArrivalContract and DAPContract
3. Update the `.env` file with the contract addresses

After deployment, you should see output similar to:

```
PortnetContractFactory deployed at: 0x1234...
Ship Arrival Contract: 0x5678...
DAP Contract: 0x9012...
.env file updated with contract addresses
Deployment info saved to deployment-info.json
```

## Step 6: Verify Deployment

Check the deployment-info.json file to verify the contract addresses:

```bash
cat deployment-info.json
```

You can also verify the contracts on the block explorer:
http://157.173.119.195:25000/explorer/nodes

## Step 7: Configure the Business Event Manager

The `.env` file should now contain the correct contract addresses. Start or restart your application to pick up the new configuration.

## Step 8: Test the Integration

Send a test message to the appropriate RabbitMQ queue:

```bash
# Example for ship arrival event
curl -X POST http://localhost:8080/api/test/ship-arrival
```

Check the logs to verify the event was recorded on the blockchain:

```
INFO c.b.b.e.m.blockchain.BlockchainConnector : Ship arrival recorded on blockchain with transaction hash: 0x...
```

## Blockchain Explorer

You can view the transactions and contract interactions on the block explorer:

- Block Explorer: http://157.173.119.195:25000/explorer/nodes
- Chainlens: http://157.173.119.195:8081/

## Monitoring

The blockchain network can be monitored using:

- Prometheus: http://157.173.119.195:9090/graph
- Grafana: http://157.173.119.195:3000/d/XE4V0WGZz/besu-overview
- Logs (Grafana): http://157.173.119.195:3000/d/Ak6eXLsPxFemKYKEXfcH/quorum-logs-loki
- Logs (Kibana): http://157.173.119.195:5601/app/kibana#/discover

## Troubleshooting

### Transaction Failed

If a transaction fails, check:
- Account balance: Ensure your account has enough Ether for gas
- Contract addresses: Verify the addresses in the `.env` file
- Network connectivity: Ensure you can connect to the blockchain RPC endpoint

### RPC Connection Issues

If you cannot connect to the blockchain:
- Verify the RPC URL is correct
- Check if the blockchain node is running
- Try using the WebSocket endpoint instead

### Missing Contract Events

If events are not being recorded:
- Check if blockchain integration is enabled
- Verify the contract addresses
- Check the application logs for errors

## Next Steps

- Review the smart contract code to understand the available functions
- Explore the BlockchainConnector class to see how the integration works
- Consider setting up a monitoring service to track blockchain events 