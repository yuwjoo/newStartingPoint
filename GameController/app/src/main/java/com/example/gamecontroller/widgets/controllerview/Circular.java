package com.example.gamecontroller.widgets.controllerview;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;

/**
 * 圆形元素
 */
public class Circular {
    public static final int BACKGROUND_MODE_COLOR = 0; // 色值背景
    public static final int BACKGROUND_MODE_BITMAP = 1; // 位图背景

    private final Paint paint = new Paint(); // 画笔
    private int backgroundMode; // 背景模式
    private int backgroundColor; // 色值背景
    private Bitmap backgroundBitmap; // 位图背景
    private final Rect bitmapRect = new Rect(); // 位图矩形信息
    private final CircularSize circularSize; // 圆形尺寸

    /**
     * 构造函数
     *
     * @param size 圆形尺寸
     * @param backgroundDrawable 背景资源
     */
    public Circular(CircularSize size, Drawable backgroundDrawable) {
        paint.setAntiAlias(true);
        circularSize = size;
        setBackground(backgroundDrawable);
    }

    public int getRadius() {
        return circularSize.radius;
    }

    public void setRadius(int radius) {
        circularSize.setRadius(radius);
    }

    public int getDiameter() {
        return circularSize.diameter;
    }

    public void setDiameter(int diameter) {
        circularSize.setDiameter(diameter);
    }

    public int getCenterX() {
        return circularSize.centerX;
    }

    public int getCenterY() {
        return circularSize.centerY;
    }

    public void setCenter(int x, int y) {
        circularSize.setCenter(x, y);
    }

    /**
     * 设置背景
     *
     * @param drawable 资源
     */
    public void setBackground(Drawable drawable) {
        if (drawable == null) {
            backgroundMode = BACKGROUND_MODE_COLOR;
            backgroundColor = Color.BLACK;
            return;
        }

        if (drawable instanceof BitmapDrawable) {
            // 图片
            backgroundBitmap = ((BitmapDrawable) drawable).getBitmap();
            backgroundMode = BACKGROUND_MODE_BITMAP;
        } else if (drawable instanceof GradientDrawable) {
            // XML
            backgroundBitmap = drawable2Bitmap(drawable);
            backgroundMode = BACKGROUND_MODE_BITMAP;
        } else if (drawable instanceof ColorDrawable) {
            // 色值
            backgroundColor = ((ColorDrawable) drawable).getColor();
            backgroundMode = BACKGROUND_MODE_COLOR;
        }

        if (backgroundMode == BACKGROUND_MODE_BITMAP) {
            bitmapRect.set(0, 0, backgroundBitmap.getWidth(), backgroundBitmap.getHeight());
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
                canvas.drawBitmap(backgroundBitmap, bitmapRect, circularSize.getRect(), paint);
                break;
            case BACKGROUND_MODE_COLOR:
                // 色值
                paint.setColor(backgroundColor);
                canvas.drawCircle(getCenterX(), getCenterY(), getRadius(), paint);
                break;
        }
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
