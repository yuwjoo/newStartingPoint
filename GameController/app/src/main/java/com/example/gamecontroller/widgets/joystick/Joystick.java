package com.example.gamecontroller.widgets.joystick;

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

public class Joystick extends View {
    public static final int TOUCH_ACTION_DOWN = MotionEvent.ACTION_DOWN; // 触摸按下
    public static final int TOUCH_ACTION_MOVE = MotionEvent.ACTION_MOVE; // 触摸移动
    public static final int TOUCH_ACTION_UP = MotionEvent.ACTION_UP; // 触摸抬起
    public static final int DEFAULT_SIZE = 400; // view默认大小
    private int keyId; // 按键id
    private final Circular rouletteCircular = new Circular(); // 轮盘
    private final Circular rockerCircular = new Circular(); // 摇杆
    private Drawable rouletteBackground; // 轮盘背景
    private Drawable rockerBackground; // 摇杆背景
    private final boolean isRockerOverflow; // 摇杆是否可以超出轮盘
    private final float rockerScale; // 摇杆缩放值（基于轮盘）
    private int touchAction; // 当前触摸动作
    private int maxDistance; // 摇杆移动的最远距离
    private OnJoystickListener mOnJoystickListener; // 摇杆监听器

    public Joystick(Context context, int keyId, Drawable rouletteBackground, Drawable rockerBackground, boolean isRockerOverflow, float rockerScale) {
        super(context);

        this.keyId = keyId;
        this.rouletteBackground = rouletteBackground;
        this.rockerBackground = rockerBackground;
        this.isRockerOverflow = isRockerOverflow;
        this.rockerScale = rockerScale;

        init();
    }

    public Joystick(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.Joystick); // 布局属性

        keyId = typedArray.getInteger(R.styleable.Joystick_keyId, -1); // id
        rouletteBackground = typedArray.getDrawable(R.styleable.Joystick_rouletteBackground); // 轮盘背景
        rockerBackground = typedArray.getDrawable(R.styleable.Joystick_rockerBackground); // 摇杆背景
        isRockerOverflow = typedArray.getBoolean(R.styleable.Joystick_isRockerOverflow, true);
        rockerScale = typedArray.getFloat(R.styleable.Joystick_rockerScale, 0.5f);

        typedArray.recycle();

        init();
    }

    public int getKeyId() {
        return keyId;
    }

    public void setKeyId(int keyId) {
        this.keyId = keyId;
    }

    /**
     * 初始化
     */
    private void init() {
        if (rouletteBackground == null) {
            rouletteBackground = new ColorDrawable();
            ((ColorDrawable) rouletteBackground).setColor(Color.GRAY); // 轮盘背景默认背景
        }

        if (rockerBackground == null) {
            rockerBackground = new ColorDrawable();
            ((ColorDrawable) rockerBackground).setColor(Color.WHITE); // 摇杆背景默认背景
        }

        rouletteCircular.setBackground(rouletteBackground);
        rockerCircular.setBackground(rockerBackground);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int measureWidth, measureHeight;

        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

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
            int rouletteRadius, rockerRadius, maxDistance;

            if (isRockerOverflow) {
                rouletteRadius = (int) (Math.min(cx, cy) / (rockerScale + 1));// 轮盘的半径
                rockerRadius = (int) (rouletteRadius * rockerScale);// 摇杆的半径
                maxDistance = rouletteRadius;
            } else {
                rouletteRadius = Math.min(cx, cy);// 轮盘的半径
                rockerRadius = (int) (rouletteRadius * rockerScale);// 摇杆的半径
                maxDistance = rouletteRadius - rockerRadius;
            }

            rouletteCircular.setRadius(rouletteRadius);
            rockerCircular.setRadius(rockerRadius);

            rouletteCircular.setCenterPoint(cx, cy);
            rockerCircular.setCenterPoint(cx, cy);

            this.maxDistance = maxDistance;
        }
    }

    @Override
    protected void onDraw(@NonNull Canvas canvas) {
        super.onDraw(canvas);

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
                touchAction = TOUCH_ACTION_DOWN;
                updateRocker(rouletteCircular.getCenterPoint());
            case MotionEvent.ACTION_MOVE:// 移动
                touchAction = TOUCH_ACTION_MOVE;
                Point startPoint = rouletteCircular.getCenterPoint();
                Point currentPoint = getRockerPositionPoint(startPoint.x, startPoint.y, (int) touchX, (int) touchY, maxDistance); // 计算摇杆实际偏移的位置
                updateRocker(currentPoint);
                break;
            case MotionEvent.ACTION_UP:// 抬起
            case MotionEvent.ACTION_CANCEL:// 取消
                touchAction = TOUCH_ACTION_UP;
                updateRocker(rouletteCircular.getCenterPoint());
                break;
        }
        return true;
    }

    /**
     * 触发触摸事件
     *
     * @param action  触摸动作
     * @param offsetX 触摸偏移x坐标
     * @param offsetY 触摸偏移y坐标
     */
    public void triggerTouchEvent(int action, float offsetX, float offsetY) {
        switch (action) {
            case MotionEvent.ACTION_DOWN:// 按下
                performClick();
                touchAction = TOUCH_ACTION_DOWN;
                updateRocker(rouletteCircular.getCenterPoint());
            case MotionEvent.ACTION_MOVE:// 移动
                touchAction = TOUCH_ACTION_MOVE;
                Point startPoint = rouletteCircular.getCenterPoint();
                Point currentPoint = getRockerPositionPoint(startPoint.x, startPoint.y, (int) (startPoint.x + offsetX), (int) (startPoint.y + offsetY), maxDistance); // 计算摇杆实际偏移的位置
                updateRocker(currentPoint);
                break;
            case MotionEvent.ACTION_UP:// 抬起
            case MotionEvent.ACTION_CANCEL:// 取消
                touchAction = TOUCH_ACTION_UP;
                updateRocker(rouletteCircular.getCenterPoint());
                break;
        }
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
     * @param startX      起始点X
     * @param startY      起始点Y
     * @param endX        结束点X
     * @param endY        结束点Y
     * @param maxDistance 起始点和结束点的最大距离
     * @return 摇杆实际显示的位置（点）
     */
    private Point getRockerPositionPoint(int startX, int startY, int endX, int endY, float maxDistance) {
        float lenX = (float) (endX - startX); // 两点在X轴的距离
        float lenY = (float) (endY - startY);// 两点在Y轴距离
        float lenXY = (float) Math.sqrt(lenX * lenX + lenY * lenY); // 两点距离
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

    /**
     * 更新摇杆
     *
     * @param movePoint 移动位置
     */
    private void updateRocker(Point movePoint) {
        Point currentPoint = rockerCircular.getCenterPoint();
        Point centerPoint = rouletteCircular.getCenterPoint();

        if (touchAction == TOUCH_ACTION_DOWN || touchAction == TOUCH_ACTION_UP || movePoint.x != currentPoint.x || movePoint.y != currentPoint.y) {
            float offsetX = movePoint.x - centerPoint.x;
            float offsetY = movePoint.y - centerPoint.y;

            rockerCircular.setCenterPoint(movePoint.x, movePoint.y); // 设置摇杆中心点

            invalidate(); // 通知重新渲染

            if (mOnJoystickListener != null) {
                // 通知摇杆监听器
                mOnJoystickListener.onChange(keyId, touchAction, offsetX, offsetY);
            }
        }
    }
}
