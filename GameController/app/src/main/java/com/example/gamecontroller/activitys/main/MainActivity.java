package com.example.gamecontroller.activitys.main;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.gamecontroller.R;
import com.example.gamecontroller.activitys.gamecontroller.GameControllerActivity;
import com.example.gamecontroller.activitys.main.functions.ConnectLocalSocket;

import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {

    public static final String MAIN_TAG = MainActivity.class.getSimpleName(); // 日志标识
    private Button m_game_controller_btn; // 游戏控制器按钮
    private TextView m_connect_state_text; // 连接状态文本
    private Timer timer; // 更新连接状态的定时器

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        m_game_controller_btn = findViewById(R.id.game_controller_btn);
        m_connect_state_text = findViewById(R.id.connect_state_tv);

        updateConnectState();

        m_game_controller_btn.setOnClickListener(view -> {
            Intent intent = new Intent(this, GameControllerActivity.class);
            startActivity(intent);
        });

        ConnectLocalSocket.start(); // 开启本地socket连接
    }

    @Override
    protected void onPause() {
        super.onPause();
        timer.cancel();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        updateConnectState();
    }

    /**
     * 更新连接状态
     */
    private void updateConnectState() {
        timer = new Timer();
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(() -> {
                    m_connect_state_text.setText(ConnectLocalSocket.isConnect() ? "连接成功" : "未连接");
                });
            }
        };
        timer.schedule(task, 0, 1000);
    }

}