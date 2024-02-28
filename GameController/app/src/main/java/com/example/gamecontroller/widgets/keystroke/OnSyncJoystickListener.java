package com.example.gamecontroller.widgets.keystroke;

import android.view.MotionEvent;

/**
 * 同步轮盘的监听接口
 */
public interface OnSyncJoystickListener {
    /**
     * 同步轮盘改变
     *
     * @param keyId  按键id
     * @param centerX  中心坐标x
     * @param centerY  中心坐标y
     * @param action  触摸动作
     * @param touchX  触摸x坐标
     * @param touchY  触摸y坐标
     */
    void onChange(int keyId, float centerX, float centerY, int action, float touchX, float touchY);
}
