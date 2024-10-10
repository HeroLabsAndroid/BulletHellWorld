package com.example.bullethellworld.activities;

import android.content.Context;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageButton;
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
import com.example.bullethellworld.views.Bullet;
import com.example.bullethellworld.views.JoyconView;
import com.example.bullethellworld.views.PlayingFieldView;

import java.util.Locale;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity implements JoyconView.JoyconListener, PlayingFieldView.GameOverListener, DialogDismissedListener {



    JoyconView joyconViewCtrl, joyconViewBullet;
    PlayingFieldView playingFieldView;

    TextView tvScore;

    int widthMeasureSpec, heightMeasureSpec;

    ImageButton btnPause;

    FragmentManager fragMan = getSupportFragmentManager();
    private static MainActivity activity_ref;

// GAME PARAMS //
    float[] playerPos = new float[2];
    public static boolean paused = true;
    public static boolean ingame = false;
    public static int score = 0;
//------------------------------------//

// Clock //
    final Handler handler = new Handler();
    Timer goTim = new Timer();
    Timer timer = new Timer(false);
    TimerTask timerTask = new TimerTask() {
        @Override
        public void run() {
            handler.post(() -> {
                    if(!paused && ingame) {
                        update_ui();
                        score += playingFieldView.getEnemy().bulletCount()*4;
                        for(Bullet b: playingFieldView.getEnemy().getBullets()) {
                            score += b.getBullets().size();
                        }
                        playingFieldView.getPlayer().cooldown--;
                    }
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

        activity_ref = this;
        joyconViewCtrl = findViewById(R.id.JV_joycon);
        joyconViewBullet = findViewById(R.id.JV_bullet);
        playingFieldView = findViewById(R.id.PV_player);
        tvScore = findViewById(R.id.TV_score);
        tvScore.setText("HOI!");

        btnPause = findViewById(R.id.BTN_pause);
        btnPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                paused = !paused;
            }
        });

        joyconViewCtrl.setIsCtrl(true);
        joyconViewBullet.setIsCtrl(false);


        playingFieldView.setGameOverListener(this);
        playingFieldView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!ingame) {
                    Log.d("BOOP", "beep boop");
                    playingFieldView.reset();
                    ingame = true;
                    paused = false;
                }
            }
        });
        goTim.purge();

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
        playingFieldView.movePlayer(playerPos[0], playerPos[1]);
        playingFieldView.moveEnemy(1+score/100000f);


        playingFieldView.check_blt_plblt_coll();
        playingFieldView.invalidate();
        tvScore.setText(String.format(Locale.getDefault(),"%d",score/100));
    }

    @Override
    public void onJoyconMoved(float[] coord, boolean ctrl) {
        if(ctrl)
            playerPos = coord;
        else {
            playingFieldView.getPlayer().fire(coord);
        }
    }

    private void show_gameover_dialog(String msg) {
        Log.d("GAme OVER!", msg);
        GameOverDialog goDial = new GameOverDialog(this, score, this);
        goDial.show(activity_ref.getSupportFragmentManager(), "gameover");
    }

    @Override
    public void gameOver(String msg) {
        paused = true;

        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                show_gameover_dialog(msg);
            }
        }, 420);

    }

    @Override
    public void onDialogDismissed() {


        score = 0;
        tvScore.setText("HOI!");
        playingFieldView.reset();
        ingame = false;
        playingFieldView.invalidate();
    }
}