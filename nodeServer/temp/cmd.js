const { spawn, exec } = require("child_process");

class PointerManager {
  pointersMap = new Map(); // 触摸点集合
  oldPointersMap = new Map(); // 旧的触摸点集合

  id; // 触摸点id
  x; // 触摸点x
  y; // 触摸点y
  isUp = false; // 是否抬起

  setId(id) {
    this.id = id;
  }

  setX(x) {
    this.x = x;
  }

  setY(y) {
    this.y = y;
  }

  setIsUp(isUp) {
    this.isUp = isUp;
  }

  /**
   * @description: 同步一个触摸点
   */
  syncMT() {
    if (this.id !== undefined) {
      this.pointersMap.set(this.id, {
        x: this.x || this.oldPointersMap.get(this.id)?.x || 0,
        y: this.y || this.oldPointersMap.get(this.id)?.y || 0,
      });
    }
    this.id = this.x = this.y = undefined;
  }

  /**
   * @description: 同步当前所有触摸点
   * @return 触摸动作列表
   */
  sync() {
    let actions = [];
    this.pointersMap.forEach((pos, id) => {
      const oldPointer = this.oldPointersMap.get(id);
      if (oldPointer) {
        if (oldPointer.x !== pos.x || oldPointer.y !== pos.y) {
          // 移动点
          actions.push({ ...pos, id, action: 2 });
        }
        this.oldPointersMap.delete(id);
      } else {
        // 按下点
        actions.push({ ...pos, id, action: 0 });
      }
    });

    if (this.isUp || this.pointersMap.length) {
      this.oldPointersMap.forEach((pos, id) => {
        // 抬起点
        actions.push({ ...pos, id, action: 1 });
      });
    }
    this.oldPointersMap = new Map(this.pointersMap);
    this.pointersMap.clear();
    this.isUp = false;
    return actions;
  }
}

const deviceName = "192.168.6.183:5555"; // adb操作的设备名称
const targetDeviceName = "emulator-5554"; // 被控设备名称
const pointerManager = new PointerManager(); // 指针管理器
let eventRange = new Map(); // 事件位置范围
let deviceRange = new Map(); // 设备位置范围

/**
 * @description: 设置事件位置范围
 */
function setEventRange() {
  return new Promise((resolve, reject) => {
    exec(`adb -s ${deviceName} shell getevent -p`, (error, stdout, stderr) => {
      if (error) {
        reject(`exec error: ${error}`);
      }

      const str = stdout.toString();
      const start = str.indexOf("input_mt_wrapper");
      const end = str.indexOf("add device", start);
      const matchArr = str
        .substring(start, end)
        .match(/(0035|0036).+max\s\d{0,6}/gm);
      if (!matchArr) {
        reject("未获取到事件位置范围");
      }
      matchArr.forEach((str) => {
        const arr = str.split(" ");
        const code = arr.shift();
        const max = Number(arr.pop());
        eventRange.set(code === "0035" ? "x" : "y", { min: 0, max });
      });
      resolve();
    });
  });
}

/**
 * @description: 使用adb开启端口映射
 */
function adbPortMap() {
  return new Promise((resolve, reject) => {
    exec(
      `adb -s ${targetDeviceName} forward tcp:13000 localabstract:gameController`,
      (error, stdout, stderr) => {
        if (error) {
          reject(`exec error: ${error}`);
        }
        resolve();
      }
    );
  });
}

/**
 * @description: 监听手机触摸事件
 * @param {function} cb 回调函数（触摸动作列表）
 */
function listenerTouchEvent(cb) {
  const { stdout } = spawn("adb", ["-s", deviceName, "shell", "getevent"], {});

  stdout.on("data", (stream) => {
    const raw = stream.toString().trim().split("\r\n");
    raw.forEach((line) => {
      const [, type, code, value] = line.split(" ");
      if (type === "0001" && code === "014a") {
        // 是触摸状态事件
        pointerManager.setIsUp(parseInt(value, 16) === 0);
      } else if (type === "0003") {
        // 触摸信息事件
        switch (code) {
          case "0039":
            // 触摸点id
            pointerManager.setId(parseInt(value, 16));
            break;
          case "0035":
            // x坐标(游戏横屏关系实际给到y的触摸坐标)
            pointerManager.setX(
              parseInt(value, 16) *
                (deviceRange.get("x").max / eventRange.get("x").max)
            );
            break;
          case "0036":
            // y坐标(游戏横屏关系实际给到x的触摸坐标)
            pointerManager.setY(
              parseInt(value, 16) *
                (deviceRange.get("y").max / eventRange.get("y").max)
            );
            break;
        }
      } else if (type === "0000") {
        // 系统事件
        switch (code) {
          case "0000":
            // 同步所有点
            const actions = pointerManager.sync();
            cb(actions);
            break;
          case "0002":
            // 同步当前点
            pointerManager.syncMT();
            break;
        }
      }
    });
  });
}

module.exports = {
  listenerEvent: async (cb) => {
    deviceRange.set("x", { min: 0, max: 1080 });
    deviceRange.set("y", { min: 0, max: 1920 });
    await setEventRange();
    listenerTouchEvent(cb);
  },
  adbPortMap,
};

// const map = {
//   ABS_MT_POSITION_X: "x坐标",
//   ABS_MT_POSITION_Y: "y坐标",
//   ABS_MT_PRESSURE: "压力",
//   ABS_MT_TRACKING_ID: "追踪id",
//   ABS_MT_TOUCH_MAJOR: "触摸主要",
//   ABS_MT_TOUCH_MINOR: "触摸次要",
//   ABS_MT_ORIENTATION: "方向",
//   ABS_MT_BLOB_ID: "点id",
//   SYN_MT_REPORT: "同步MT报告",
// };
