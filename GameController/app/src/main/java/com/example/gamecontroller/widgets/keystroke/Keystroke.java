package com.example.gamecontroller.widgets.keystroke;

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

public class Keystroke extends View {
    public static final int KEY_TYPE_BUTTON = 0; // 普通按键
    public static final int KEY_TYPE_ROULETTE = 1; // 轮盘按键
    public static final int TOUCH_ACTION_DOWN = MotionEvent.ACTION_DOWN; // 触摸按下
    public static final int TOUCH_ACTION_MOVE = MotionEvent.ACTION_MOVE; // 触摸移动
    public static final int TOUCH_ACTION_UP = MotionEvent.ACTION_UP; // 触摸抬起
    public static final int DEFAULT_SIZE = 200; // view默认大小
    private int keyId; // 按键id
    private final Circular keyCircular = new Circular(); // 按键
    private Drawable keyBackground; // 按键背景
    private final Point startPoint = new Point(); // 触摸起始点
    private int touchAction; // 当前触摸动作
    private int keyType; // 按键类型
    private OnKeystrokeListener mOnKeystrokeListener; // 按键变化监听接口
    private OnSyncJoystickListener mOnSyncJoystickListener;// 同步摇杆监听器

    public Keystroke(Context context, int keyId, int keyType, Drawable keyBackground) {
        super(context);

        this.keyId = keyId;
        this.keyType = keyType;
        this.keyBackground = keyBackground;

        init();
    }

    public Keystroke(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.Keystroke); // 布局属性

        keyId = typedArray.getInteger(R.styleable.Keystroke_keyId, -1); // 按键id
        keyBackground = typedArray.getDrawable(R.styleable.Keystroke_keyBackground); // 按键背景

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
        if (keyBackground == null) {
            keyBackground = new ColorDrawable();
            ((ColorDrawable) keyBackground).setColor(Color.GRAY); // 按键默认背景
        }

        keyCircular.setBackground(keyBackground);
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
            int keyRadius = Math.min(cx, cy);// 轮盘的半径

            keyCircular.setRadius(keyRadius);
            keyCircular.setCenterPoint(cx, cy);
        }
    }

    @Override
    protected void onDraw(@NonNull Canvas canvas) {
        super.onDraw(canvas);

        keyCircular.draw(canvas);
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
                startPoint.set((int) touchX, (int) touchY);
                break;
            case MotionEvent.ACTION_MOVE:// 移动
                touchAction = TOUCH_ACTION_MOVE;
                break;
            case MotionEvent.ACTION_UP:// 抬起
            case MotionEvent.ACTION_CANCEL:// 取消
                touchAction = TOUCH_ACTION_UP;
                break;
        }

        if (keyType == KEY_TYPE_BUTTON) {
            if (touchAction != TOUCH_ACTION_MOVE && mOnKeystrokeListener != null) {
                mOnKeystrokeListener.onChange(keyId, touchAction, touchX, touchY);
            }
        } else {
            if (mOnSyncJoystickListener != null) {
                mOnSyncJoystickListener.onChange(keyId, getX() + touchX, getY() + touchY,
                        event.getAction(), touchX - startPoint.x, touchY - startPoint.y);
            }
        }

        return true;
    }

    /**
     * 设置按键监听器
     *
     * @param listener 监听器
     */
    public void setOnKeystrokeListener(OnKeystrokeListener listener) {
        mOnKeystrokeListener = listener;
    }

    /**
     * 设置同步摇杆监听器
     *
     * @param listener 监听器
     */
    public void setOnSyncJoystickListener(OnSyncJoystickListener listener) {
        mOnSyncJoystickListener = listener;
    }
}
