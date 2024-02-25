package backageServer.utils;

import backageServer.entity.MotionData;
import backageServer.entity.Pointer;
import backageServer.manager.InputManager;
import android.os.SystemClock;
import android.view.InputDevice;
import android.view.MotionEvent;

/**
 * 触摸控制器
 */
public class Controller {
    private static final int DEFAULT_DEVICE_ID = 0; // 默认设备id 0：主设备
    private static long lastTouchDown; // 第一个指针触摸时间

    /**
     * 注入触摸事件
     * 
     * @param motionData 触摸数据
     * @return 是否注入成功
     */
    public static boolean injectTouch(MotionData motionData) {
        long now = SystemClock.uptimeMillis();
        int source = InputDevice.SOURCE_TOUCHSCREEN;
        int action = motionData.action;
        int pointerIndex = motionData.actionIndex;
        int pointerCount = motionData.pointerCount;
        int buttons = 0;
        MotionEvent.PointerProperties[] pointerProperties = new MotionEvent.PointerProperties[pointerCount];
        MotionEvent.PointerCoords[] pointerCoords = new MotionEvent.PointerCoords[pointerCount];

        for (int i = 0; i < pointerCount; ++i) {
            Pointer pointer = motionData.pointers[i];

            MotionEvent.PointerProperties props = new MotionEvent.PointerProperties();
            props.toolType = MotionEvent.TOOL_TYPE_FINGER;
            props.id = pointer.id;

            MotionEvent.PointerCoords coords = new MotionEvent.PointerCoords();
            coords.orientation = 0;
            coords.size = 0;
            // coords.x = pointer.y * ((float) 2676 / 2160);
            // coords.y = (1080 - pointer.x) * ((float) 1236 / 1080);
            coords.x = pointer.x * ((float) 1236 / 1080);
            coords.y = pointer.y * ((float) 2676 / 2160);
            coords.pressure = pointer.pressure;

            pointerProperties[i] = props;
            pointerCoords[i] = coords;
        }

        if (action == MotionEvent.ACTION_DOWN) {
            lastTouchDown = now;
        }

        if (action == MotionEvent.ACTION_POINTER_DOWN || action == MotionEvent.ACTION_POINTER_UP) {
            action = action | (pointerIndex << MotionEvent.ACTION_POINTER_INDEX_SHIFT);
        }

        MotionEvent event = MotionEvent.obtain(lastTouchDown, now, action, pointerCount, pointerProperties,
                pointerCoords, 0, buttons, 1f, 1f, DEFAULT_DEVICE_ID, 0, source, 0);

        return InputManager.getInstance().injectInputEvent(event, InputManager.INJECT_INPUT_EVENT_MODE_ASYNC);
    }
}
