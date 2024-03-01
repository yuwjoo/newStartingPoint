package com.example.gamecontroller.widgets.controllerview;

import android.graphics.Canvas;
import android.view.MotionEvent;

import com.example.gamecontroller.activitys.main.functions.messagemanager.Pointer;

import java.util.ArrayList;

public class DirectionKey extends VirtualKey {
    private Joystick joystick; // 轮盘

    public DirectionKey(int keyId, CircularSize size, CircularSize remoteSize) {
        super(keyId, size, remoteSize);

        joystick = new Joystick(size, false, 0.5f, null, null);
    }

    @Override
    public void draw(Canvas canvas) {
        joystick.draw(canvas);
    }

    @Override
    public Pointer touch(int action, int touchX, int touchY) {
        float remoteX = 0;
        float remoteY = 0;
        float pressure = 0;

        switch (action) {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_MOVE:
                ArrayList<Float> ratioList = joystick.shake(touchX, touchY);
                remoteX = remoteSize.centerX + remoteSize.radius * ratioList.get(0);
                remoteY = remoteSize.centerY + remoteSize.radius * ratioList.get(1);
                pressure = 1.0f;
                break;
            case MotionEvent.ACTION_UP:
                joystick.reset();
                remoteX = remoteSize.centerX;
                remoteY = remoteSize.centerY;
                pressure = 0.0f;
                break;
        }

        return new Pointer(keyId, remoteX, remoteY, pressure);
    }

    @Override
    public void setCenter(int x, int y) {
        size.setCenter(x, y);
        joystick.setCenter(x, y);
    }
}
