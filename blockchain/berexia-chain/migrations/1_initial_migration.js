const PortnetContractFactory = artifacts.require("PortnetContractFactory");

module.exports = async function (deployer, network, accounts) {
  // Deploy the factory
  await deployer.deploy(PortnetContractFactory);
  const factory = await PortnetContractFactory.deployed();
  
  console.log(`PortnetContractFactory deployed at: ${factory.address}`);
  
  // Deploy all contracts using the factory
  console.log("Deploying ShipArrival and DAP contracts...");
  await factory.deployAllContracts();
  
  // Get deployed contract addresses
  const shipArrivalAddress = await factory.shipArrivalContract();
  const dapAddress = await factory.dapContract();
  
  console.log(`Ship Arrival Contract deployed at: ${shipArrivalAddress}`);
  console.log(`DAP Contract deployed at: ${dapAddress}`);
  
  // Write deployment info to file
  const fs = require('fs');
  const path = require('path');
  
  const deploymentInfo = {
    network,
    deploymentDate: new Date().toISOString(),
    deployer: accounts[0],
    contracts: {
      factory: factory.address,
      shipArrival: shipArrivalAddress,
      dap: dapAddress
    }
  };
  
  fs.writeFileSync(
    path.join(__dirname, '../deployment-info.json'),
    JSON.stringify(deploymentInfo, null, 2)
  );
  
  console.log("Deployment info written to deployment-info.json");
  
  // Update .env file if it exists
  const envFilePath = path.join(__dirname, '../../../.env');
  if (fs.existsSync(envFilePath)) {
    let envContent = fs.readFileSync(envFilePath, 'utf8');
    
    // Replace contract addresses
    envContent = envContent.replace(/SHIP_ARRIVAL_CONTRACT_ADDRESS=.*/, `SHIP_ARRIVAL_CONTRACT_ADDRESS=${shipArrivalAddress}`);
    envContent = envContent.replace(/DAP_CONTRACT_ADDRESS=.*/, `DAP_CONTRACT_ADDRESS=${dapAddress}`);
    
    fs.writeFileSync(envFilePath, envContent);
    console.log(".env file updated with contract addresses");
  }
}; 