// This script deploys the contracts to the Berexia Chain blockchain
// Usage: node deploy.js <private_key>

const Web3 = require('web3');
const fs = require('fs');
const path = require('path');

// Load compiled contract artifacts
const PortnetContractFactory = require('../build/contracts/PortnetContractFactory.json');

// Connect to the blockchain
const rpcUrl = process.env.BLOCKCHAIN_RPC_URL || 'http://157.173.119.195:8545';
const web3 = new Web3(new Web3.providers.HttpProvider(rpcUrl));

// Get private key from command line arguments or environment variable
const privateKey = process.argv[2] || process.env.BLOCKCHAIN_PRIVATE_KEY;

if (!privateKey) {
  console.error('Please provide a private key as argument or set BLOCKCHAIN_PRIVATE_KEY environment variable');
  process.exit(1);
}

async function deployContracts() {
  try {
    console.log(`Connecting to blockchain at ${rpcUrl}...`);
    
    // Get network ID
    const networkId = await web3.eth.net.getId();
    console.log(`Connected to network ID: ${networkId}`);
    
    // Setup account from private key
    const account = web3.eth.accounts.privateKeyToAccount('0x' + privateKey.replace(/^0x/, ''));
    web3.eth.accounts.wallet.add(account);
    const deployerAddress = account.address;
    
    console.log(`Deploying contracts from address: ${deployerAddress}`);
    
    // Check balance
    const balance = await web3.eth.getBalance(deployerAddress);
    console.log(`Account balance: ${web3.utils.fromWei(balance, 'ether')} ETH`);
    
    if (web3.utils.toBN(balance).isZero()) {
      console.error('Account has no balance. Please fund the account before deploying contracts.');
      process.exit(1);
    }
    
    // Deploy PortnetContractFactory
    console.log('Deploying PortnetContractFactory...');
    const factoryContract = new web3.eth.Contract(PortnetContractFactory.abi);
    
    const factory = await factoryContract.deploy({
      data: PortnetContractFactory.bytecode
    }).send({
      from: deployerAddress,
      gas: 6000000,
      gasPrice: web3.utils.toWei('10', 'gwei')
    });
    
    console.log(`PortnetContractFactory deployed at: ${factory.options.address}`);
    
    // Deploy ShipArrival and DAP contracts using the factory
    console.log('Deploying all contracts via factory...');
    const deployTx = await factory.methods.deployAllContracts().send({
      from: deployerAddress,
      gas: 10000000,
      gasPrice: web3.utils.toWei('10', 'gwei')
    });
    
    console.log('All contracts deployed successfully!');
    
    // Get deployed contract addresses
    const shipArrivalAddress = await factory.methods.shipArrivalContract().call();
    const dapAddress = await factory.methods.dapContract().call();
    
    console.log(`Ship Arrival Contract: ${shipArrivalAddress}`);
    console.log(`DAP Contract: ${dapAddress}`);
    
    // Update .env file
    const envFile = path.resolve(__dirname, '../../../.env');
    let envContent = '';
    
    if (fs.existsSync(envFile)) {
      envContent = fs.readFileSync(envFile, 'utf8');
    }
    
    // Replace contract addresses
    envContent = envContent.replace(/SHIP_ARRIVAL_CONTRACT_ADDRESS=.*/, `SHIP_ARRIVAL_CONTRACT_ADDRESS=${shipArrivalAddress}`);
    envContent = envContent.replace(/DAP_CONTRACT_ADDRESS=.*/, `DAP_CONTRACT_ADDRESS=${dapAddress}`);
    
    fs.writeFileSync(envFile, envContent);
    console.log('.env file updated with contract addresses');
    
    // Create deployment info JSON
    const deploymentInfo = {
      networkId,
      deploymentDate: new Date().toISOString(),
      deployerAddress,
      contracts: {
        factory: factory.options.address,
        shipArrival: shipArrivalAddress,
        dap: dapAddress
      },
      transactionHash: deployTx.transactionHash
    };
    
    fs.writeFileSync(
      path.resolve(__dirname, '../deployment-info.json'),
      JSON.stringify(deploymentInfo, null, 2)
    );
    
    console.log('Deployment info saved to deployment-info.json');
    console.log('Deployment completed successfully!');
    
  } catch (error) {
    console.error('Deployment failed:', error);
    process.exit(1);
  }
}

deployContracts(); 