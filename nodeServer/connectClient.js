const net = require("net");
const { openPortMap } = require("./adbCommand");

const client = new net.Socket();
let onSuccess = null;
let onMessage = null;
let onClose = null;
let isConnect = false; // 是否连接中

/**
 * @description: 连接到客户端socket
 * @param {number} port 端口
 * @param {string} adbDeviceName adb设备名
 * @param {object} options 配置
 */
async function connect(port, adbDeviceName, options = {}) {
  onSuccess = options.onSuccess;
  onMessage = options.onMessage;
  onClose = options.onClose;
  await openPortMap(adbDeviceName, port, "game-controller-client");
  client.connect(port, "127.0.0.1", handleConnect);
  client.on("data", handleMessage);
  client.on("close", handleClose);
}

/**
 * @description: 处理连接成功
 */
function handleConnect() {
  isConnect = true;
  console.log("客户端-连接成功");
  onSuccess?.();
}

/**
 * @description: 处理接收消息
 * @param {any} data 消息
 */
function handleMessage(data) {
  // const dataView = new DataView(new Int8Array(Object.values(data)).buffer);
  // const flag = dataView.getInt32(0);
  // const id = dataView.getInt32(4);
  // const action = dataView.getInt32(8);
  // const x = dataView.getFloat32(12);
  // const y = dataView.getFloat32(16);
  // const pressure = dataView.getFloat32(20);
  // console.log("客户端-接受到: ", flag, id, action, x, y, pressure);
  onMessage?.(data);
}

/**
 * @description: 处理socket关闭
 */
function handleClose() {
  isConnect = false;
  console.log("客户端-断开连接");
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
