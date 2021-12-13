const fetch = (...args) => import('node-fetch').then(({default: fetch}) => fetch(...args));
const WebSocketClient = require('websocket').client;

const iconNodeUrl = 'https://btp.net.solidwallet.io/api/v3';
const iconNodeWebsocketUrl = 'wss://btp.net.solidwallet.io/api/v3/icon_dex/block';

const fetchLastBlock = async () => {
  const response = await fetch(iconNodeUrl, {
    method: 'post',
    body: JSON.stringify({
      jsonrpc: "2.0",
      method: "icx_getLastBlock",
      id: 1234
    }),
    headers: {'Content-Type': 'application/json'}
  });
  const data = await response.json();
  console.log("last block data: ", data.result);
}

const fetchBlockByHeight = async (height) => {
  const response = await fetch(iconNodeUrl, {
    method: 'post',
    body: JSON.stringify({
      jsonrpc: "2.0",
      method: "icx_getBlockByHeight",
      id: 1639046681803,
      params: {
        height: "0x" + height.toString(16),
      }
    }),
    headers: {'Content-Type': 'application/json'}
  });
  return (await response.json()).result;
}

fetchLastBlock();
fetchBlockByHeight(1000).then(d => console.log("block 1000 data: ", d));

const client = new WebSocketClient();

client.on('connectFailed', function(error) {
    console.log('Connect Error: ' + error.toString());
});

client.on('connect', function(connection) {
    console.log('WebSocket Client Connected');

    connection.on('error', function(error) {
      console.log("Connection Error: " + error.toString());
    });

    connection.on('close', function() {
      console.log('icon-websocket Connection Closed');
    });

    connection.on('message', async function(message) {
      const blockData = JSON.parse(message.utf8Data);
      console.log("Received new block: ");
      console.log("block hash: " + blockData.hash);
      console.log("block number: " + Number(blockData.height));

      if (blockData.indexes) {
        console.log('------- found event in block: ', blockData.height);
        console.log('------- event indexes: ', blockData.indexes);
        console.log('------- events: ', blockData.events);
      }

      console.log('------- xxxxxx ----------');
    });

    connection.send(JSON.stringify({
      "height": 7289162,
      "eventFilters": [
        {
          "addr": "cx652515186c82bc985554c9e3270d8661c8050cfd",
          "event": "Event2(Address,Address,int)",
          "indexed": [
            "cx652515186c82bc985554c9e3270d8661c8050cfd",
          ]
        }
      ]
    }));
});

// client.connect(iconNodeWebsocketUrl);