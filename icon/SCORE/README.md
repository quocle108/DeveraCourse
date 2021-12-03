# Devera sample SCORE

This repository contains SCORE (Smart Contract for ICON) examples written in Java.

## IRC2 token SCORE

### build

```
./gradlew optimizedJar
```

If buiding success, `IRC2BurnableToken-0.1.0-optimized.jar` generated in `icon/SCORE/irc2/build/libs/`, this file is used to deploy contract to ICON network.

### deploy to local network

Initialize parameters:

- _name: name of token
- _symbol: symbol of token, use when transfer token from one to another
- _decimals: number of decimals point
- _initialSupply: amount of initial supply of token, belong to account that deployed contract (contract owner)

```bash
$ goloop rpc --uri http://127.0.0.1:9082/api/v3 sendtx deploy ./irc2/build/libs/IRC2Burnable-0.1.0-optimized.jar \
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
>>> optimizedJar = /Users/mac/Books/BlockChainLecle/Buoi4-Deploy smartcontract/DeveraCourse/icon/SCORE/irc2/build/libs/IRC2BurnableToken-0.1.0-optimized.jar
>>> keystore = ./godWallet.json
Succeeded to deploy: 0xc85113eb36a2ab92f1e49b54335cc45999b5329ae88fd6e9222e19587157e101
SCORE address: cxc1594d72c121e2e7fa2bf04d20fc4f9c5a62512a

BUILD SUCCESSFUL in 5s
1 actionable task: 1 executed

========================================================================================
$ ./gradlew sampleContract:deployToLocal -PkeystoreName=./godWallet.json -PkeystorePass=gochain

> Task :sampleContract:deployToLocal
>>> deploy to http://localhost:9082/api/v3
>>> optimizedJar = /Users/mac/Books/BlockChainLecle/Buoi4-Deploy smartcontract/DeveraCourse/icon/SCORE/sampleContract/build/libs/SampleContract-0.1.0-optimized.jar
>>> keystore = ./godWallet.json
Succeeded to deploy: 0x4597d8a4df01418c866a544bd66383183699341ccd17e1592a5be4ffb22bfd34
SCORE address: cx27a8852f2d217ff5531868da57b35d2e938a80ea

BUILD SUCCESSFUL in 3s
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
Token contract: cxc1594d72c121e2e7fa2bf04d20fc4f9c5a62512a
Sample contract: cx27a8852f2d217ff5531868da57b35d2e938a80ea

goloop rpc --uri http://127.0.0.1:9082/api/v3 sendtx call --to cxc1594d72c121e2e7fa2bf04d20fc4f9c5a62512a \
    --method transfer \
    --key_store ./godWallet.json --key_password gochain \
    --param _to=cx27a8852f2d217ff5531868da57b35d2e938a80ea \
    --param _value=1000000000000000000 \
    --nid 0x3 --step_limit 20000000000000
0xf11b8464578f763c5fa971da343db488f3855776bf8ef267fcc420476edd5336

goloop rpc --uri http://127.0.0.1:9082/api/v3 txresult  0xf11b8464578f763c5fa971da343db488f3855776bf8ef267fcc420476edd5336
{
  "to": "cxc1594d72c121e2e7fa2bf04d20fc4f9c5a62512a",
  "cumulativeStepUsed": "0x325d6",
  "stepUsed": "0x325d6",
  "stepPrice": "0x2e90edd00",
  "eventLogs": [
    {
      "scoreAddress": "cxc1594d72c121e2e7fa2bf04d20fc4f9c5a62512a",
      "indexed": [
        "Transfer(Address,Address,int,bytes)",
        "hxb6b5791be0b5ef67063b3c10b840fb81514db2fd",
        "cx27a8852f2d217ff5531868da57b35d2e938a80ea",
        "0xde0b6b3a7640000"
      ],
      "data": [
        "0x"
      ]
    }
  ],
  "logsBloom": "0x00000000000000000000002000000000000000000000000000000000000000020000000000000000000000000000004000000000000000000000000000000020000000000000000000000000000000020000000000000000000000000080000008000000000000000000000000000000000000000000000020000000000000000000000000000000000000000000000000000000000000000000000000100000002000000000000000100000000000000000000000000000000000000000000000000000000000000000000000000010000000000001000000000000000000000000000000000010000000000000000000000000000000040000000000000000",
  "status": "0x1",
  "blockHash": "0x1b01c087a11d1343090dec0b3769fe5fffcaae0eaab13636e04ee3856cc1cb7b",
  "blockHeight": "0x4b2",
  "txIndex": "0x0",
  "txHash": "0xf11b8464578f763c5fa971da343db488f3855776bf8ef267fcc420476edd5336"
}
```

Check token balance of sender and receiver
```bash
$ goloop rpc --uri http://127.0.0.1:9082/api/v3 call --to cxc1594d72c121e2e7fa2bf04d20fc4f9c5a62512a --method balanceOf --param _owner=hxb6b5791be0b5ef67063b3c10b840fb81514db2fd
0x47bf19673df52e37f240ff33c594c589c0000

$ goloop rpc --uri http://127.0.0.1:9082/api/v3 call --to cx27a8852f2d217ff5531868da57b35d2e938a80ea --method balanceOf --param _owner=hxb6b5791be0b5ef67063b3c10b840fb81514db2fd
0xde0b6b3a7640000
```

### Deploy token to testnet

Available testnet: https://www.icondev.io/introduction/the-icon-network/testnet

1. Choose one network
2. Get ICX from testnet faucet
3. Deploy token contrat to testnet
4. Deploy and transfer token to address: `hxc00a6d2d1e9ee0686704e0b6eec75d0f2c095b39`
5. Comment network name, token address, block number deploy token, token name, symbol transaction id that transfer token of step 5






