package com.example.gamecontroller.widgets.controllerview;

import android.graphics.Canvas;
import android.view.MotionEvent;

import com.example.gamecontroller.activitys.main.functions.messagemanager.Pointer;

public class PressKey extends VirtualKey {
    private final Circular skillCircular; // 图标

    public PressKey(int keyId, CircularSize size, CircularSize remoteSize) {
        super(keyId, size, remoteSize);

        skillCircular = new Circular(size, null);
    }

    @Override
    public void draw(Canvas canvas) {
        skillCircular.draw(canvas);
    }

    @Override
    public Pointer touch(int action, int touchX, int touchY) {
        float remoteX = remoteSize.centerX;
        float remoteY = remoteSize.centerY;
        float pressure = 0;

        switch (action) {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_MOVE:
                pressure = 1.0f;
                break;
            case MotionEvent.ACTION_UP:
                pressure = 0.0f;
                break;
        }

        return new Pointer(keyId, remoteX, remoteY, pressure);
    }

    @Override
    public void setCenter(int x, int y) {
        size.setCenter(x, y);
        skillCircular.setCenter(x, y);
    }
}
