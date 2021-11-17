const SHA256 = require('crypto-js/sha256');

class SimpleBlock{
    constructor(index, timestamp, transactions, previousHash=''){
        this.index = index;
        this.timestamp = timestamp;
        this.previousHash = previousHash;
        this.transactions = transactions;
        this.hash = this.hashBlock();
    }

    hashBlock(){
        return SHA256(this.index + this.timestamp + this.previousHash + this.transactions).toString();
    }
};


class Blockchain{
    constructor(){
        this.blockchain = [this.startGenesisBlock()];
    }

    startGenesisBlock(){
        return new SimpleBlock(0, new Date(), [], '');
    }

    getLastBlock(){
        return this.blockchain[this.blockchain.length -1];
    }

    addNewBlock(newBlock){
        newBlock.previousHash = this.getLastBlock().hash
        newBlock.hash = newBlock.hashBlock();
        this.blockchain.push(newBlock);
    }
}



let myChain = new Blockchain();
myChain.addNewBlock(new SimpleBlock(1, new Date(), ["transaction1", "transaction2"]));
myChain.addNewBlock(new SimpleBlock(2, new Date(), ["transaction5", "transaction6", "transaction7"]));
myChain.addNewBlock(new SimpleBlock(3, new Date(), ["transaction8", "transaction9", "transaction10"]));
console.log(JSON.stringify(myChain, null, 4));
