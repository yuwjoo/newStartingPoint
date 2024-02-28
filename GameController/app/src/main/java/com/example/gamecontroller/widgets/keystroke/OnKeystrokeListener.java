package com.example.gamecontroller.widgets.keystroke;

/**
 * 按键变化的监听接口
 */
public interface OnKeystrokeListener {
    /**
     * 按键变化
     *
     * @param keyId  按键id
     * @param action  事件类型
     * @param offsetX  x触摸偏移坐标
     * @param offsetY  y触摸偏移坐标
     */
    void onChange(int keyId, int action, float offsetX, float offsetY);
}
