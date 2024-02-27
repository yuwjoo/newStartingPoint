package com.example.gamecontroller.widgets.joystick;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;

public class Circular {
    public static final int BACKGROUND_MODE_COLOR = 0; // 背景模式：色值
    public static final int BACKGROUND_MODE_BITMAP = 1; // 背景模式：位图
    private int backgroundMode; // 背景模式
    private Bitmap bitmapBackground; // 位图数据
    private int colorBackground; // 色值数据
    private int radius; // 半径
    private final Paint paint; // 画笔
    private final Rect bitmapRect; // 位图矩形信息
    private final Rect positionRect; // 位置矩形信息
    private final Point centerPoint; // 中心点坐标

    public Circular() {
        paint = new Paint();
        paint.setAntiAlias(true);

        bitmapRect = new Rect();

        positionRect = new Rect();

        centerPoint = new Point();

        backgroundMode = BACKGROUND_MODE_COLOR;
        colorBackground = Color.BLACK;
        radius = 0;
    }

    /**
     * 获取中心点坐标
     */
    public Point getCenterPoint() {
        return centerPoint;
    }

    /**
     * 设置中心点坐标
     *
     * @param x x坐标
     * @param y y坐标
     */
    public void setCenterPoint(int x, int y) {
        this.centerPoint.set(x, y);
    }

    /**
     * 获取半径
     */
    public int getRadius() {
        return radius;
    }

    /**
     * 设置半径
     *
     * @param radius 半径值
     */
    public void setRadius(int radius) {
        this.radius = radius;
    }

    /**
     * 设置背景
     *
     * @param drawable 资源
     */
    public void setBackground(Drawable drawable) {
        if (drawable == null) {
            throw new RuntimeException("背景资源不能为 null");
        }

        if (drawable instanceof BitmapDrawable) {
            // 图片
            bitmapBackground = ((BitmapDrawable) drawable).getBitmap();
            backgroundMode = BACKGROUND_MODE_BITMAP;
        } else if (drawable instanceof GradientDrawable) {
            // XML
            bitmapBackground = drawable2Bitmap(drawable);
            backgroundMode = BACKGROUND_MODE_BITMAP;
        } else if (drawable instanceof ColorDrawable) {
            // 色值
            colorBackground = ((ColorDrawable) drawable).getColor();
            backgroundMode = BACKGROUND_MODE_COLOR;
        }

        if (backgroundMode == BACKGROUND_MODE_BITMAP) {
            bitmapRect.set(0, 0, bitmapBackground.getWidth(), bitmapBackground.getHeight());
        }
    }

    /**
     * 绘制控件
     *
     * @param canvas 画布
     */
    public void draw(Canvas canvas) {
        switch (backgroundMode) {
            case BACKGROUND_MODE_BITMAP:
                // 位图
                positionRect.contains(centerPoint.x - radius, centerPoint.y - radius, centerPoint.x + radius, centerPoint.y + radius);
                canvas.drawBitmap(bitmapBackground, bitmapRect, positionRect, paint);
                break;
            case BACKGROUND_MODE_COLOR:
                // 色值
                paint.setColor(colorBackground);
                canvas.drawCircle(centerPoint.x, centerPoint.y, radius, paint);
                break;
        }
    }

    /**
     * 是否在矩形范围内发生的触摸
     *
     * @param x x坐标
     * @param y y坐标
     * @return 是否在矩形范围内
     */
    public boolean isTouchRect(int x, int y) {
        return x >= centerPoint.x - radius && x <= centerPoint.x + radius && y >= centerPoint.y - radius && y <= centerPoint.y + radius;
    }

    /**
     * Drawable 转 Bitmap
     *
     * @param drawable Drawable
     * @return Bitmap
     */
    private Bitmap drawable2Bitmap(Drawable drawable) {
        // 取 drawable 的长宽
        int width = drawable.getIntrinsicWidth();
        int height = drawable.getIntrinsicHeight();
        // 取 drawable 的颜色格式
        Bitmap.Config config = drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888 : Bitmap.Config.RGB_565;
        // 建立对应 bitmap
        Bitmap bitmap = Bitmap.createBitmap(width, height, config);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, width, height);
        drawable.draw(canvas);
        return bitmap;
    }
}
