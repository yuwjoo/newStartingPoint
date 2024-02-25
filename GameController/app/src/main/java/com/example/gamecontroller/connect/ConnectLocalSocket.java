package com.example.gamecontroller.connect;

import static com.example.gamecontroller.activity.MainActivity.MAIN_TAG;

import android.net.LocalServerSocket;
import android.net.LocalSocket;
import android.util.Log;

import java.io.IOException;
import java.io.OutputStream;

/**
 * 建立本地socket连接
 */
public class ConnectLocalSocket {
    private static final String socketName = "game-controller-client"; // socket名称
    private static LocalServerSocket localServerSocket = null; // socket实例
    private static LocalSocket controlSocket = null; // 连接实例
    private static OutputStream controlOutputStream = null; // 发送消息输出流

    /**
     * 是否连接中
     *
     * @return 连接情况
     */
    public static boolean isConnect() {
        if (controlSocket != null) {
            return controlSocket.isConnected();
        } else {
            return false;
        }
    }

    /**
     * 启动socket服务
     */
    public static void start() {
        new Thread(() -> {
            try (LocalServerSocket lss = new LocalServerSocket(socketName)) {
                localServerSocket = lss;
                connect();
            } catch (IOException e) {
                close();
            }
        }).start();
    }

    /**
     * 连接socket
     */
    private static void connect() throws IOException {
        Log.v(MAIN_TAG, "等待连接");
        controlSocket = localServerSocket.accept();
        Log.v(MAIN_TAG, "连接成功");

        controlOutputStream = controlSocket.getOutputStream();
    }

    /**
     * 发送消息
     */
    public static void send(byte[] data) {
        try {
            controlOutputStream.write(data);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 关闭连接
     */
    public static void close() {
        if (controlSocket != null) {
            try {
                controlSocket.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
