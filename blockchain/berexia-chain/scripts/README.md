# ShipArrivalContract Testing Scripts

This directory contains scripts for testing the ShipArrivalContract on the Berexia Chain network.

## Network Configuration

The scripts are configured to use the Berexia Chain network at:
```
http://157.173.119.195:8545
```

## Prerequisites

Before running the tests, ensure you have:

1. Node.js and npm installed
2. Truffle installed globally (`npm install -g truffle`)
3. An `account.json` file in the project root with your private key and address:
   ```json
   {
     "privateKey": "your_private_key_here",
     "address": "your_address_here"
   }
   ```

   > **Note:** The private key can be provided with or without the "0x" prefix. The scripts will automatically handle both formats.

## Available Scripts

### 1. Deploy and Test Ship Arrival Contract

This script deploys the ShipArrivalContract to the Berexia Chain network and runs a basic test:

```bash
node scripts/deploy-and-test-ship-arrival.js
```

### 2. Run Ship Arrival Tests

This script runs the full test suite for the ShipArrivalContract:

```bash
node scripts/test-ship-arrival.js
```

## Running Tests on Berexia Chain

1. Ensure your `account.json` file is properly configured with your private key and address
2. The scripts will automatically:
   - Load your account information from `account.json`
   - Format the private key correctly (removing "0x" prefix if present)
   - Create a temporary `.env` file with the necessary configuration
   - Deploy and test the contract on the Berexia Chain network

## Troubleshooting

### Common Issues

1. **Invalid Private Key**
   - Ensure your private key in `account.json` is correct
   - The private key can be provided with or without the "0x" prefix
   - If you're still having issues, try removing the "0x" prefix manually

2. **Network Connection**
   - Verify you can connect to the Berexia Chain network
   - Check if the RPC URL is accessible

3. **Insufficient Funds**
   - Ensure your account has enough funds for deployment and testing
   - The account address in `account.json` must have sufficient balance

### Getting Help

If you encounter any issues:
1. Check the error messages in the console
2. Verify your `account.json` configuration
3. Ensure you have the latest version of the scripts

## Test Data

The test scripts use the following sample data:

```javascript
const sampleData = [
  {
    numeroAvis: 12345,
    numeroEscale: "ESC001",
    nomPort: "Casablanca",
    codePort: "CASA",
    nomNavire: "Test Ship 1",
    etat: "EN_ATTENTE"
  },
  {
    numeroAvis: 12346,
    numeroEscale: "ESC002",
    nomPort: "Tanger",
    codePort: "TNG",
    nomNavire: "Test Ship 2",
    etat: "EN_ATTENTE"
  },
  {
    numeroAvis: 12347,
    numeroEscale: "ESC003",
    nomPort: "Agadir",
    codePort: "AGA",
    nomNavire: "Test Ship 3",
    etat: "EN_ATTENTE"
  }
];
``` 