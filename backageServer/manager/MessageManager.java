package backageServer.manager;

import android.view.MotionEvent;

import backageServer.entity.Message;
import backageServer.entity.MotionData;
import backageServer.entity.Pointer;

import java.nio.ByteBuffer;

/**
 * 解析远程消息数据
 */
public class MessageManager {
    public static final int TOUCH_FLAG = 200; // 触摸标识
    public static final int CLOSE_SERVER_FLAG = 201; // 关闭服务标识
    public static final int UNKNOWN_FLAG = -1; // 未知标识

    /**
     * 解析消息数据
     *
     * @param buffer 字节数据
     * @return 解析后的消息对象
     */
    public static Message parse(ByteBuffer buffer) {
        switch (buffer.getInt()) {
            case TOUCH_FLAG:
                return new Message(TOUCH_FLAG, parseTouchBuffer(buffer));
            case CLOSE_SERVER_FLAG:
                return new Message(CLOSE_SERVER_FLAG, null);
            default:
                return new Message(UNKNOWN_FLAG, null);
        }
    }

    /**
     * 解析触摸字节数据
     *
     * @param buffer 字节数据
     * @return 触摸点
     */
    public static MotionData parseTouchBuffer(ByteBuffer buffer) {
        int action = buffer.getInt();
        int actionIndex = buffer.getInt();
        int pointerCount = buffer.getInt();
        Pointer[] pointers = new Pointer[pointerCount];
        for (int i = 0; i < pointerCount; i++) {
            pointers[i] = new Pointer(buffer.getInt(), buffer.getFloat(), buffer.getFloat(), buffer.getFloat());
        }
        return new MotionData(action, actionIndex, pointerCount, pointers);
    }

    /**
     * 创建触摸字节数据
     *
     * @param event 触摸事件
     * @return 字节数据： byte[
     *         int: 触摸标记，
     *         int: 触摸动作，
     *         int: 当前激活的指针下标,
     *         int: 指针数量,
     *         [int: 触摸点id, float: x坐标，float: y坐标，float: 按压力度]...
     *         ]
     */
    public static byte[] createTouchByte(MotionEvent event) {
        byte[] raw = new byte[1024];
        ByteBuffer buffer = ByteBuffer.wrap(raw);
        int pointerCount = event.getPointerCount();
        buffer.limit(0);
        buffer.compact();
        buffer.putInt(TOUCH_FLAG);
        buffer.putInt(event.getActionMasked());
        buffer.putInt(event.getActionIndex());
        buffer.putInt(pointerCount);
        for (int i = 0; i < pointerCount; i++) {
            buffer.putInt(event.getPointerId(i));
            buffer.putFloat(event.getX(i));
            buffer.putFloat(event.getY(i));
            buffer.putFloat(event.getPressure(i));
        }
        return raw;
    }

    /**
     * 创建触摸字节数据
     *
     * @param motionData 触摸数据
     * @return 字节数据
     */
    public static byte[] createTouchByte(MotionData motionData) {
        byte[] raw = new byte[1024];
        ByteBuffer buffer = ByteBuffer.wrap(raw);
        buffer.limit(0);
        buffer.compact();
        buffer.putInt(TOUCH_FLAG);
        buffer.putInt(motionData.action);
        buffer.putInt(motionData.actionIndex);
        buffer.putInt(motionData.pointerCount);
        for (int i = 0; i < motionData.pointerCount; i++) {
            Pointer pointer = motionData.pointers[i];
            buffer.putInt(pointer.id);
            buffer.putFloat(pointer.x);
            buffer.putFloat(pointer.y);
            buffer.putFloat(pointer.pressure);
        }
        return raw;
    }
}
