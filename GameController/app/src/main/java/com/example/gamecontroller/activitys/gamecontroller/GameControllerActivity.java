package com.example.gamecontroller.activitys.gamecontroller;

import android.graphics.Point;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;

import com.example.gamecontroller.R;
import com.example.gamecontroller.activitys.gamecontroller.function.controllerviewmanager.ControllerViewManager;
import com.example.gamecontroller.activitys.gamecontroller.function.controllerviewmanager.KeyConfig;
import com.example.gamecontroller.activitys.main.functions.ConnectLocalSocket;
import com.example.gamecontroller.activitys.main.functions.messagemanager.MessageManager;

public class GameControllerActivity extends AppCompatActivity {
    private ControllerViewManager controllerViewManager; // 控制器视图管理器

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_controller);

//        ViewGroup contentViewGroup = getWindow().getDecorView().findViewById(android.R.id.content); // 获取content布局
//        RelativeLayout layout = (RelativeLayout) contentViewGroup.getChildAt(0); // 获取根布局
//
//        controllerViewManager = new ControllerViewManager(layout);
//
//        KeyConfig keyConfig = new KeyConfig(10, KeyConfig.TYPE_JOYSTICK, 200, 500, 300,
//                false, 0.5f, new Point());
//        KeyConfig keyConfig1 = new KeyConfig(11, KeyConfig.TYPE_JOYSTICK_AND_KEYSTROKE, 800, 500, 100,
//                false, 0.5f, new Point());
//        KeyConfig keyConfig2 = new KeyConfig(11, KeyConfig.TYPE_KEYSTROKE, 1000, 600, 100,
//                false, 0.5f, new Point());
//
//        controllerViewManager.addKeyConfig(keyConfig);
//        controllerViewManager.addKeyConfig(keyConfig1);
//        controllerViewManager.addKeyConfig(keyConfig2);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        WindowInsetsControllerCompat windowInsetsController = WindowCompat.getInsetsController(getWindow(), getWindow().getDecorView());
        // Configure the behavior of the hidden system bars.
        windowInsetsController.setSystemBarsBehavior(
                WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        );
        windowInsetsController.hide(WindowInsetsCompat.Type.systemBars());
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (ConnectLocalSocket.isConnect()) {
            ConnectLocalSocket.send(MessageManager.createTouchByte(event));
        }
        return super.onTouchEvent(event);
    }
}