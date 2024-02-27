package com.example.gamecontroller.widgets.joystick;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.gamecontroller.R;

public class Joystick extends View {
    public static final int TOUCH_ACTION_DOWN = MotionEvent.ACTION_DOWN; // 触摸按下
    public static final int TOUCH_ACTION_MOVE = MotionEvent.ACTION_MOVE; // 触摸移动
    public static final int TOUCH_ACTION_UP = MotionEvent.ACTION_UP; // 触摸抬起
    public static final int DEFAULT_SIZE = 400; // view默认大小
    private final Circular triggerCircular; // 触发区
    private final Circular rouletteCircular; // 轮盘
    private final Circular rockerCircular; // 摇杆
    private boolean isRockerOverflow; // 摇杆是否可以超出轮盘
    private float rockerScale; // 摇杆缩放值（基于轮盘）
    private int touchAction; // 当前触摸动作
    private OnJoystickListener mOnJoystickListener; // 摇杆监听器

    public Joystick(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.Joystick); // 布局属性
        Drawable triggerBackground = typedArray.getDrawable(R.styleable.Joystick_triggerBackground); // 触发区背景
        Drawable rouletteBackground = typedArray.getDrawable(R.styleable.Joystick_rouletteBackground); // 轮盘背景
        Drawable rockerBackground = typedArray.getDrawable(R.styleable.Joystick_rockerBackground); // 摇杆背景

        if (triggerBackground == null) {
            triggerBackground = new ColorDrawable();
            ((ColorDrawable) triggerBackground).setColor(Color.TRANSPARENT); // 触发区背景默认背景
        }

        if (rouletteBackground == null) {
            rouletteBackground = new ColorDrawable();
            ((ColorDrawable) rouletteBackground).setColor(Color.GRAY); // 轮盘背景默认背景
        }

        if (rockerBackground == null) {
            rockerBackground = new ColorDrawable();
            ((ColorDrawable) rockerBackground).setColor(Color.WHITE); // 摇杆背景默认背景
        }

        triggerCircular = new Circular();
        triggerCircular.setBackground(triggerBackground);

        rouletteCircular = new Circular();
        rouletteCircular.setBackground(rouletteBackground);

        rockerCircular = new Circular();
        rockerCircular.setBackground(rockerBackground);

        isRockerOverflow = typedArray.getBoolean(R.styleable.Joystick_isRockerOverflow, true);
        rockerScale = typedArray.getFloat(R.styleable.Joystick_rockerScale, 0.5f);

        centerPoint = new Point();

        typedArray.recycle();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int measureWidth, measureHeight;

        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

//        Log.v(MAIN_TAG, "子级测量：" + "width:" + widthSize + "--" + widthMode + " height:" + heightSize + "--" + heightMode);

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
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);

        if (changed) {
            int measuredWidth = getMeasuredWidth();
            int measuredHeight = getMeasuredHeight();

            int cx = measuredWidth / 2;
            int cy = measuredHeight / 2;
            int triggerRadius, rouletteRadius, rockerRadius;

            if (isRockerOverflow) {
                rouletteRadius = (int) (Math.min(cx, cy) / rockerScale + 1);// 轮盘的半径
            } else {
                rouletteRadius = Math.min(cx, cy);// 轮盘的半径
            }

            triggerRadius = rouletteRadius; // 触发区的半径
            rockerRadius = (int) (rouletteRadius * rockerScale);// 摇杆的半径

            triggerCircular.setRadius(triggerRadius);
            rouletteCircular.setRadius(rouletteRadius);
            rockerCircular.setRadius(rockerRadius);

            triggerCircular.setCenterPoint(cx, cy);
            rouletteCircular.setCenterPoint(cx, cy);
            rockerCircular.setCenterPoint(cx, cy);
        }
    }

    @Override
    protected void onDraw(@NonNull Canvas canvas) {
        super.onDraw(canvas);

        triggerCircular.draw(canvas);
        rouletteCircular.draw(canvas);
        rockerCircular.draw(canvas);
    }

    @Override
    public boolean performClick() {
        return super.performClick();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float touchX = event.getX();
        float touchY = event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:// 按下
                performClick();
                if (triggerCircular.isTouchRect((int) touchX, (int) touchY)) {
                    touchAction = TOUCH_ACTION_DOWN;
                    updateRocker(rouletteCircular.getCenterPoint());
                }
                break;
            case MotionEvent.ACTION_MOVE:// 移动
                if (touchAction == TOUCH_ACTION_DOWN || touchAction == TOUCH_ACTION_MOVE) {
                    touchAction = TOUCH_ACTION_MOVE;
                    Point TouchPoint = new Point((int) touchX, (int) touchY);
                    int maxDistance = isRockerOverflow ? rouletteCircular.getRadius() + rockerCircular.getRadius() : rouletteCircular.getRadius();
                    // 计算摇杆实际偏移的位置
                    Point currentPoint = getRockerPositionPoint(rouletteCircular.getCenterPoint(), TouchPoint, maxDistance, rockerCircular.getRadius());
                    updateRocker(currentPoint);
                }
                break;
            case MotionEvent.ACTION_UP:// 抬起
            case MotionEvent.ACTION_CANCEL:// 取消
                if (touchAction == TOUCH_ACTION_DOWN || touchAction == TOUCH_ACTION_MOVE) {
                    touchAction = TOUCH_ACTION_UP;
                    updateRocker(rouletteCircular.getCenterPoint());
                }
                break;
        }
        return true;
    }

    /**
     * 设置摇杆监听器
     *
     * @param listener 监听器
     */
    public void setOnJoystickListener(OnJoystickListener listener) {
        mOnJoystickListener = listener;
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

    /**
     * 更新摇杆
     *
     * @param movePoint 移动位置
     */
        private void updateRocker(Point movePoint) {
            Point currentPoint = rockerCircular.getCenterPoint();
            if (movePoint.x != currentPoint.x || movePoint.y != currentPoint.y) {
                if (canOverflowRing) {
                    ratioX = (float) offsetX / lowerRing.getRadius();
                    ratioY = (float) offsetY / lowerRing.getRadius();
                } else {
                    int oX = offsetX < 0 ? -1 : 1;
                    int oY = offsetY < 0 ? -1 : 1;
                    ratioX = (float) (upperRing.getRadius() * oX + offsetX) / lowerRing.getRadius();
                    ratioY = (float) (upperRing.getRadius() * oY + offsetY) / lowerRing.getRadius();
                }
            }

        int offsetX = movePoint.x - centerPoint.x;
        int offsetY = movePoint.y - centerPoint.y;
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
}
