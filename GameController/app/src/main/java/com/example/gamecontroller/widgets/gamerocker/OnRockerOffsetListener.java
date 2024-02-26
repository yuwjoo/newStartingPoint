package com.example.gamecontroller.widgets.gamerocker;

/**
 * 摇杆偏移的监听接口
 */
public interface OnRockerOffsetListener {
    /**
     * 摇杆偏移变化
     *
     * @param action  摇杆事件类型
     * @param offsetX x偏移距离
     * @param offsetY y偏移距离
     * @param ratioX  x偏移比例
     * @param ratioY  y偏移比例
     */
    void onOffset(int action, int offsetX, int offsetY, float ratioX, float ratioY);
}