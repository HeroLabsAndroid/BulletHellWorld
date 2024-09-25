package com.example.bullethellworld.activities;

import android.content.Context;
import android.graphics.Point;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.bullethellworld.R;
import com.example.bullethellworld.views.JoyconView;
import com.example.bullethellworld.views.PlayerView;

import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity implements JoyconView.JoyconListener{

    JoyconView joyconView;
    PlayerView playerView;


    int widthMeasureSpec, heightMeasureSpec;

    float[] playerPos = new float[2];

    final Handler handler = new Handler();
    Timer timer = new Timer(false);
    TimerTask timerTask = new TimerTask() {
        @Override
        public void run() {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    update_ui();
                }
            });
        }
    };



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        joyconView = findViewById(R.id.JV_joycon);
        playerView = findViewById(R.id.PV_player);


        timer.scheduleAtFixedRate(timerTask, 20, 20); // 1000 = 1 second.
    }

    @Override
    protected void onResume() {
        super.onResume();

        WindowManager wm =
                (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();

        int deviceWidth, deviceHeight;

        if(android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2){
            Point size = new Point();
            display.getSize(size);
            deviceWidth = size.x;
            deviceHeight = size.y;
        } else {
            deviceWidth = display.getWidth();
            deviceHeight = display.getHeight();
        }

        widthMeasureSpec = View.MeasureSpec.makeMeasureSpec(deviceWidth, View.MeasureSpec.EXACTLY);
        heightMeasureSpec = View.MeasureSpec.makeMeasureSpec(deviceHeight, View.MeasureSpec.EXACTLY);
    }

    public void update_ui() {
        playerView.move(playerPos[0], playerPos[1]);
    }

    @Override
    public void onJoyconMoved(float[] coord) {
        playerPos = coord;
    }
}