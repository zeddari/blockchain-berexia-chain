const Web3 = require('web3');
const fs = require('fs');
const path = require('path');

// Connect to the blockchain
const rpcUrl = 'http://157.173.119.195:8545';
const web3 = new Web3(new Web3.providers.HttpProvider(rpcUrl));

// File to store account information
const ACCOUNT_FILE = path.join(__dirname, 'account.json');

// Genesis account details (this is typically the first account in a Quorum network)
const GENESIS_ACCOUNT = {
    address: '0xf39Fd6e51aad88F6F4ce6aB8827279cffFb92266',
    privateKey: '0xac0974bec39a17e36ba4a6b4d238ff944bacb478cbed5efcae784d7bf4f2ff80'
};

async function createAndSaveAccount() {
    try {
        console.log('Creating new account...');
        
        // Create a new account
        const account = web3.eth.accounts.create();
        
        // Save account details to file
        const accountData = {
            address: account.address,
            privateKey: account.privateKey,
            createdAt: new Date().toISOString()
        };
        
        fs.writeFileSync(ACCOUNT_FILE, JSON.stringify(accountData, null, 2));
        
        console.log('Account created successfully!');
        console.log(`Address: ${account.address}`);
        console.log(`Private Key: ${account.privateKey}`);
        console.log(`Account details saved to: ${ACCOUNT_FILE}`);
        
        return account;
    } catch (error) {
        console.error('Error creating account:', error);
        throw error;
    }
}

async function loadAccount() {
    try {
        if (!fs.existsSync(ACCOUNT_FILE)) {
            console.log('No existing account found. Creating new account...');
            return await createAndSaveAccount();
        }
        
        const accountData = JSON.parse(fs.readFileSync(ACCOUNT_FILE, 'utf8'));
        const account = web3.eth.accounts.privateKeyToAccount(accountData.privateKey);
        
        console.log('Loaded existing account:');
        console.log(`Address: ${account.address}`);
        
        return account;
    } catch (error) {
        console.error('Error loading account:', error);
        throw error;
    }
}

async function fundAccount() {
    try {
        console.log('Connecting to blockchain...');
        
        // Get network ID
        const networkId = await web3.eth.net.getId();
        console.log(`Connected to network ID: ${networkId}`);
        
        // Load or create account
        const account = await loadAccount();
        
        // Add genesis account to web3 wallet
        const genesisAccount = web3.eth.accounts.privateKeyToAccount(GENESIS_ACCOUNT.privateKey);
        web3.eth.accounts.wallet.add(genesisAccount);
        
        // Get account balance
        const balance = await web3.eth.getBalance(account.address);
        console.log(`Account balance: ${web3.utils.fromWei(balance, 'ether')} ETH`);
        
        // Get genesis account balance
        const genesisBalance = await web3.eth.getBalance(GENESIS_ACCOUNT.address);
        console.log(`Genesis account balance: ${web3.utils.fromWei(genesisBalance, 'ether')} ETH`);
        
        // Request funds from genesis account
        console.log('Requesting funds...');
        
        // Get the nonce for the genesis account
        const nonce = await web3.eth.getTransactionCount(GENESIS_ACCOUNT.address);
        
        // Create transaction object
        const tx = {
            from: GENESIS_ACCOUNT.address,
            to: account.address,
            value: web3.utils.toWei('1', 'ether'), // Reduced amount
            gas: 21000,
            gasPrice: web3.utils.toWei('1', 'gwei'), // Reduced gas price
            nonce: nonce
        };
        
        // Sign and send transaction
        const signedTx = await web3.eth.accounts.signTransaction(tx, GENESIS_ACCOUNT.privateKey);
        const receipt = await web3.eth.sendSignedTransaction(signedTx.rawTransaction);
        
        console.log(`Transaction hash: ${receipt.transactionHash}`);
        
        // Check new balance
        const newBalance = await web3.eth.getBalance(account.address);
        console.log(`New account balance: ${web3.utils.fromWei(newBalance, 'ether')} ETH`);
        
        console.log('Account funded successfully!');
        
    } catch (error) {
        console.error('Error funding account:', error);
        if (error.message.includes('insufficient funds')) {
            console.log('\nNote: The genesis account might not have enough funds.');
            console.log('Please contact your network administrator to get funds.');
        } else if (error.message.includes('nonce too low')) {
            console.log('\nNote: Transaction nonce issue detected.');
            console.log('Please wait a few seconds and try again.');
        }
    }
}

// Run the funding process
fundAccount(); 