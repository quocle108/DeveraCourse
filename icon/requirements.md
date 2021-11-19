# ICON DAPP development course requirements

## Docker

Guide how to install [here](https://docs.docker.com/get-docker/)

## OpenJDK 11

### Mac OS

```bash
$ brew tap AdoptOpenJDK/openjdk$ brew install --cask adoptopenjdk11
$ brew install --cask adoptopenjdk11
```

### Linux

```bash
$ sudo apt install openjdk-11-jdk
```

### Windows

1. Download JDK 11 for Window [here](https://jdk.java.net/java-se-ri/11)

2. Extract the zip file into a folder, e.g. C:\Program Files\Java\ and it will create a jdk-11 folder (where the bin folder is a direct sub-folder). You may need Administrator privileges to extract the zip file to this location.

3. Set a PATH:

Select Control Panel and then System.
Click Advanced and then Environment Variables.
Add the location of the bin folder of the JDK installation to the PATH variable in System Variables.
The following is a typical value for the PATH variable: C:\WINDOWS\system32;C:\WINDOWS;"C:\Program Files\Java\jdk-11\bin"

4. Set JAVA_HOME:

Under System Variables, click New.
Enter the variable name as JAVA_HOME.
Enter the variable value as the installation path of the JDK (without the bin sub-folder).
Click OK.
Click Apply Changes.

5. Configure the JDK in your IDE (e.g. IntelliJ or Eclipse).

To see if it worked, open up the Command Prompt and type java -version and see if it prints your newly installed JDK.

Reference: https://stackoverflow.com/questions/52511778/how-to-install-openjdk-11-on-windows

## golang

### Mac OS

```bash
$ brew install go
```

### Linux

```bash
$sudo wget -c https://dl.google.com/go/go1.14.2.linux-amd64.tar.gz -O - | sudo tar -xz -C /usr/local
$export PATH=$PATH:/usr/local/go/bin
```

### Windows

Guide how to install [here](https://golang.org/doc/install)


## python

### Mac OS

```bash
$ brew install python3
$ pip3 install virtualenv setuptools wheel
```

### Linux

```bash
$ sudo apt install python3
$ pip3 install virtualenv setuptools wheel
```

### Windows

Guide how to install [here](https://phoenixnap.com/kb/how-to-install-python-3-windows)


## rocksdb

### Mac OS

```bash
$ brew install rocksdb
```

### Linux

```bash
git clone https://github.com/facebook/rocksdb.git
cd rocksdb
sudo DEBUG_LEVEL=0 make shared_lib install-shared
export LD_LIBRARY_PATH=/usr/local/lib
```

### Windows

Guide how to install [here](https://github.com/facebook/rocksdb/wiki/Building-on-Windows)

## Build gochain local docker

1. Clone goloop repository:

```bash
$ git clone git@github.com:icon-project/gochain-local.git
```

2. Build go chain docker images: 

```bash
$ cd goloop
$ make gochain-icon-image
```

3. If the command runs successfully, it generates the docker image like the following.

```bash
$ docker images goloop/gochain-icon
REPOSITORY            TAG       IMAGE ID       CREATED          SIZE
goloop/gochain-icon   latest    73927da2b1a0   20 seconds ago   512MB
```

## Build goloop cli

```
$ cd goloop
$ make goloop 
```

In Mac OS or Linux, copy `goloop/bin/goloop` to `/usr/local/bin`

In Windows, add `goloop/bin` PATH to Environment Variables

## start gochain local docker

1. Clone gochain local repository:

```bash
$ git clone https://github.com/icon-project/gochain-local.git
```

2. Start gochain local docker

```bash
cd gochain-local
./run_gochain_icon.sh start
```

3. Check local node running successfully

```bash
$ goloop rpc --uri http://127.0.0.1:9082/api/v3 lastblock
{
  "block_hash": "41b2a3bef4001a33273d55460c967712640b9098cd2bc18754f8dfd7c460f764",
  "version": "2.0",
  "height": 173,
  "time_stamp": 1637308091345452,
  "peer_id": "hxb6b5791be0b5ef67063b3c10b840fb81514db2fd",
  "prev_block_hash": "85a7846e796d9ebe744212e419629a47c6fddd631406e2938d67de4387e03257",
  "merkle_tree_root_hash": "",
  "signature": "",
  "confirmed_transaction_list": []
}
```

## Deploy simple java smart contract to local node

1. Clone java-score-examples repository for sample java SCORE

```bash
$ git clone https://github.com/icon-project/java-score-examples.git
```

2. Build java score example

```bash
$ cd java-score-examples
$ ./gradlew optimizedJar
```

3. Save godWallet java-score-examples folder

```json
{
  "address": "hxb6b5791be0b5ef67063b3c10b840fb81514db2fd",
  "id": "87323a66-289a-4ce2-88e4-00278deb5b84",
  "version": 3,
  "coinType": "icx",
  "crypto": {
    "cipher": "aes-128-ctr",
    "cipherparams": {
      "iv": "069e46aaefae8f1c1f840d6b09144999"
    },
    "ciphertext": "f35ff7cf4f5759cb0878088d0887574a896f7f0fc2a73898d88be1fe52977dbd",
    "kdf": "scrypt",
    "kdfparams": {
      "dklen": 32,
      "n": 65536,
      "r": 8,
      "p": 1,
      "salt": "0fc9c3b24cdb8175"
    },
    "mac": "1ef4ff51fdee8d4de9cf59e160da049eb0099eb691510994f5eca492f56c817a"
  }
}
```

3. Deploy hello world example to local node

```bash
$ goloop rpc --uri http://127.0.0.1:9082/api/v3 sendtx deploy hello-world/build/libs/hello-world-0.1.0-optimized.jar --key_store godWallet.json --key_password gochain --nid 0x3 --step_limit 10000000000 --content_type application/java --param name=daniel
0x7b9a3ff5612af55be91d913578a53d1a13945d2eb408bf337ee3a39c9700d3da
```

4. Check transaction result and get smart contract address

```bash
$ goloop rpc --uri http://127.0.0.1:9082/api/v3 txresult 0x7b9a3ff5612af55be91d913578a53d1a13945d2eb408bf337ee3a39c9700d3da
{
  "to": "cx0000000000000000000000000000000000000000",
  "cumulativeStepUsed": "0x3d70a6f9",
  "stepUsed": "0x3d70a6f9",
  "stepPrice": "0x2e90edd00",
  "eventLogs": [],
  "logsBloom": "0x00000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000",
  "status": "0x1",
  "scoreAddress": "cx59a4390ea74b4178269a61f3f5121d7f2d48ef55",
  "blockHash": "0xfcc6a16d4ba33f344cffe5674f6882e6c064e1bacbdca94b7c4e6e97a3ed6ae4",
  "blockHeight": "0x359",
  "txIndex": "0x0",
  "txHash": "0x7b9a3ff5612af55be91d913578a53d1a13945d2eb408bf337ee3a39c9700d3da"
}
```
