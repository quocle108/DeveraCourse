# Devera sample SCORE

This repository contains SCORE (Smart Contract for ICON) examples written in Java.

## IRC2 token SCORE

### build

```
./gradlew optimizedJar
```

If buiding success, `IRC2Burnable-0.1.0-optimized.jar` generated in `icon/SCORE/irc2/build/libs/`, this file is used to deploy contract to ICON network.

### deploy to local network

Initialize parameters:

- _name: name of token
- _symbol: symbol of token, use when transfer token from one to another
- _decimals: number of decimals point
- _initialSupply: amount of initial supply of token, belong to account that deployed contract (contract owner)

```bash
$ goloop rpc --uri http://127.0.0.1:9082/api/v3 sendtx deploy ./irc2/build/libs/IRC2BurnableToken-0.1.0-optimized.jar \
    --key_store ./godWallet.json --key_password gochain \
    --nid 0x3 --step_limit 2000000000 --content_type application/java \
    --param _name="QUY VO TOKEN" \
    --param _symbol=QUY \
    --param _decimals=18 \
    --param _initialSupply=1000000000000000000000000
0x50f3c6fbfe472796efbbeb55700e62f4765ac1ca4fa190a5dac1acf43fc28c1b

$ goloop rpc --uri http://127.0.0.1:9082/api/v3 txresult 0x50f3c6fbfe472796efbbeb55700e62f4765ac1ca4fa190a5dac1acf43fc28c1b
{
  "to": "cx0000000000000000000000000000000000000000",
  "cumulativeStepUsed": "0x40dd8e6c",
  "stepUsed": "0x40dd8e6c",
  "stepPrice": "0x2e90edd00",
  "eventLogs": [
    {
      "scoreAddress": "cx5a924818d89bd73864c6d2c30abcbb1eddfd4758",
      "indexed": [
        "Transfer(Address,Address,int,bytes)",
        "hx0000000000000000000000000000000000000000",
        "hxb6b5791be0b5ef67063b3c10b840fb81514db2fd",
        "0xb7abc627050305adf14a3d9e40000000000"
      ],
      "data": [
        "0x6d696e74"
      ]
    }
  ],
  "logsBloom": "0x00000000000000000200002000000000008000000000000000020000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000002000000000100000001000000000410000000000000000000000000000000000000000000000000000008000000000000000000100000000000004000000000000000000000000100000000000000000000000000000000000000000000000000000000000000000000000001000080000000000000000000000000000000000000000000000000000000000000000000000000000000",
  "status": "0x1",
  "scoreAddress": "cx5a924818d89bd73864c6d2c30abcbb1eddfd4758",
  "blockHash": "0xeb998749f394ce2f1fad65efcb0b88f96b3260878454cc843d670784c82e36dd",
  "blockHeight": "0x1eee",
  "txIndex": "0x0",
  "txHash": "0xebcbfafb098bc13e61faa8531e53e15d962ac32b0ba3611a447b54df6883ea6c"
}
```

Use gradle javaee plugin to deploy: 

```
$ ./gradlew irc2:deployToLocal -PkeystoreName=./godWallet.json -PkeystorePass=gochain

> Task :irc2:deployToLocal
>>> deploy to http://localhost:9082/api/v3
>>> optimizedJar = /Users/leclevietnam/mwork/DeveraCourse/icon/SCORE/irc2/build/libs/IRC2BurnableToken-0.1.0-optimized.jar
>>> keystore = ./godWallet.json
Succeeded to deploy: 0x08ad84fe012811ce9cc574d35edee6ab05ff9cf7187f6db14e51062a233e8df7
SCORE address: cxfdbc7aca1f4ba6d37d4f80eb112d0e62f0727280

BUILD SUCCESSFUL in 4s
1 actionable task: 1 executed
```

Check token balance of contract owner: 

Parameters: 

- _owner: address of want to check balance

```bash
$ goloop rpc --uri http://127.0.0.1:9082/api/v3 call --to cx5a924818d89bd73864c6d2c30abcbb1eddfd4758 \
    --method balanceOf \
    --param _owner=hxb6b5791be0b5ef67063b3c10b840fb81514db2fd
"0xb7abc627050305adf14a3d9e40000000000"
```

### Transfer token from owner to others

Parameters: 

- _to: address of receiver
- _value: amount of token
- _data: data to pass if receiver is contract

```bash
$ goloop rpc --uri http://127.0.0.1:9082/api/v3 sendtx call --to cx5a924818d89bd73864c6d2c30abcbb1eddfd4758 \
    --method transfer \
    --key_store ./godWallet.json --key_password gochain \
    --nid 0x3 --step_limit 2000000000 \
    --param _to=hxbb78dbaf1c3ed187e956abcfdf43eb1110077dd4 \
    --param _value=10000000000000000000
0xf491225b62717688eecb23eebf09042f1529a9a4d922fec00a99dea9b9e8c835

$ goloop rpc --uri http://127.0.0.1:9082/api/v3 txresult 0xf491225b62717688eecb23eebf09042f1529a9a4d922fec00a99dea9b9e8c835
{
  "to": "cx5a924818d89bd73864c6d2c30abcbb1eddfd4758",
  "cumulativeStepUsed": "0x2abf9",
  "stepUsed": "0x2abf9",
  "stepPrice": "0x2e90edd00",
  "eventLogs": [
    {
      "scoreAddress": "cx5a924818d89bd73864c6d2c30abcbb1eddfd4758",
      "indexed": [
        "Transfer(Address,Address,int,bytes)",
        "hxb6b5791be0b5ef67063b3c10b840fb81514db2fd",
        "hxbb78dbaf1c3ed187e956abcfdf43eb1110077dd4",
        "0xde0b6b3a7640000"
      ],
      "data": [
        "0x"
      ]
    }
  ],
  "logsBloom": "0x00000000000000000000002000000000000000000000000000020000000000000000000000000000010000000000004000000000000000000000000000000020000000000000000000000000000000000000000000000000000000000000000008000000000000000000000000000000000000000000000030000000000000000000000000000000000000000000000000000000000000000000000000102000002000000000000000100000000000000000000000000000000000000000000100000000000000000000000000000000000000000001000080000000000000000000000000000000000000000000000000000000000000000000000000000000",
  "status": "0x1",
  "blockHash": "0x4eac52463cecb8273ecafd15b337f2ad6c8dcdf718e80a1fb437d6c794b88ecf",
  "blockHeight": "0x1f11",
  "txIndex": "0x0",
  "txHash": "0xf491225b62717688eecb23eebf09042f1529a9a4d922fec00a99dea9b9e8c835"
}
```

Check token balance of sender and receiver

```
$ goloop rpc --uri http://127.0.0.1:9082/api/v3 call --to cx5a924818d89bd73864c6d2c30abcbb1eddfd4758 \
    --method balanceOf \
    --param _owner=hxb6b5791be0b5ef67063b3c10b840fb81514db2fd
"0xb7abc627050305adf1495f92d4c589c0000"

$ goloop rpc --uri http://127.0.0.1:9082/api/v3 call --to cx5a924818d89bd73864c6d2c30abcbb1eddfd4758 \
    --method balanceOf \
    --param _owner=hxbb78dbaf1c3ed187e956abcfdf43eb1110077dd4
"0xde0b6b3a7640000"
```

### Deploy token to testnet

Available testnet: https://www.icondev.io/introduction/the-icon-network/testnet

1. Choose one network
2. Get ICX from testnet faucet
3. Deploy token contrat to testnet
4. Deploy and transfer token to address: `hxc00a6d2d1e9ee0686704e0b6eec75d0f2c095b39`
5. Comment network name, token address, block number deploy token, token name, symbol transaction id that transfer token of step 5

## Crowdsale token SCORE

### deploy to local network


Initialize parameters:

- _fundingGoalInIcx: funding goal in ICX
- _tokenScore: IRC2 token score address
- _durationInBlocks: crowdsale duration
- _tokenPrice: IRC2/ICX ratio


```bash
$ goloop rpc --uri http://127.0.0.1:9082/api/v3 sendtx deploy ./crowdsale/build/libs/Crowdsale-0.1.0-optimized.jar \
    --key_store ./godWallet.json --key_password gochain \
    --nid 0x3 --step_limit 2000000000 --content_type application/java \
    --param _fundingGoalInIcx=10 \
    --param _tokenScore=cx79158ef89bda22e927bd04e751473610ca1bc4ea \
    --param _durationInBlocks=3600 \
    --param _tokenPrice=10
0xd33c29366d1aaf13e01e1252523f6b30508fbbbfcd361358784461c6650c4bc3

$ goloop rpc --uri http://127.0.0.1:9082/api/v3 txresult 0xd33c29366d1aaf13e01e1252523f6b30508fbbbfcd361358784461c6650c4bc3
{
  "to": "cx0000000000000000000000000000000000000000",
  "cumulativeStepUsed": "0x40481544",
  "stepUsed": "0x40481544",
  "stepPrice": "0x2e90edd00",
  "eventLogs": [],
  "logsBloom": "0x00000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000",
  "status": "0x1",
  "scoreAddress": "cx8d889b2a3b48985dddafee6799b5cd7e7936dc53",
  "blockHash": "0x71eeb3b628d5663d596ace26e9d4c87cbea19c661c70e44a9a2bb95b175cd1c5",
  "blockHeight": "0x1e095",
  "txIndex": "0x0",
  "txHash": "0xd33c29366d1aaf13e01e1252523f6b30508fbbbfcd361358784461c6650c4bc3"
}
```

Use gradle javaee plugin to deploy:

```
$ ./gradlew crowdsale:deployToLocal -PkeystoreName=./godWallet.json -PkeystorePass=gochain
Starting a Gradle Daemon, 1 incompatible Daemon could not be reused, use --status for details

> Task :crowdsale:deployToLocal
>>> deploy to http://localhost:9082/api/v3
>>> optimizedJar = /Users/leclevietnam/mwork/DeveraCourse/icon/SCORE/crowdsale/build/libs/Crowdsale-0.1.0-optimized.jar
>>> keystore = ./godWallet.json
Succeeded to deploy: 0x960bbf574554f08a332ef6563273af4c30e0fbae6dea11ee822b3aeb1936eda2
SCORE address: cx6749c6a328d0b33e66dd4ffcce8982c3850be21a

BUILD SUCCESSFUL in 15s
1 actionable task: 1 executed
```

Get name and description of crowsale project:

```bash
$ goloop rpc --uri http://127.0.0.1:9082/api/v3 call --to cx0a9bdb97e2d86cc51f56c933f95c8aea576f4a5c \
    --method name
"Sample Crowdsale"
$ goloop rpc --uri http://127.0.0.1:9082/api/v3 call --to cx0a9bdb97e2d86cc51f56c933f95c8aea576f4a5c \
    --method description
"Devera ICON dapp development course"
```

### Transfer IRC2 token to crowdsale score to active crowdsale

```
$ goloop rpc --uri http://127.0.0.1:9082/api/v3 sendtx call --to cxd2e2cfe28562ed17bf3446f9368a5cdccba1ea76 --method transfer \
  --param _to=cx0a9bdb97e2d86cc51f56c933f95c8aea576f4a5c \
  --param _value=1000000000000000000000000 \
  --key_store ./godWallet.json --key_password gochain \
  --nid 0x3 --step_limit 2000000000
```

### User deposit token to crowdsale

Transfer token to crowdsale contract

```bash
$ goloop rpc --uri http://127.0.0.1:9082/api/v3 sendtx transfer --to cx0a9bdb97e2d86cc51f56c933f95c8aea576f4a5c --value 4000000000000000000 --key_store ./daniel.json --key_password abc123456 --nid 0x3 --step_limit 1000000000
```

### Check result

Check IRC2 token after transfer token

```bash
$ goloop rpc --uri http://127.0.0.1:9082/api/v3 call --to cxd2e2cfe28562ed17bf3446f9368a5cdccba1ea76 --method balanceOf --param _owner=hxc00a6d2d1e9ee0686704e0b6eec75d0f2c095b39
```

Check ICX balance record in crowdsale contract

```bash
$ goloop rpc --uri http://127.0.0.1:9082/api/v3 call --to cxc624a8a090f3cd00d2a9b87bf2f5d52fae6c07ec --method balanceOf --param _owner=hxc00a6d2d1e9ee0686704e0b6eec75d0f2c095b39
```

### Check ICX balance before withdraw

```
$ goloop rpc --uri http://127.0.0.1:9082/api/v3 balance hxb6b5791be0b5ef67063b3c10b840fb81514db2fd
```


### Withdraw balance

```bash
$ goloop rpc --uri http://127.0.0.1:9082/api/v3 sendtx call --to cx0a9bdb97e2d86cc51f56c933f95c8aea576f4a5c --method withdraw \
  --param _value=1000000000000000000 \
  --key_store ./godWallet.json --key_password gochain \
  --nid 0x3 --step_limit 2000000000
```

### Check ICX balance after withdraw

```
$ goloop rpc --uri http://127.0.0.1:9082/api/v3 balance hxb6b5791be0b5ef67063b3c10b840fb81514db2fd
```