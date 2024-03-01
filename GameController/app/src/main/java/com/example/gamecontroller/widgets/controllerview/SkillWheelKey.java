package com.example.gamecontroller.widgets.controllerview;

import android.graphics.Canvas;
import android.view.MotionEvent;

import com.example.gamecontroller.activitys.main.functions.messagemanager.Pointer;

import java.util.ArrayList;

public class SkillWheelKey extends VirtualKey {
    private Circular skillCircular; // 技能图标
    private Joystick joystick; // 技能轮盘
    private boolean isDrag; // 是否拖拽中

    public SkillWheelKey(int keyId, CircularSize size, CircularSize remoteSize) {
        super(keyId, size, remoteSize);

        skillCircular = new Circular(size, null);
        joystick = new Joystick(new CircularSize(200, size.centerX, size.centerY), true, 0.5f, null, null);
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
        float remoteX = 0;
        float remoteY = 0;
        float pressure = 0;

        switch (action) {
            case MotionEvent.ACTION_DOWN:
                isDrag = true;
                joystick.setCenter(touchX, touchY);
            case MotionEvent.ACTION_MOVE:
                ArrayList<Float> ratioList = joystick.shake(touchX, touchY);
                remoteX = remoteSize.centerX + remoteSize.radius * ratioList.get(0);
                remoteY = remoteSize.centerY + remoteSize.radius * ratioList.get(1);
                pressure = 1.0f;
                break;
            case MotionEvent.ACTION_UP:
                isDrag = false;
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
        skillCircular.setCenter(x, y);
        joystick.setCenter(x, y);
    }
}
