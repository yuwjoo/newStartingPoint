package com.example.gamecontroller.activitys.gamecontroller;

import static com.example.gamecontroller.activitys.main.MainActivity.MAIN_TAG;

import android.annotation.SuppressLint;
import android.graphics.Point;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.Switch;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;

import com.example.gamecontroller.R;
import com.example.gamecontroller.activitys.gamecontroller.function.controllerviewmanager.ControllerViewManager;
import com.example.gamecontroller.activitys.gamecontroller.function.controllerviewmanager.KeyConfig;
import com.example.gamecontroller.activitys.main.functions.ConnectLocalSocket;
import com.example.gamecontroller.activitys.main.functions.messagemanager.MessageManager;
import com.example.gamecontroller.widgets.controllerview.ControllerView;

public class GameControllerActivity extends AppCompatActivity {
    private ControllerViewManager controllerViewManager; // 控制器视图管理器

    @SuppressLint("UseSwitchCompatOrMaterialCode")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_controller);

        ControllerView controllerView = findViewById(R.id.controller_view);
        Switch mSwitch = findViewById(R.id.sw_demo);
        mSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            controllerView.setEdit(isChecked);
        });

        ControllerViewManager keyConfig = new ControllerViewManager(2);
        Log.v(MAIN_TAG, keyConfig.getType() + "");
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