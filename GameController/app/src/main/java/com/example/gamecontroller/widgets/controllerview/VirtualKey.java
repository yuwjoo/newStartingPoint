package com.example.gamecontroller.widgets.controllerview;

import android.graphics.Canvas;

import com.example.gamecontroller.activitys.main.functions.messagemanager.Pointer;

abstract public class VirtualKey {
    public int keyId; // 按键id
    public int size; // 尺寸
    public int centerX; // 中心点x
    public int centerY; // 中心点y
    public int targetSize; // 目标尺寸
    public int targetCenterX; // 目标中心点x
    public int targetCenterY; // 目标中心点y

    public VirtualKey(int keyId, int size, int centerX, int centerY, int targetSize, int targetCenterX, int targetCenterY) {
        this.keyId = keyId;
        this.size = size;
        this.centerX = centerX;
        this.centerY = centerY;
        this.targetSize = targetSize;
        this.targetCenterX = targetCenterX;
        this.targetCenterY = targetCenterY;
    }

    abstract public void draw(Canvas canvas);

    abstract public Pointer touch(int action, int touchX, int touchY);
}
