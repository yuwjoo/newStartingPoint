/**
 * .java -> .class
 * javac -source 1.8 -target 1.8 -encoding utf-8 -cp E:\InstallSoftware\Android\Sdk\platforms\android-29\android.jar *.java ./controller/*.java ./connect/*.java -d ./out
 * <p>
 * .class -> .dex
 * E:\InstallSoftware\Android\Sdk\build-tools\29.0.2\dx --dex --no-strict --output classes.dex .\out\com\example\gamecontroller\test\test
 * <p>
 * .dex -> .jar
 * jar cvf hello.jar classes.dex
 * <p>
 * 查看jar包
 * jar tf hello.jar
 * <p>
 * 推送文件到设备
 * adb push hello.jar /data/local/tmp/
 * <p>
 * 执行java代码
 * adb shell CLASSPATH=/data/local/tmp/server.jar app_process / backageServer.Server
 * <p>
 * 端口映射
 * adb forward tcp:13000 localabstract:gameController
 * <p>
 * 查看端口映射
 * adb forward --list
 */
const { exec, spawn } = require("child_process");
const fs = require("fs");
const { resolve } = require("path");

const config1 = {
  androidJarPath:
    "E:\\InstallSoftware\\Android\\Sdk\\platforms\\android-29\\android.jar", // android jar包路径
  dxPath: "E:\\InstallSoftware\\Android\\Sdk\\build-tools\\29.0.2\\dx", // dx程序路径
  outputJarFile: "server.jar", // 打包出来的jar包名称
  outputPath: "./.output", // 打包文件存放路径
  tempPath: "./.temp", // 存放打包时产生的额外文件的目录（打包完成后自动删除）
  rootPackageName: "backageServer", // java程序根包名
  deviceName: "TTDUT21C07001910", // 运行adb命令的设备名称
};

const config2 = {
  androidJarPath:
    "C:\\Users\\YH\\AppData\\Local\\Android\\Sdk\\platforms\\android-29\\android.jar", // android jar包路径
  dxPath:
    "C:\\Users\\YH\\AppData\\Local\\Android\\Sdk\\build-tools\\29.0.2\\dx", // dx程序路径
  outputJarFile: "server.jar", // 打包出来的jar包名称
  outputPath: "./.output", // 打包文件存放路径
  tempPath: "./.temp", // 存放打包时产生的额外文件的目录（打包完成后自动删除）
  rootPackageName: "backageServer", // java程序根包名
  deviceName: "TTDUT21C07001910", // 运行adb命令的设备名称
};

/**
 * @description: 执行命令
 * @param {string} cmd cmd命令
 * @return {Promise<any>}
 */
function execPromise(cmd) {
  return new Promise((resolve, reject) => {
    exec(cmd, (error, stdout, stderr) => {
      if (error) {
        reject(error);
      }
      resolve(stdout);
    });
  });
}

/**
 * @description: 执行命令(动态获取命令结果)
 * @param {string} cmd cmd命令
 * @param {object} options 配置
 */
function spawnCmd(cmd, options) {
  const args = cmd.split(" ");
  const command = args.shift();
  const { stdout } = spawn(command, args, {});

  stdout.on("data", (stream) => {
    options?.onMessage(stream);
  });
}

/**
 * 创建目录
 * @param {string} path 目录路径
 */
function mkDir(path) {
  fs.mkdirSync(path, { recursive: true });
}

/**
 * 删除目录
 * @param {string} path 目录路径
 */
function removeDir(path) {
  if (fs.existsSync(path)) {
    fs.readdirSync(path).forEach((file) => {
      const curPath = path + "/" + file;
      if (fs.lstatSync(curPath).isDirectory()) {
        removeDir(curPath);
      } else {
        fs.unlinkSync(curPath);
      }
    });
    fs.rmdirSync(path);
  }
}

/**
 * 查看目录文件
 * @param {string} path 目录路径
 * @return string[] 文件名列表
 */
function lookDir(path) {
  return fs.readdirSync(path);
}

/**
 * 开始编译
 * @param {object} config 配置
 */
async function compile(config) {
  const includeJavaFile = ["*.java"]; // 需要编译的java文件

  lookDir("./").forEach((fileName) => {
    const path = resolve(__dirname, fileName);
    const noOutputDir = resolve(fileName) !== resolve(config.outputPath);
    if (noOutputDir && fs.lstatSync(path).isDirectory()) {
      includeJavaFile.push(resolve(path, "*.java"));
    }
  });

  mkDir(config.tempPath); // 创建存放临时文件的目录
  mkDir(config.outputPath); // 创建存放输出文件的目录

  // .java -> .class
  await execPromise(
    `javac -source 1.8 -target 1.8 -encoding utf-8 -cp ${
      config.androidJarPath
    } ${includeJavaFile.join(" ")} -d ${config.tempPath}`
  );

  // .class -> .dex
  await execPromise(
    `${config.dxPath} --dex --no-strict --output classes.dex ${resolve(
      config.tempPath,
      config.rootPackageName
    )}`
  );

  // .dex -> .jar
  await execPromise(
    `jar cvf ${resolve(config.outputPath, config.outputJarFile)} classes.dex`
  );

  removeDir(config.tempPath); // 删除存放临时文件的目录
  fs.unlinkSync("./classes.dex"); // 删除临时dex文件
}

/**
 * 运行到设备
 * @param {object} config 配置
 */
async function runToDevice(config) {
  const remoteDir = "/data/local/tmp/"; // 远程存放文件的目录

  // 推送文件到设备
  await execPromise(
    `adb -s ${config.deviceName} push ${resolve(
      config.outputPath,
      config.outputJarFile
    )} ${remoteDir}`
  );

  // 执行程序
  spawnCmd(
    `adb -s ${config.deviceName} shell CLASSPATH=${remoteDir}${config.outputJarFile} app_process / ${config.rootPackageName}.Server`,
    {
      onMessage: (data) => {
        console.log(data.toString());
      },
    }
  );
}

compile(config1);
runToDevice(config1);
