package com.example.gamecontroller.widgets.controllerview;

import android.graphics.Rect;

public class CircularSize {
    public int radius; // 半径
    public int diameter; // 直径
    public int centerX; // 中心点x
    public int centerY; // 中心点y
    private final Rect rect = new Rect(); // 矩形信息

    public CircularSize(int radius, int centerX, int centerY) {
        this.radius = radius;
        this.centerX = centerX;
        this.centerY = centerY;
        this.diameter = radius * 2;
    }

    public void setRadius(int radius) {
        this.radius = radius;
        this.diameter = radius * 2;
    }

    public void setDiameter(int diameter) {
        this.diameter = diameter;
        this.radius = diameter / 2;
    }

    public void setCenter(int x, int y) {
        this.centerX = x;
        this.centerY = y;
    }

    public Rect getRect() {
        updateRect();
        return this.rect;
    }

    public void updateRect() {
        this.rect.set(centerX - radius, centerY - radius, centerX + radius, centerY + radius);
    }
}
