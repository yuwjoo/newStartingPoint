package com.example.gamecontroller.widgets.controllerview;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.NonNull;

import com.example.gamecontroller.activitys.main.functions.ConnectLocalSocket;
import com.example.gamecontroller.activitys.main.functions.messagemanager.MessageManager;
import com.example.gamecontroller.activitys.main.functions.messagemanager.MotionData;
import com.example.gamecontroller.activitys.main.functions.messagemanager.Pointer;

import java.util.HashMap;
import java.util.Map;

public class ControllerView1 extends View {
    Map<Integer, VirtualKey> virtualKeyMap = new HashMap<>();
    Map<Integer, Integer> idMap = new HashMap<>();
    boolean isEdit; // 编辑状态
    VirtualKey dragVirtualKey; // 被拖拽的按键
    int startOffsetX; // 拖拽起始偏移坐标x
    int startOffsetY; // 拖拽起始偏移坐标y

    public ControllerView1(Context context, AttributeSet attrs) {
        super(context, attrs);

        SkillWheelKey skillWheelKey = new SkillWheelKey(10, new CircularSize(100, 150, 500), new CircularSize(500, 120, 230));
        virtualKeyMap.put(10, skillWheelKey);

        DirectionKey directionKey = new DirectionKey(11, new CircularSize(200, 700, 500), new CircularSize(500, 120, 230));
        virtualKeyMap.put(11, directionKey);

        PressKey pressKey = new PressKey(12, new CircularSize(80, 1200, 500), new CircularSize(500, 120, 230));
        virtualKeyMap.put(12, pressKey);

    }

    public boolean isEdit() {
        return isEdit;
    }

    public void setEdit(boolean edit) {
        isEdit = edit;
    }

    @Override
    protected void onDraw(@NonNull Canvas canvas) {
        super.onDraw(canvas);

        for (VirtualKey virtualKey : virtualKeyMap.values()) {
            virtualKey.draw(canvas);
        }
    }

    @Override
    public boolean performClick() {
        return super.performClick();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int actionMasked = event.getActionMasked();
        int action = actionMasked;
        int pointerCount = event.getPointerCount();
        Pointer[] pointers = new Pointer[pointerCount];

        if (isEdit) {
            int id = event.getPointerId(0);
            int x = (int) event.getX(0);
            int y = (int) event.getY(0);
            switch (actionMasked) {
                case MotionEvent.ACTION_DOWN:
                    dragVirtualKey = getTouchVirtualKey(id, x, y);
                    startOffsetX = dragVirtualKey.size.centerX - x;
                    startOffsetY = dragVirtualKey.size.centerY - y;
                    break;
                case MotionEvent.ACTION_MOVE:
                    dragVirtualKey.setCenter(startOffsetX + x, startOffsetY + y);
                    break;
                case MotionEvent.ACTION_UP:
                    dragVirtualKey = null;
                    idMap.clear();
            }
            invalidate();
            return true;
        }

        switch (actionMasked) {
            case MotionEvent.ACTION_DOWN:
                performClick();
            case MotionEvent.ACTION_POINTER_DOWN:
                action = MotionEvent.ACTION_DOWN;
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_POINTER_UP:
            case MotionEvent.ACTION_CANCEL:
                action = MotionEvent.ACTION_UP;
                break;
        }

        for (int i = 0; i < pointerCount; i++) {
            int id = event.getPointerId(i);
            int x = (int) event.getX(i);
            int y = (int) event.getY(i);
            float pressure = event.getPressure();
            VirtualKey virtualKey = getTouchVirtualKey(id, x, y);
            if (virtualKey != null) {
                pointers[i] = virtualKey.touch(action, x, y);
            } else {
                pointers[i] = new Pointer(id, x, y, pressure);
            }
        }

        if (action == MotionEvent.ACTION_UP) {
            idMap.clear();
        }

        MotionData motionData = new MotionData(actionMasked, event.getActionIndex(), pointerCount, pointers);

        if (ConnectLocalSocket.isConnect()) {
            ConnectLocalSocket.send(MessageManager.createTouchByte(motionData));
        }

        invalidate();

        return true;
    }

    private VirtualKey getTouchVirtualKey(int id, int x, int y) {
        VirtualKey virtualKey = null;

        if (idMap.containsKey(id)) {
            virtualKey = virtualKeyMap.get(idMap.get(id));
        } else {
            for (VirtualKey key : virtualKeyMap.values()) {
                if (key.inRect(x, y)) {
                    virtualKey = key;
                    idMap.put(id, key.keyId);
                    break;
                }
            }
        }
        return virtualKey;
    }
}
