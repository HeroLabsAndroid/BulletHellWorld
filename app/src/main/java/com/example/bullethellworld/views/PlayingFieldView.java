package com.example.bullethellworld.views;

import static java.lang.Math.max;
import static java.lang.Math.min;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.bullethellworld.GameOverDialog;
import com.example.bullethellworld.activities.MainActivity;

import java.util.ArrayList;
import java.util.Locale;
import java.util.Random;

public class PlayingFieldView extends View implements Bullet.BulletEventListener {

    Paint p = new Paint();

    Field field;
    Player player;

    private ArrayList<Bullet> bullets = new ArrayList<>();

    boolean unmeasured = true;

    int bullet_ctr = 0;

    public interface GameOverListener {
        void gameOver();
    }

    GameOverListener goListen;

    public void setGameOverListener(GameOverListener gol) {
        goListen = gol;
    }

    public PlayingFieldView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        p.setColor(Color.argb(255, 196, 96, 224));

    }

    public Player getPlayer() {return player;}

    public void reset() {
        bullets = new ArrayList<>();
        setFieldAndPlayer(field.width(), field.height());
        bullet_ctr = 0;
    }

    public void setFieldAndPlayer(int w, int h) {
        field = new Field(w, h);
        Log.d("PLAYER", String.format(Locale.getDefault(), "fieldsize is (%d, %d)", w, h));
        player = new Player(w/2f, h/2f, field);
    }

    public void spawnBullet() {
        Side side = Side.TOP;

        float[] v = new float[2];
        float[] pos = new float[2];

        Random rdm = new Random();

        switch (side) {
            case TOP:
                pos[0] = rdm.nextInt(field.width());
                pos[1] = Const.BLT_SIZE;
                v[0] = (rdm.nextBoolean() ? -1 : 1) * rdm.nextFloat();
                v[1] = rdm.nextFloat();
                v = Util.normalize(v, Const.BLT_SPEED_BASE);

        }

        bullets.add(new Bullet(pos[0], pos[1], v, player, field, this, bullet_ctr, 666+rdm.nextInt(2000)));
        bullet_ctr++;

    }

    public int bulletIndex(int bullet_id) {
        for(int i=0; i<bullets.size(); i++) {
            if(bullet_id==bullets.get(i).getID()) return i;
        }
        return -1;
    }

    public int bulletCount() {
        return bullets.size();
    }

    public void movePlayer(float x, float y) {
        player.move(x, y);
        if(player.cooldown > 0) player.cooldown --;
    }

    public void moveBullets(float scale) {
        for(Bullet bul: bullets) {
            bul.move(scale);
        }
    }

    @Override
    public void onBulletAged(int id) {
        MainActivity.paused = true;
        int idx = bulletIndex(id);
        if(idx < 0) Log.e("onBulletAged", "ERROR: BULLET ID NOT FOUND");
        else bullets.remove(idx);
        MainActivity.paused = false;
    }

    public void check_blt_plblt_coll() {
        for(PlayerBullet pb: player.getBullets()) {
            for(Bullet bl: bullets) {
                if(pb.hit_bullet(bl)) {
                    pb.dead = true;
                    bl.remove();
                }
            }
        }
        /*for(int i=player.getBullets().size()-1; i>=0; i--) {
            if(player.getBullets().get(i).dead) player.getBullets().remove(i);
        }*/
        for(int i=bullets.size()-1; i>=0; i--) {
            if(bullets.get(i).getAge()>=Const.BLT_MAXAGE) bullets.remove(i);
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

        field.draw(canvas);
        player.draw(canvas);
        for(Bullet bul: bullets) {
            bul.draw(canvas);
        }
    }



}
