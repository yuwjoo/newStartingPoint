package com.example.gamecontroller.activitys.gamecontroller.function.controllerviewmanager;

import static com.example.gamecontroller.activitys.main.MainActivity.MAIN_TAG;

import android.util.Log;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.example.gamecontroller.widgets.joystick.Joystick;
import com.example.gamecontroller.widgets.joystick.OnJoystickListener;
import com.example.gamecontroller.widgets.keystroke.Keystroke;
import com.example.gamecontroller.widgets.keystroke.OnKeystrokeListener;
import com.example.gamecontroller.widgets.keystroke.OnSyncJoystickListener;

import java.util.ArrayList;
import java.util.List;

public class ControllerViewManager {
    private final int START_ID = 10; // 起始id值
    private final RelativeLayout controllerLayout; // 控制器布局容器
    private final List<KeyConfig> keyConfigList = new ArrayList<>(); // 按键配置列表
    private Joystick defaultJoystick; // 默认摇杆
    private final OnJoystickListener onJoystickListener; // 摇杆变化监听器
    private final OnKeystrokeListener onKeystrokeListener; // 按键变化监听器
    private final OnSyncJoystickListener onSyncJoystickListener; // 同步轮盘监听器

    public ControllerViewManager(RelativeLayout layout) {
        controllerLayout = layout;

        onJoystickListener = (keyId, action, offsetX, offsetY) -> {
            Log.v(MAIN_TAG, keyId + " -- " + action + " -- " + offsetX + " -- " + offsetY);
        };

        onKeystrokeListener = (keyId, action, touchX, touchY) -> {
            Log.v(MAIN_TAG, keyId + " -- " + action);
        };

        onSyncJoystickListener = (keyId, centerX, centerY, action, offsetX, offsetY) -> {
            switch (action) {
                case MotionEvent.ACTION_DOWN:
                    defaultJoystick = addJoystick(keyId, (int) centerX, (int) centerY, 300, 300, true, 0.5f);
                    break;
                case MotionEvent.ACTION_UP:
                    controllerLayout.removeView(defaultJoystick);
                    break;
            }
            defaultJoystick.triggerTouchEvent(action, offsetX, offsetY);
        };
    }

    /**
     * 添加按键配置
     *
     * @param keyConfig 配置对象
     */
    public void addKeyConfig(KeyConfig keyConfig) {
        switch (keyConfig.type) {
            case KeyConfig.TYPE_JOYSTICK:
                addJoystick(keyConfig.keyId, keyConfig.x, keyConfig.y, keyConfig.size, keyConfig.size, keyConfig.isRockerOverflow, keyConfig.rockerScale);
                break;
            case KeyConfig.TYPE_KEYSTROKE:
                addKeystroke(keyConfig.keyId, keyConfig.x, keyConfig.y, keyConfig.size, keyConfig.size, Keystroke.KEY_TYPE_BUTTON);
                break;
            case KeyConfig.TYPE_JOYSTICK_AND_KEYSTROKE:
                addKeystroke(keyConfig.keyId, keyConfig.x, keyConfig.y, keyConfig.size, keyConfig.size, Keystroke.KEY_TYPE_ROULETTE);
                break;
        }
    }

    /**
     * 添加摇杆
     *
     * @param keyId            按键id
     * @param x                x坐标
     * @param y                y坐标
     * @param width            宽度
     * @param height           高度
     * @param isRockerOverflow 摇杆是否可以超出轮盘
     * @param rockerScale      摇杆缩放比例
     */
    private Joystick addJoystick(int keyId, int x, int y, int width, int height, boolean isRockerOverflow, float rockerScale) {
        Joystick joystick = new Joystick(controllerLayout.getContext(), keyId, null, null, isRockerOverflow, rockerScale);
        joystick.setOnJoystickListener(onJoystickListener);
        ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(width, height);
        joystick.setLayoutParams(layoutParams);
        joystick.setX(x - (float) width / 2);
        joystick.setY(y - (float) height / 2);
        controllerLayout.addView(joystick);
        return joystick;
    }

    /**
     * 添加按键
     *
     * @param keyId   按键id
     * @param x       x坐标
     * @param y       y坐标
     * @param width   宽度
     * @param height  高度
     * @param keyType 按键类型
     */
    private Keystroke addKeystroke(int keyId, int x, int y, int width, int height, int keyType) {
        Keystroke keystroke = new Keystroke(controllerLayout.getContext(), keyId, keyType, null);
        keystroke.setOnKeystrokeListener(onKeystrokeListener);
        keystroke.setOnSyncJoystickListener(onSyncJoystickListener);
        ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(width, height);
        keystroke.setLayoutParams(layoutParams);
        keystroke.setX(x - (float) width / 2);
        keystroke.setY(y - (float) height / 2);
        controllerLayout.addView(keystroke);
        return keystroke;
    }

    /**
     * 获取id
     *
     * @return id值
     */
    private int getNextId() {
        int size = keyConfigList.size();
        int max = START_ID - 1;
        for (int i = 0; i < size; i++) {
            max = Math.max(keyConfigList.get(i).keyId, max);
        }
        return max + 1;
    }
}
