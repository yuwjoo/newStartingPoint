package com.example.gamecontroller.widgets.gamerocker;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;

/**
 * 圆环类
 */
public class Ring {
    public final int BACKGROUND_MODE_COLOR = 0; // 背景模式：色值
    public final int BACKGROUND_MODE_BITMAP = 1; // 背景模式：位图
    private Bitmap bitmapBackground; // 位图数据
    private int colorBackground; // 色值数据
    private int radius = 0; // 半径
    private int backgroundMode; // 背景模式
    private Rect bitmapRect; // 位图矩形信息
    private final Paint paint; // 画笔

    public Ring() {
        paint = new Paint();
        paint.setAntiAlias(true);
    }

    public int getRadius() {
        return radius;
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
            bitmapRect = new Rect();
            bitmapRect.set(0, 0, bitmapBackground.getWidth(), bitmapBackground.getHeight());
        }
    }

    /**
     * 设置尺寸
     *
     * @param radius 半径值
     */
    public void setSize(int radius) {
        this.radius = radius;
    }

    /**
     * 绘制控件
     *
     * @param canvas 画布
     * @param x      x坐标
     * @param y      y坐标
     */
    public void draw(Canvas canvas, int x, int y) {
        switch (backgroundMode) {
            case BACKGROUND_MODE_BITMAP:
                // 位图
                Rect positionRect = new Rect(x - radius, y - radius, x + radius, y + radius);
                canvas.drawBitmap(bitmapBackground, bitmapRect, positionRect, paint);
                break;
            case BACKGROUND_MODE_COLOR:
                // 色值
                paint.setColor(colorBackground);
                canvas.drawCircle(x, y, radius, paint);
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
