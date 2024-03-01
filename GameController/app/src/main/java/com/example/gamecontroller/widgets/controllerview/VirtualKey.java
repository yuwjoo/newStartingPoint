package com.example.gamecontroller.widgets.controllerview;

import android.graphics.Canvas;
import android.graphics.Rect;

import com.example.gamecontroller.activitys.main.functions.messagemanager.Pointer;

abstract public class VirtualKey {
    public static final int KEY_TYPE_DIRECTION = 0; // 方向健
    public static final int KEY_TYPE_SKILL = 1; // 技能健
    public static final int KEY_TYPE_TRIGGER = 2; // 触发键

    private int id; // id
    private final int type; // 类型
    private final Rect rect = new Rect(); // 矩形位置

    public VirtualKey(int id, int type, int left, int top, int right, int bottom) {
        this.id = id;
        this.type = type;
        rect.set(left, top, right, bottom);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getType() {
        return type;
    }

    abstract public void draw(Canvas canvas);

    abstract public Pointer touch(int action, int touchX, int touchY);

    abstract public void setCenter(int x, int y);

    public boolean inRect(int x, int y) {
        return rect.contains(x, y);
    }
}
