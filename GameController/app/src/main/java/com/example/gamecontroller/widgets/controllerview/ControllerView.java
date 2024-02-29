package com.example.gamecontroller.widgets.controllerview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Point;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.NonNull;

import java.util.ArrayList;

public class ControllerView extends View {
    ArrayList<VirtualKey> keyList = new ArrayList<>(20);

    public ControllerView(Context context, AttributeSet attrs) {
        super(context, attrs);

        SkillWheelKey skillWheelKey = new SkillWheelKey(10, 300, 500, 500, 500, 120,230);
        keyList.add(skillWheelKey);
    }

    @Override
    protected void onDraw(@NonNull Canvas canvas) {
        super.onDraw(canvas);
        keyList.get(0).draw(canvas);
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
                break;
            case MotionEvent.ACTION_MOVE:// 移动
                break;
            case MotionEvent.ACTION_UP:// 抬起
            case MotionEvent.ACTION_CANCEL:// 取消
                break;
        }

        keyList.get(0).touch(event.getActionMasked(), (int) event.getX(), (int) event.getY());

        invalidate();

        return true;
    }
}
