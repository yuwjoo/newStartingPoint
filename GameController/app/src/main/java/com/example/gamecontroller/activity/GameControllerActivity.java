package com.example.gamecontroller.activity;

import static com.example.gamecontroller.activity.MainActivity.MAIN_TAG;

import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;

import com.example.gamecontroller.R;
import com.example.gamecontroller.components.GameRocker;
import com.example.gamecontroller.connect.ConnectLocalSocket;
import com.example.gamecontroller.manager.MessageManager;

public class GameControllerActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_controller);

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