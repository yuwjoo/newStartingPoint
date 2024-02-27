package com.example.gamecontroller.widgets.controllerview;

import static com.example.gamecontroller.activitys.main.MainActivity.MAIN_TAG;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

public class ControllerView extends RelativeLayout {

    public ControllerView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        Log.v(MAIN_TAG, "父级测量：" + "width:" + widthSize + "--" + widthMode + " height:" + heightSize + "--" + heightMode);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {

    }
}
