package com.example.gamecontroller.widgets.controllerview;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.view.MotionEvent;

import java.util.ArrayList;

/**
 * 游戏摇杆
 */
public class Joystick {
    private final Circular roulette; // 轮盘
    private final Circular rocker; // 摇杆
    private final int maxDistance; // 摇杆移动的最远距离

    public Joystick(CircularSize rouletteSize, boolean isRockerOverflow, float rockerScale, Drawable rouletteBackground, Drawable rockerBackground) {
        int rouletteRadius, rockerRadius, maxDistance;

        if (rouletteBackground == null) {
            rouletteBackground = new ColorDrawable();
            ((ColorDrawable) rouletteBackground).setColor(Color.GRAY); // 轮盘默认背景
        }

        if (rockerBackground == null) {
            rockerBackground = new ColorDrawable();
            ((ColorDrawable) rockerBackground).setColor(Color.WHITE); // 摇杆默认背景
        }

        rouletteRadius = rouletteSize.radius;// 轮盘的半径
        rockerRadius = (int) (rouletteRadius * rockerScale);// 摇杆的半径

        if (isRockerOverflow) {
            maxDistance = rouletteRadius; // 摇杆最大移动距离
        } else {
            maxDistance = rouletteRadius - rockerRadius; // 摇杆最大移动距离
        }

        this.roulette = new Circular(rouletteSize, rouletteBackground);
        this.rocker = new Circular(new CircularSize(rockerRadius, rouletteSize.centerX, rouletteSize.centerY), rockerBackground);
        this.maxDistance = maxDistance;
    }

    public int getDiameter() {
        return this.roulette.getDiameter();
    }

    public void setDiameter(int diameter) {
        this.roulette.setDiameter(diameter);
    }

    public int getCenterX() {
        return this.roulette.getCenterX();
    }

    public int getCenterY() {
        return this.roulette.getCenterY();
    }

    public void setCenter(int centerX, int centerY) {
        this.roulette.setCenter(centerX, centerY);
        this.rocker.setCenter(centerX, centerY);
    }

    /**
     * 绘制
     *
     * @param canvas 画布
     */
    protected void draw(Canvas canvas) {
        roulette.draw(canvas);
        rocker.draw(canvas);
    }

    /**
     * 摇动摇杆
     *
     * @param touchX 触摸x
     * @param touchY 触摸x
     */
    public ArrayList<Float> shake(int touchX, int touchY) {
        ArrayList<Float> ratioList = new ArrayList<>(2);

        Point rockerPoint = getRockerCenter(roulette.getCenterX(), roulette.getCenterY(), touchX, touchY, maxDistance); // 计算摇杆实际偏移的位置

        rocker.setCenter(rockerPoint.x, rockerPoint.y);
        ratioList.add(0, (float) ((rockerPoint.x - roulette.getCenterX()) / maxDistance));
        ratioList.add(1, (float) ((rockerPoint.y - roulette.getCenterY()) / maxDistance));

        return ratioList;
    }

    /**
     * 复位摇杆
     *
     */
    public void reset() {
        rocker.setCenter(roulette.getCenterX(), roulette.getCenterY());
    }

    /**
     * 获取摇杆实际要显示的位置（点）
     *
     * @param startX      起始点X
     * @param startY      起始点Y
     * @param endX        结束点X
     * @param endY        结束点Y
     * @param maxDistance 起始点和结束点的最大距离
     * @return 摇杆实际显示的位置（点）
     */
    private Point getRockerCenter(int startX, int startY, int endX, int endY, float maxDistance) {
        int lenX = endX - startX; // 两点在X轴的距离
        int lenY = endY - startY;// 两点在Y轴距离
        double lenXY = Math.sqrt(lenX * lenX + lenY * lenY); // 两点距离
        double radian = Math.acos(lenX / lenXY) * (endY < startY ? -1 : 1); // 计算弧度

        if (lenXY <= maxDistance) {
            // 触摸位置在可活动范围内
            return new Point(endX, endY);
        } else {
            // 触摸位置在可活动范围外
            int showPointX = (int) (startX + maxDistance * Math.cos(radian));
            int showPointY = (int) (startY + maxDistance * Math.sin(radian));
            return new Point(showPointX, showPointY);
        }
    }
}
