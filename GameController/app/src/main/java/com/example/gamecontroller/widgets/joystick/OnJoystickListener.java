package com.example.gamecontroller.widgets.joystick;

/**
 * 摇杆变化的监听接口
 */
public interface OnJoystickListener {
    /**
     * 摇杆变化
     *
     * @param keyId  摇杆id
     * @param action  摇杆事件类型
     * @param offsetX  x偏移
     * @param offsetY  y偏移
     */
    void onChange(int keyId, int action, float offsetX, float offsetY);
}
