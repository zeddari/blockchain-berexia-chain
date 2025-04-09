#!/usr/bin/env node

/**
 * Script to test the ShipArrivalContract on the specified network
 * 
 * Usage:
 *   node scripts/test-ship-arrival.js
 */

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

console.log('🚀 Testing ShipArrivalContract on network:', NETWORK_URL);
console.log('===========================================');
console.log(`Using account: ${ACCOUNT_ADDRESS}`);

try {
  // Create a temporary .env file with the private key from account.json
  const envContent = `BLOCKCHAIN_PRIVATE_KEY=${PRIVATE_KEY}\nBLOCKCHAIN_RPC_URL=${NETWORK_URL}`;
  fs.writeFileSync(path.join(projectDir, '.env'), envContent);
  
  // Run the Truffle test command with the specified network
  console.log('Running tests...');
  execSync(`npx truffle test test/ShipArrivalContract.test.js --network ${NETWORK_NAME}`, { stdio: 'inherit' });
  
  console.log('\n✅ Tests completed successfully!');
} catch (error) {
  console.error('\n❌ Tests failed!');
  process.exit(1);
} 