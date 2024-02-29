package com.example.gamecontroller.widgets.controllerview;

import android.graphics.Canvas;
import android.view.MotionEvent;

import com.example.gamecontroller.activitys.main.functions.messagemanager.Pointer;

public class PressKey extends VirtualKey {
    private final Circular skillCircular; // 图标

    public PressKey(int keyId, int size, int centerX, int centerY, int targetSize, int targetCenterX, int targetCenterY) {
        super(keyId, size, centerX, centerY, targetSize, targetCenterX, targetCenterY);

        skillCircular = new Circular(size / 2, centerX, centerY, null);
    }

    @Override
    public void draw(Canvas canvas) {
        skillCircular.draw(canvas);
    }

    @Override
    public Pointer touch(int action, int touchX, int touchY) {
        return new Pointer(keyId, targetCenterX, targetCenterY, 1.0f);
    }
}
