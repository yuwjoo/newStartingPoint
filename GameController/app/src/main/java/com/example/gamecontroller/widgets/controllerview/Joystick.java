package com.example.gamecontroller.widgets.controllerview;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.view.MotionEvent;

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

    public int getSize() {
        return this.roulette.;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public int getCenterX() {
        return centerX;
    }

    public int getCenterY() {
        return centerY;
    }

    public void setCenterPoint(int centerX, int centerY) {
        this.centerX = centerX;
        this.centerY = centerY;
        rouletteCircular.setCenterPoint(centerX, centerY);
        rockerCircular.setCenterPoint(centerX, centerY);
    }

    /**
     * 绘制
     *
     * @param canvas 画布
     */
    protected void draw(Canvas canvas) {
        rouletteCircular.draw(canvas);
        rockerCircular.draw(canvas);
    }

    /**
     * 摇动摇杆
     *
     * @param action 触摸动作
     * @param touchX 触摸x
     * @param touchY 触摸x
     */
    public ShakeEvent shake(int action, int touchX, int touchY) {
        Point rockerPoint = null;

        switch (action) {
            case MotionEvent.ACTION_DOWN:// 按下
            case MotionEvent.ACTION_MOVE:// 移动
                rockerPoint = getRockerPositionPoint(rouletteCircular.getCenterX(), rouletteCircular.getCenterY(), touchX, touchY, maxDistance); // 计算摇杆实际偏移的位置
                break;
            case MotionEvent.ACTION_UP: // 抬起
                rockerPoint = new Point(rouletteCircular.getCenterX(), rouletteCircular.getCenterY());
                break;
        }

        assert rockerPoint != null;
        rockerCircular.setCenterPoint(rockerPoint.x, rockerPoint.y);

        return  new ShakeEvent((float) (rockerPoint.x - rouletteCircular.getCenterX()) / +maxDistance, (float) (rockerPoint.y - rouletteCircular.getCenterY()) / +maxDistance);
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
    private Point getRockerPositionPoint(int startX, int startY, int endX, int endY, float maxDistance) {
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
