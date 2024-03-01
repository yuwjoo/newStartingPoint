package com.example.gamecontroller.widgets.controllerview;

import android.graphics.Rect;

public class KeyProp {
    public int id; // id
    public int type; // 类型
    public final Rect rect = new Rect(); // 矩形位置

    public KeyProp(int id, int type, int left, int top, int right, int bottom) {
        this.id = id;
        this.type = type;
        rect.set(left, top, right, bottom);
    }
}
