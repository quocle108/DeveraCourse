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
    --param _value=1000000000000000000
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






