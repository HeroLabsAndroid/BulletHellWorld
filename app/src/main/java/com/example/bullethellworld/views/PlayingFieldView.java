package com.example.bullethellworld.views;

import static androidx.core.content.res.ResourcesCompat.getColor;
import static androidx.core.content.res.ResourcesCompat.getFont;
import static com.example.bullethellworld.activities.MainActivity.ingame;
import static java.lang.Math.max;
import static java.lang.Math.min;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.bullethellworld.Const;
import com.example.bullethellworld.R;
import com.example.bullethellworld.Util;
import com.example.bullethellworld.activities.MainActivity;

import java.util.Locale;
import java.util.Random;

public class PlayingFieldView extends View implements Bullet.BulletEventListener {

    Paint p = new Paint();

    Field field;
    Player player;
    EnemyShip enemy;
    private Context con;

    boolean unmeasured = true;



    public interface GameOverListener {
        void gameOver();
    }

    GameOverListener goListen;

    public void setGameOverListener(GameOverListener gol) {
        goListen = gol;
    }

    public PlayingFieldView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        p.setColor(getColor(getResources(), R.color.TXT_hilite, null));
        p.setTextSize(96);
        p.setTypeface(getFont(context, R.font.finger_paint));
        con = context;

    }

    public EnemyShip getEnemy() {return enemy;}

    public Player getPlayer() {return player;}

    public void reset() {
        setFieldAndPlayer(field.width(), field.height());
        //enemy.reset();
    }

    public void setFieldAndPlayer(int w, int h) {
        field = new Field(w, h);
        Log.d("PLAYER", String.format(Locale.getDefault(), "fieldsize is (%d, %d)", w, h));
        player = new Player(w/2f, h/2f, field);
        Random rdm = new Random();
        int nmeX = rdm.nextInt(field.width());
        int nmeY = 16;

        float[] nme_vect = new float[2];
        nme_vect[0] = rdm.nextFloat()*2-1;
        nme_vect[1] = rdm.nextFloat();
        nme_vect = Util.normalize(nme_vect);
        enemy = new EnemyShip(nmeX, nmeY, nme_vect, field, player, this, Const.NME_FIRERATE, con);
    }




    public void movePlayer(float x, float y) {
        player.move(x, y);
        if(player.cooldown > 0) player.cooldown --;
    }

    public void moveEnemy(float scale) {
        enemy.move(scale);
    }



    @Override
    public void onBulletAged(int id) {
        MainActivity.paused = true;
        int idx = enemy.bulletIndex(id);
        if(idx < 0) Log.e("onBulletAged", "ERROR: BULLET ID NOT FOUND");
        else enemy.getBullets().remove(idx);
        MainActivity.paused = false;
    }

    public void check_blt_plblt_coll() {
        for(PlayerBullet pb: player.getBullets()) {
            for(Bullet bl: enemy.getBullets()) {
                if(pb.hit_bullet(bl)) {
                    pb.dead = true;
                    bl.remove();
                }
            }
        }
        /*for(int i=player.getBullets().size()-1; i>=0; i--) {
            if(player.getBullets().get(i).dead) player.getBullets().remove(i);
        }*/
        for(int i=enemy.getBullets().size()-1; i>=0; i--) {
            if(enemy.getBullets().get(i).getAge()>=Const.BLT_MAXAGE) enemy.getBullets().remove(i);
        }
    }

    @Override
    public void onBulletHit(int id) {
        goListen.gameOver();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(MeasureSpec.getSize(widthMeasureSpec), MeasureSpec.getSize(heightMeasureSpec));
        if(unmeasured) {
            setFieldAndPlayer(MeasureSpec.getSize(widthMeasureSpec), MeasureSpec.getSize(heightMeasureSpec));
            unmeasured = false;
        }
    }

    @Override
    protected void onDraw(@NonNull Canvas canvas) {
        super.onDraw(canvas);

        if(ingame) {
            field.draw(canvas);
            player.draw(canvas);
            enemy.draw(canvas);
        } else {
            field.draw(canvas);
            String btnText = "BOOP";
            Rect txtBnds = new Rect();
            p.getTextBounds(btnText, 0, btnText.length(), txtBnds);

            canvas.drawText("BOOP", field.width()/2f-txtBnds.width()/2f, field.width()/2f-txtBnds.height(), p);
        }

    }



}
