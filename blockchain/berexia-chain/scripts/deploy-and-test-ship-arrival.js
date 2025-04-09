#!/usr/bin/env node

/**
 * Script to deploy and test the ShipArrivalContract on the specified network
 * 
 * Usage:
 *   node scripts/deploy-and-test-ship-arrival.js
 */

const Web3 = require('web3');
const fs = require('fs');
const path = require('path');
const { execSync } = require('child_process');

// Get the directory of this script
const scriptDir = path.dirname(__filename);
const projectDir = path.resolve(scriptDir, '..');

// Change to the project directory
process.chdir(projectDir);

// Network configuration
const NETWORK_URL = 'http://157.173.119.195:8545';
const NETWORK_NAME = 'berexia';

// Load account information from account.json
const accountInfo = JSON.parse(fs.readFileSync(path.join(projectDir, 'account.json'), 'utf8'));
// Remove '0x' prefix if present and ensure the private key is properly formatted
const PRIVATE_KEY = accountInfo.privateKey.startsWith('0x') 
  ? accountInfo.privateKey.substring(2) 
  : accountInfo.privateKey;
const ACCOUNT_ADDRESS = accountInfo.address;

// Sample data for testing
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

// Update data for testing
const updateData = {
  numeroEscale: "ESC001-UPDATED",
  nomPort: "Casablanca Updated",
  codePort: "CASA",
  nomNavire: "Test Ship 1 Updated",
  etat: "ARRIVE"
};

console.log('🚀 Deploying and testing ShipArrivalContract on network:', NETWORK_URL);
console.log('=====================================================');
console.log(`Using account: ${ACCOUNT_ADDRESS}`);

async function main() {
  try {
    // Step 1: Compile the contracts
    console.log('Compiling contracts...');
    execSync('npx truffle compile', { stdio: 'inherit' });
    
    // Step 2: Deploy the contract to the specified network
    console.log(`\nDeploying contract to network: ${NETWORK_URL}...`);
    
    // Create a temporary .env file with the private key from account.json
    const envContent = `BLOCKCHAIN_PRIVATE_KEY=${PRIVATE_KEY}\nBLOCKCHAIN_RPC_URL=${NETWORK_URL}`;
    fs.writeFileSync(path.join(projectDir, '.env'), envContent);
    
    execSync(`npx truffle migrate --reset --network ${NETWORK_NAME}`, { stdio: 'inherit' });
    
    // Step 3: Get the contract address from the build artifacts
    const contractData = JSON.parse(fs.readFileSync(path.join(projectDir, 'build/contracts/ShipArrivalContract.json'), 'utf8'));
    const contractAddress = contractData.networks[Object.keys(contractData.networks)[0]].address;
    console.log(`Contract deployed at: ${contractAddress}`);
    
    // Step 4: Connect to the specified network
    const web3 = new Web3(NETWORK_URL);
    
    // Step 5: Get the contract instance
    const ShipArrivalContract = new web3.eth.Contract(contractData.abi, contractAddress);
    
    // Step 6: Record ship arrivals
    console.log('\nRecording ship arrivals...');
    for (const data of sampleData) {
      const result = await ShipArrivalContract.methods.recordShipArrival(
        data.numeroAvis,
        data.numeroEscale,
        data.nomPort,
        data.codePort,
        data.nomNavire,
        data.etat
      ).send({ from: ACCOUNT_ADDRESS, gas: 5000000 });
      
      console.log(`Recorded ship arrival for ${data.nomNavire} (${data.numeroAvis})`);
      console.log(`Transaction hash: ${result.transactionHash}`);
    }
    
    // Step 7: Update a ship arrival
    console.log('\nUpdating ship arrival...');
    const updateResult = await ShipArrivalContract.methods.updateShipArrival(
      sampleData[0].numeroAvis,
      updateData.numeroEscale,
      updateData.nomPort,
      updateData.codePort,
      updateData.nomNavire,
      updateData.etat
    ).send({ from: ACCOUNT_ADDRESS, gas: 5000000 });
    
    console.log(`Updated ship arrival for ${updateData.nomNavire} (${sampleData[0].numeroAvis})`);
    console.log(`Transaction hash: ${updateResult.transactionHash}`);
    
    // Step 8: Get ship arrival details
    console.log('\nGetting ship arrival details...');
    const arrival = await ShipArrivalContract.methods.getShipArrival(sampleData[0].numeroAvis).call();
    console.log('Ship arrival details:');
    console.log(`  Numero Avis: ${arrival[0]}`);
    console.log(`  Numero Escale: ${arrival[1]}`);
    console.log(`  Nom Port: ${arrival[2]}`);
    console.log(`  Code Port: ${arrival[3]}`);
    console.log(`  Nom Navire: ${arrival[4]}`);
    console.log(`  Etat: ${arrival[5]}`);
    console.log(`  Timestamp: ${new Date(arrival[6] * 1000).toISOString()}`);
    
    // Step 9: Check if ship arrival exists
    console.log('\nChecking if ship arrival exists...');
    const exists = await ShipArrivalContract.methods.shipArrivalExists(sampleData[0].numeroAvis).call();
    console.log(`Ship arrival exists: ${exists}`);
    
    // Step 10: Get ship events
    console.log('\nGetting ship events...');
    const shipEvents = await ShipArrivalContract.methods.getShipEvents(sampleData[0].numeroEscale).call();
    console.log(`Number of ship events: ${shipEvents.length}`);
    
    // Step 11: Get port events
    console.log('\nGetting port events...');
    const portEvents = await ShipArrivalContract.methods.getPortEvents(sampleData[0].codePort).call();
    console.log(`Number of port events: ${portEvents.length}`);
    
    console.log('\n✅ All tests completed successfully!');
  } catch (error) {
    console.error('\n❌ Error:', error);
    process.exit(1);
  }
}

main(); 