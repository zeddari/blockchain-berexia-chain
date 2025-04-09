/**
 * Truffle configuration file for Berexia Chain smart contracts.
 */

require('dotenv').config({ path: '.env' });
const HDWalletProvider = require('@truffle/hdwallet-provider');

// Ensure private key is available
let privateKey = process.env.BLOCKCHAIN_PRIVATE_KEY || '';
// Remove '0x' prefix if present
if (privateKey.startsWith('0x')) {
  privateKey = privateKey.substring(2);
}
privateKey = privateKey.trim();
if (privateKey.length !== 64) {
  throw new Error('Invalid private key: expected 64 hex characters (32 bytes).');
}

const rpcUrl = process.env.BLOCKCHAIN_RPC_URL || 'http://157.173.119.195:8545';

module.exports = {
  /**
   * Networks define how you connect to your ethereum client.
   */
  networks: {
    // Development network - used during development
    development: {
      host: "157.173.119.195",     // Localhost
      port: 8545,            // Standard Ethereum port
      network_id: "*",       // Any network
    },
    
    // Berexia Chain - the production network
    berexia: {
      provider: () => new HDWalletProvider({
        privateKeys: [privateKey],
        providerOrUrl: rpcUrl
      }),
      network_id: "*",       // Match any network id
      gas: 5500000,          // Gas limit
      gasPrice: 10000000000, // 10 Gwei
      confirmations: 2,      // Number of confirmations to wait between deployments
      timeoutBlocks: 200,    // Number of blocks before a deployment times out
      skipDryRun: true       // Skip dry run before migrations?
    },
  },

  // Configure your compilers
  compilers: {
    solc: {
      version: "0.8.17",     // Fetch exact version from solc-bin
      settings: {
        optimizer: {
          enabled: true,
          runs: 200
        },
      }
    }
  },
  
  // Directories to store artifacts and contracts
  contracts_directory: "./contracts",
  contracts_build_directory: "./build/contracts",
  migrations_directory: "./migrations",
  
  // Plugins
  plugins: [
    'truffle-plugin-verify'
  ],
  
  // Configure paths
  api_keys: {
    etherscan: process.env.ETHERSCAN_API_KEY // For contract verification
  }
}; 