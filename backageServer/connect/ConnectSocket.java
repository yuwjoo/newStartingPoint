package backageServer.connect;

import android.net.LocalServerSocket;
import android.net.LocalSocket;
import backageServer.entity.Message;
import backageServer.entity.MotionData;
import backageServer.manager.MessageManager;
import backageServer.utils.Controller;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

public class ConnectSocket {
    private static final String socketName = "game-controller-server"; // socket名称
    private static LocalServerSocket localServerSocket = null; // socket实例
    private static LocalSocket controlSocket = null; // 连接实例
    private static InputStream controlInputStream = null; // socket输入流
    private static final int MESSAGE_MAX_SIZE = 1 << 8; // 256k
    private static final byte[] rawBuffer = new byte[MESSAGE_MAX_SIZE];
    private static final ByteBuffer buffer = ByteBuffer.wrap(rawBuffer);

    static {
        buffer.limit(0);
    }

    /**
     * 启动socket
     */
    public static void start() {
        try (LocalServerSocket lss = new LocalServerSocket(socketName)) {
            localServerSocket = lss;
            connect();
        } catch (IOException e) {
            close();
        }
    }

    /**
     * 建立连接
     */
    private static void connect() throws IOException {
        System.out.println("等待连接");
        controlSocket = localServerSocket.accept();
        System.out.println("连接成功");
        controlInputStream = controlSocket.getInputStream();

        while (!Thread.currentThread().isInterrupted()) {
            // 当前位置后面没有可处理的数据
            if (!buffer.hasRemaining()) {
                readStream();
            }
            handleEvent();
        }
    }

    /**
     * 读取输入流
     */
    private static void readStream() throws IOException {
        int bufferLength = rawBuffer.length;
        int readLength = 0;

        buffer.clear();
        do {
            readLength = controlInputStream.read(rawBuffer, 0, bufferLength);
        } while (readLength == 0);
        if (readLength == -1) {
            connect();
            throw new EOFException("Controller socket closed");
        }
        buffer.position(readLength);
        buffer.flip();
    }

    /**
     * 处理消息事件
     */
    private static void handleEvent() {
        Message message = MessageManager.parse(buffer);
        switch (message.type) {
            case MessageManager.TOUCH_FLAG:
                Controller.injectTouch((MotionData) message.body);
                break;
            case MessageManager.CLOSE_SERVER_FLAG:
                close();
                break;
        }
    }

    /**
     * 关闭socket
     */
    public static void close() {
        if (controlSocket != null) {
            try {
                controlSocket.close();
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        }
    }
}
