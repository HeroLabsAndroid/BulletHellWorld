package com.example.bullethellworld.activities;

import android.content.Context;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Handler;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.FragmentManager;

import com.example.bullethellworld.DialogDismissedListener;
import com.example.bullethellworld.GameOverDialog;
import com.example.bullethellworld.R;
import com.example.bullethellworld.views.JoyconView;
import com.example.bullethellworld.views.PlayingFieldView;

import java.util.Locale;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity implements JoyconView.JoyconListener, PlayingFieldView.GameOverListener, DialogDismissedListener {



    JoyconView joyconViewCtrl, joyconViewBullet;
    PlayingFieldView playingFieldView;

    Button btnDebug;
    TextView tvScore;

    int widthMeasureSpec, heightMeasureSpec;

// GAME PARAMS //
    float[] playerPos = new float[2];
    public static boolean paused = false;
    public static int score = 0;
//------------------------------------//

// Clock //
    final Handler handler = new Handler();
    Timer timer = new Timer(false);
    TimerTask timerTask = new TimerTask() {
        @Override
        public void run() {
            handler.post(() -> {
                    update_ui();
                    score += playingFieldView.bulletCount();
                    playingFieldView.getPlayer().cooldown--;
                    if(new Random().nextFloat()<(0.075/(float)(playingFieldView.bulletCount()/2))) playingFieldView.spawnBullet();
                }
            );
        }
    };

//------------------------------------//

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

        joyconViewCtrl = findViewById(R.id.JV_joycon);
        joyconViewBullet = findViewById(R.id.JV_bullet);
        playingFieldView = findViewById(R.id.PV_player);
        btnDebug = findViewById(R.id.btnDebug);
        tvScore = findViewById(R.id.TV_score);

        joyconViewCtrl.setIsCtrl(true);
        joyconViewBullet.setIsCtrl(false);

        btnDebug.setOnClickListener(v -> playingFieldView.spawnBullet());

        playingFieldView.setGameOverListener(this);


        timer.scheduleAtFixedRate(timerTask, 10, 10); // 1000 = 1 second.
    }

    @Override
    protected void onResume() {
        super.onResume();

        WindowManager wm =
                (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();

        int deviceWidth, deviceHeight;

        Point size = new Point();
        display.getSize(size);
        deviceWidth = size.x;
        deviceHeight = size.y;

        widthMeasureSpec = View.MeasureSpec.makeMeasureSpec(deviceWidth, View.MeasureSpec.EXACTLY);
        heightMeasureSpec = View.MeasureSpec.makeMeasureSpec(deviceHeight, View.MeasureSpec.EXACTLY);
    }

    public void update_ui() {
        if(!paused) {
            playingFieldView.movePlayer(playerPos[0], playerPos[1]);
            playingFieldView.moveBullets(1+(score/100000f));

            playingFieldView.check_blt_plblt_coll();
            playingFieldView.invalidate();
            tvScore.setText(String.format(Locale.getDefault(),"%d",score/100));
        }
    }

    @Override
    public void onJoyconMoved(float[] coord, boolean ctrl) {
        if(ctrl)
            playerPos = coord;
        else {
            playingFieldView.getPlayer().fire(coord);
        }
    }

    @Override
    public void gameOver() {
        paused = true;
        GameOverDialog goDial = new GameOverDialog(this, score, this);
        FragmentManager fragMan = getSupportFragmentManager();
        goDial.show(fragMan, "gameover");
    }

    @Override
    public void onDialogDismissed() {
        score = 0;
        playingFieldView.reset();
        paused = false;
    }
}