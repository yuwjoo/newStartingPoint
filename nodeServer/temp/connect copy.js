const net = require("net");
const TouchData = require("./TouchData");
const cmd = require("./cmd");

const host = "127.0.0.1"; // 服务端ip地址
const port = 13000; // 服务端端口号
const client = new net.Socket();

cmd.adbPortMap().then(() => {
  client.connect(port, host, handleConnect);
  client.on("data", handleMessage);
  client.on("close", handleClose);
});

/**
 * @description: 处理连接
 */
function handleConnect() {
  console.log("连接了");

  cmd.listenerEvent((actions) => {
    actions.forEach((touch) => {
      console.log(touch.id, touch.action, touch.x, touch.y);
      client.write(
        new TouchData(touch.id, touch.action, touch.x, touch.y).getUint8Array()
      );
    });
  });

  // client.write(new Uint8Array([20])); // 结束连接

  // sendSwipe(500, 500, 1000, 500, 1000, (action, x, y) => {
  //   console.log("发送", action, x, y)
  //   client.write(new TouchData(1, action, x, y).getUint8Array());
  // });

  // client.write(new TouchData(0, 0, 500, 500).getUint8Array());
  // // client.write(new TouchData(2, 0, 800, 800).getUint8Array());
  // console.log("按下");

  // setTimeout(() => {
  //   client.write(new TouchData(0, 1, 500, 500).getUint8Array());
  //   // client.write(new TouchData(4, 0, 400, 300).getUint8Array());
  //   console.log("按下2");

  //   setTimeout(() => {
  //     client.write(new TouchData(0, 0, 1000, 1000).getUint8Array());
  //     // client.write(new TouchData(4, 0, 400, 300).getUint8Array());
  //     console.log("按下3");
  //   }, 3000);
  // }, 3000);

  // setTimeout(() => {
  //   client.write(new TouchData(1, 0, 800, 800).getUint8Array());
  //   // client.write(new TouchData(1, 1, 500, 500).getUint8Array());
  //   console.log("抬起");
  // }, 500);
}

/**
 * @description: 处理接收数据
 */
function handleMessage(data) {
  console.log("接受到: " + data);
  // client.destroy();
}

/**
 * @description: 处理连接关闭
 */
function handleClose() {
  console.log("连接关闭");
}

function sendSwipe(x1, y1, x2, y2, duration, cb) {
  if (duration < 0) {
    duration = 300;
  }
  let now = Date.now();
  cb(0, x1, y1);
  const startTime = now;
  const endTime = startTime + duration;
  const lerp = (a, b, alpha) => (b - a) * alpha + a;
  const timear = setInterval(() => {
    if (now > endTime) {
      clearInterval(timear);
      cb(1, x2, y2);
      return;
    }
    const elapsedTime = now - startTime;
    const alpha = elapsedTime / duration;
    cb(2, lerp(x1, x2, alpha), lerp(y1, y2, alpha));
    now = Date.now();
  }, 10);
}
