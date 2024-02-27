package com.example.gamecontroller.widgets.gamerocker;

import static com.example.gamecontroller.activitys.main.MainActivity.MAIN_TAG;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.gamecontroller.R;

public class GameRocker extends View {
    private static final int DEFAULT_SIZE = 400; // 默认控件大小
    private float upperScale; // 中心圆环缩放比例
    private final Ring lowerRing = new Ring(); // 底部圆环
    private final Ring upperRing = new Ring(); // 中心圆环
    private final Point centerPoint = new Point(); // 中心点坐标
    private final Point offsetPoint = new Point(); // 偏移值坐标（根据触摸点和中心点的差值计算得出）
    private OnRockerOffsetListener mOnRockerOffsetListener; // 摇杆偏移监听器
    private boolean canOverflowRing; // 摇杆是否可以超出外环

    public GameRocker(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        initAttribute(context, attrs); // 初始化布局属性
    }

    /**
     * 初始化属性
     *
     * @param context 上下文
     * @param attrs   布局属性
     */
    private void initAttribute(Context context, AttributeSet attrs) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.GameRocker); // 布局属性
        Drawable lowerBackground = typedArray.getDrawable(R.styleable.GameRocker_lowerBackground); // 底部圆环背景
        Drawable upperBackground = typedArray.getDrawable(R.styleable.GameRocker_upperBackground); // 中心圆环背景

        if (lowerBackground == null) {
            lowerBackground = new ColorDrawable();
            ((ColorDrawable) lowerBackground).setColor(Color.GRAY); // 底部圆环默认颜色值
        }

        if (upperBackground == null) {
            upperBackground = new ColorDrawable();
            ((ColorDrawable) upperBackground).setColor(Color.WHITE); // 中心圆环默认颜色值
        }

        lowerRing.setBackground(lowerBackground);
        upperRing.setBackground(upperBackground);

        canOverflowRing = typedArray.getBoolean(R.styleable.GameRocker_canOverflowRing, true);
        upperScale = typedArray.getFloat(R.styleable.GameRocker_upperScale, 0.5f);

        typedArray.recycle();
    }

    /**
     * 初始化位置信息
     */
    private void initPosition() {
        int measuredWidth = getMeasuredWidth();
        int measuredHeight = getMeasuredHeight();

        int cx = measuredWidth / 2;
        int cy = measuredHeight / 2;
        float scale = canOverflowRing ? upperScale + 1 : 1;

        int lowerRadius = (int) (Math.min(cx, cy) / scale);// 底部圆环的半径
        int upperRadius = (int) (lowerRadius * upperScale);// 中心圆环的半径

        lowerRing.setSize(lowerRadius);
        upperRing.setSize(upperRadius);

        centerPoint.set(cx, cy);
    }

    /**
     * 设置摇杆偏移监听器
     *
     * @param listener 监听器
     */
    public void setOnRockerOffsetListener(OnRockerOffsetListener listener) {
        mOnRockerOffsetListener = listener;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int measureWidth, measureHeight;

        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        Log.v(MAIN_TAG, "子级测量：" + "width:" + widthSize + "--" + widthMode + " height:" + heightSize + "--" + heightMode);

        if (widthMode == MeasureSpec.EXACTLY) {
            // 具体的值和match_parent
            measureWidth = widthSize;
        } else {
            // wrap_content
            measureWidth = DEFAULT_SIZE;
        }
        if (heightMode == MeasureSpec.EXACTLY) {
            measureHeight = heightSize;
        } else {
            measureHeight = DEFAULT_SIZE;
        }

        setMeasuredDimension(measureWidth, measureHeight);

        initPosition();
    }

    @Override
    protected void onDraw(@NonNull Canvas canvas) {
        super.onDraw(canvas);

        lowerRing.draw(canvas, centerPoint.x, centerPoint.y);
        upperRing.draw(canvas, centerPoint.x + offsetPoint.x, centerPoint.y + offsetPoint.y);
    }

    @Override
    public boolean performClick() {
        return super.performClick();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:// 按下
                performClick();
            case MotionEvent.ACTION_MOVE:// 移动
                float moveX = event.getX();
                float moveY = event.getY();
                Point TouchPoint = new Point((int) moveX, (int) moveY);
                int maxDistance = canOverflowRing ? lowerRing.getRadius() + upperRing.getRadius() : lowerRing.getRadius();
                // 计算摇杆实际偏移的位置
                Point currentPoint = getRockerPositionPoint(centerPoint, TouchPoint, maxDistance, upperRing.getRadius());
                updateRocker(event.getAction(), currentPoint);
                break;
            case MotionEvent.ACTION_UP:// 抬起
            case MotionEvent.ACTION_CANCEL:// 移出区域
                updateRocker(MotionEvent.ACTION_UP, centerPoint);
                break;
        }
        return true;
    }

    /**
     * 更新摇杆
     *
     * @param action       摇杆事件类型
     * @param currentPoint 当前位置
     */
    private void updateRocker(int action, Point currentPoint) {
        int offsetX = currentPoint.x - centerPoint.x;
        int offsetY = currentPoint.y - centerPoint.y;
        float ratioX, ratioY;

        if (offsetX != offsetPoint.x || offsetY != offsetPoint.y) {
            if (canOverflowRing) {
                ratioX = (float) offsetX / lowerRing.getRadius();
                ratioY = (float) offsetY / lowerRing.getRadius();
            } else {
                int oX = offsetX < 0 ? -1 : 1;
                int oY = offsetY < 0 ? -1 : 1;
                ratioX = (float) (upperRing.getRadius() * oX + offsetX) / lowerRing.getRadius();
                ratioY = (float) (upperRing.getRadius() * oY + offsetY) / lowerRing.getRadius();
            }

            // 设置偏移值
            offsetPoint.set(offsetX, offsetY);
            // 通知重新渲染
            invalidate();
            // 通知摇杆偏移监听器
            if (mOnRockerOffsetListener != null) {
                mOnRockerOffsetListener.onOffset(action, offsetX, offsetY, ratioX, ratioY);
            }
        }
    }

    /**
     * 获取摇杆实际要显示的位置（点）
     *
     * @param centerPoint  中心点
     * @param touchPoint   触摸点
     * @param regionRadius 摇杆可活动区域半径
     * @param rockerRadius 摇杆半径
     * @return 摇杆实际显示的位置（点）
     */
    private Point getRockerPositionPoint(Point centerPoint, Point touchPoint, float regionRadius, float rockerRadius) {
        // 两点在X轴的距离
        float lenX = (float) (touchPoint.x - centerPoint.x);
        // 两点在Y轴距离
        float lenY = (float) (touchPoint.y - centerPoint.y);
        // 两点距离
        float lenXY = (float) Math.sqrt(lenX * lenX + lenY * lenY);
        // 计算弧度
        double radian = Math.acos(lenX / lenXY) * (touchPoint.y < centerPoint.y ? -1 : 1);

        if (lenXY + rockerRadius <= regionRadius) {
            // 触摸位置在可活动范围内
            return touchPoint;
        } else {
            // 触摸位置在可活动范围外
            int showPointX = (int) (centerPoint.x + (regionRadius - rockerRadius) * Math.cos(radian));
            int showPointY = (int) (centerPoint.y + (regionRadius - rockerRadius) * Math.sin(radian));
            return new Point(showPointX, showPointY);
        }
    }
}
