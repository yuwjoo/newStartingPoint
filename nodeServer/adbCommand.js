const { exec } = require("child_process");

/**
 * @description: 开启端口映射
 * @param {string} deviceName adb设备名称
 * @param {number} local 本地端口
 * @param {number} remote 远程端口
 * @return {Promise<void>}
 */
function openPortMap(deviceName, local, remote) {
  return new Promise((resolve, reject) => {
    exec(
      `adb -s ${deviceName} forward tcp:${local} localabstract:${remote}`,
      (error, stdout, stderr) => {
        if (error) {
          reject(`exec error: ${error}`);
        }
        resolve();
      }
    );
  });
}

module.exports = {
  openPortMap,
};
