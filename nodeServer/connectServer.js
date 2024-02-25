const net = require("net");
const { openPortMap } = require("./adbCommand");

const client = new net.Socket();
let onSuccess = null;
let onMessage = null;
let onClose = null;
let isConnect = false; // 是否连接中

/**
 * @description: 连接到服务端socket
 * @param {number} port 端口
 * @param {string} adbDeviceName adb设备名
 * @param {object} options 配置
 */
async function connect(port, adbDeviceName, options = {}) {
  onSuccess = options.onSuccess;
  onMessage = options.onMessage;
  onClose = options.onClose;
  await openPortMap(adbDeviceName, port, "game-controller-server");
  client.connect(port, "127.0.0.1", handleConnect);
  client.on("data", handleMessage);
  client.on("close", handleClose);
}

/**
 * @description: 处理连接成功
 */
function handleConnect() {
  isConnect = true;
  console.log("服务端-连接成功");
  onSuccess?.();
}

/**
 * @description: 处理接收消息
 * @param {any} data 消息
 */
function handleMessage(data) {
  console.log("服务端-接受到: " + data);
  onMessage?.(data);
}

/**
 * @description: 处理socket关闭
 */
function handleClose() {
  isConnect = false;
  console.log("服务端-断开连接");
  onClose?.();
}

/**
 * @description: 发送数据
 * @param {Uint8Array} data 数据
 */
function send(data) {
  client.write(data);
}

/**
 * @description: 获取连接状态
 */
function getConnectState() {
  return isConnect;
}

/**
 * @description: 关闭socket连接
 */
function close() {
  client.destroy();
}

module.exports = {
  connect,
  send,
  getConnectState,
  close,
};
