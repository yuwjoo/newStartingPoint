package com.example.gamecontroller.activitys.gamecontroller.function.controllerviewmanager;

import android.graphics.Point;

public class KeyConfig {
    public static final int TYPE_JOYSTICK = 0; // 游戏杆
    public static final int TYPE_KEYSTROKE = 1; // 按键
    public static final int TYPE_JOYSTICK_AND_KEYSTROKE = 2; // 带轮盘按键
    public int keyId; // 按键id
    public int type; // 按键类型
    public int x; // x坐标
    public int y; // y坐标
    public int size; // 尺寸
    public boolean isRockerOverflow; // 摇杆是否可以超出轮盘
    public float rockerScale; // 摇杆缩放比例
    public Point targetPoint; // 目标触摸点坐标

    public KeyConfig(int keyId, int type, int x, int y, int size, boolean isRockerOverflow, float rockerScale, Point targetPoint) {
        this.keyId = keyId;
        this.type = type;
        this.x = x;
        this.y = y;
        this.size = size;
        this.isRockerOverflow = isRockerOverflow;
        this.rockerScale = rockerScale;
        this.targetPoint = targetPoint;
    }
}
