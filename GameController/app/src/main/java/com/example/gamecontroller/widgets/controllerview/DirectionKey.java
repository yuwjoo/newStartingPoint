package com.example.gamecontroller.widgets.controllerview;

import android.graphics.Canvas;
import android.view.MotionEvent;

import com.example.gamecontroller.activitys.main.functions.messagemanager.Pointer;

public class DirectionKey extends VirtualKey {
    private Joystick joystick; // 轮盘

    public DirectionKey(int keyId, int size, int centerX, int centerY, int targetSize, int targetCenterX, int targetCenterY) {
        super(keyId, size, centerX, centerY, targetSize, targetCenterX, targetCenterY);

        joystick = new Joystick(size, centerX, centerY, true, 0.5f, null, null);
    }

    @Override
    public void draw(Canvas canvas) {
        joystick.draw(canvas);
    }

    @Override
    public Pointer touch(int action, int touchX, int touchY) {
        Joystick.ShakeEvent shakeEvent = joystick.shake(action, touchX, touchY);
        int targetRadius = targetSize / 2;

        return new Pointer(keyId, targetRadius * shakeEvent.ratioX, targetRadius * shakeEvent.ratioY, 1.0f);
    }
}
