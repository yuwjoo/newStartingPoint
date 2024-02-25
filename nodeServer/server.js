const connectClient = require("./connectClient");
const connectServer = require("./connectServer");

connectServer.connect(13001, "TTDUT21C07001910");
connectClient.connect(13000, "192.168.0.101:5555", {
  onMessage: (data) => {
    if (connectServer.getConnectState()) {
      connectServer.send(new Uint8Array(Object.values(data)));
    }
  },
});
