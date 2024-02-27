package com.example.gamecontroller.activitys.gamecontroller;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;

import com.example.gamecontroller.R;
import com.example.gamecontroller.activitys.main.functions.ConnectLocalSocket;
import com.example.gamecontroller.activitys.main.functions.messagemanager.MessageManager;
import com.example.gamecontroller.widgets.gamerocker.GameRocker;

public class GameControllerActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_controller);

//        RelativeLayout gameControllerLayout = findViewById(R.id.game_controller_layout);
//
//        GameRocker gameRocker = gameControllerLayout.findViewById(R.id.game_rocker_btn);
//        gameRocker.setVisibility(View.GONE);

//        getWindow().getDecorView().findViewById(android.R.id.content); // 获取布局实例（未验证）


//        GameRocker gameRocker = findViewById(R.id.game_rocker_btn);
//        gameRocker.setOnRockerOffsetListener((action, offsetX, offsetY, ratioX, ratioY) -> {
//            Log.v(MAIN_TAG, action + " -- " + ratioX + " -- " + ratioY);
//        });
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