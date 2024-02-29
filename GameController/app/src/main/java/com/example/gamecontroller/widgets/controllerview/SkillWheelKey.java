package com.example.gamecontroller.widgets.controllerview;

import static com.example.gamecontroller.activitys.main.MainActivity.MAIN_TAG;

import android.graphics.Canvas;
import android.util.Log;
import android.view.MotionEvent;

import com.example.gamecontroller.activitys.main.functions.messagemanager.Pointer;

public class SkillWheelKey extends VirtualKey {
    private Circular skillCircular; // 技能图标
    private Joystick joystick; // 技能轮盘
    private boolean isDrag; // 是否拖拽中

    public SkillWheelKey(int keyId, int size, int centerX, int centerY, int targetSize, int targetCenterX, int targetCenterY) {
        super(keyId, size, centerX, centerY, targetSize, targetCenterX, targetCenterY);

        skillCircular = new Circular(size / 2, centerX, centerY, null);
        joystick = new Joystick(300, centerX, centerY, true, 0.5f, null, null);
    }

    @Override
    public void draw(Canvas canvas) {
        skillCircular.draw(canvas);
        if (isDrag) {
            joystick.draw(canvas);
        }
    }

    @Override
    public Pointer touch(int action, int touchX, int touchY) {
        Joystick.ShakeEvent shakeEvent = null;

        switch (action) {
            case MotionEvent.ACTION_DOWN:// 按下
                isDrag = true;
                joystick.setCenterPoint(touchX, touchY);
            case MotionEvent.ACTION_MOVE:
                shakeEvent = joystick.shake(action, touchX, touchY);
                break;
            case MotionEvent.ACTION_UP: // 抬起
                isDrag = false;
                shakeEvent = joystick.shake(action, touchX, touchY);
                break;
        }

        int targetRadius = targetSize / 2;

        assert shakeEvent != null;
        return new Pointer(keyId, targetRadius * shakeEvent.ratioX, targetRadius * shakeEvent.ratioY, 1.0f);
    }
}
