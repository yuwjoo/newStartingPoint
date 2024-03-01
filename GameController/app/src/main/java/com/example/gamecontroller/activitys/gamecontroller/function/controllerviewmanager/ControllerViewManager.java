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

public class ControllerViewManager extends KeyConfig {

    public ControllerViewManager(int t) {
        super(t);
    }

}
