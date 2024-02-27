package com.example.gamecontroller.widgets.joystick;

/**
 * 摇杆变化的监听接口
 */
public interface OnJoystickListener {
    /**
     * 摇杆变化
     *
     * @param action  摇杆事件类型
     * @param ratioX  x偏移比例
     * @param ratioY  y偏移比例
     */
    void onOffset(int action, float ratioX, float ratioY);
}
