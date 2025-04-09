const Web3 = require('web3');
const fs = require('fs');
const path = require('path');

// Connect to the blockchain
const rpcUrl = 'http://157.173.119.195:8545';
const web3 = new Web3(new Web3.providers.HttpProvider(rpcUrl));

// File to store account information
const ACCOUNT_FILE = path.join(__dirname, 'account.json');

async function listAccounts() {
    try {
        console.log('Connecting to blockchain...');
        
        // Get network ID
        const networkId = await web3.eth.net.getId();
        console.log(`Connected to network ID: ${networkId}`);
        
        // Get all accounts from the node
        const accounts = await web3.eth.getAccounts();
        console.log('\nAccounts in the blockchain:');
        accounts.forEach((account, index) => {
            console.log(`${index + 1}. ${account}`);
        });
        
        // Check if we have a saved account
        if (fs.existsSync(ACCOUNT_FILE)) {
            const savedAccount = JSON.parse(fs.readFileSync(ACCOUNT_FILE, 'utf8'));
            console.log('\nYour saved account:');
            console.log(`Address: ${savedAccount.address}`);
            console.log(`Created at: ${savedAccount.createdAt}`);
            
            // Get balance for saved account
            const balance = await web3.eth.getBalance(savedAccount.address);
            console.log(`Balance: ${web3.utils.fromWei(balance, 'ether')} ETH`);
        } else {
            console.log('\nNo saved account found.');
        }
        
    } catch (error) {
        console.error('Error listing accounts:', error);
    }
}

// Run the listing process
listAccounts(); 