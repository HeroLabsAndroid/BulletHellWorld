package com.example.bullethellworld.views;

import static com.example.bullethellworld.views.Const.BLT_SIZE;
import static com.example.bullethellworld.views.Const.CLR_BLT_NEW;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;

import java.util.ArrayList;
import java.util.Locale;


public class Bullet implements DrawableEntity {

    public interface BulletEventListener {
        void onBulletAged(int id);
        void onBulletHit(int id);
    }

    public float bX, bY;
    public final int W=BLT_SIZE, H=BLT_SIZE;
    private float[] vect;

    int cooldown;
    int cooldown_Time;

    Paint paint;
    Paint lightpaint;

    Collidable player;
    Frame field;

    BulletEventListener bulletListen;

    private ArrayList<PlayerBullet> bullets = new ArrayList<>();

    private int id = 0;


    private int age = 0;

    /*------------- GETTERS & SETTERS -------------------------*/

    public int getAge() {return age;}
    public int getID() {return id;}

    public ArrayList<PlayerBullet> getBullets() {return bullets;}

    /*------------- CONSTRUCTORS ------------------------------*/

    public Bullet(float x, float y, float[] vect, Collidable player, Frame field, BulletEventListener bulletListen, int id, int cooldown) {
        bX = x;
        bY = y;
        this.vect = vect;
        paint = setPaint();
        this.player = player;
        this.field = field;
        this.id = id;
        this.bulletListen = bulletListen;
        this.cooldown = cooldown;
        cooldown_Time = cooldown;
    }

    private float relative_age() {
        return (float)age/(float)Const.BLT_MAXAGE;
    }

    public void remove() {
        age = Const.BLT_MAXAGE+1;
    }

    private void set_bullet_color() {
        float rage = relative_age();
        //rage = 0;
        paint.setColor(
                Color.argb(255,
                        ((int)(rage*Const.CLR_BLT_OLD[0])+(int)((1-rage)*Const.CLR_BLT_NEW[0])),
                        ((int)(rage*Const.CLR_BLT_OLD[1])+(int)((1-rage)*Const.CLR_BLT_NEW[1])),
                        ((int)(rage*Const.CLR_BLT_OLD[2])+(int)((1-rage)*Const.CLR_BLT_NEW[2]))
                )
        );
        paint.setAlpha(255);


        Color clr = Color.valueOf(paint.getColor());

        //lightpaint.setColor(Color.argb(255, (int) (clr.red()*1.2), (int) (clr.green()*1.2), (int) (clr.blue()*1.2)));
    }

    public void fire(float[] vect) {
        if(cooldown <= 0) {
            PlayerBullet pb = new PlayerBullet(new float[] {bX+W/2f, bY+H/2f }, vect, field);
            pb.setPaint();
            bullets.add(pb);
            cooldown = cooldown_Time;
            Log.d("bullet.fire", "pewpew");
        }
    }

    public void moveBullets() {
        for(int i=bullets.size()-1; i>=0; i--) {
            if(bullets.get(i).dead) bullets.remove(i);
        }
        for(PlayerBullet pb: bullets) {
            pb.move();
        }
    }

    @Override
    public void draw(Canvas c) {
        c.drawCircle(bX,bY,W,paint);
        c.drawCircle(bX,bY, (float) (3 * W) /4,lightpaint);
    }

    @Override
    public Paint setPaint() {
        lightpaint = new Paint();
        Paint p = new Paint();
        p.setColor(Color.argb(255, (int)CLR_BLT_NEW[0], (int)CLR_BLT_NEW[1], (int)CLR_BLT_NEW[2]));
        Color clr = Color.valueOf(p.getColor());
        lightpaint.setColor(0xFF161B29);
        return p;
    }

    public void move(float scale) {
        if(cooldown <= 0) {
            fire(new float[] {-1, -1});
            fire(new float[] {-1, 1});
            fire(new float[] {1, -1});
            fire(new float[] {1, 1});
        }
        moveBullets();
        if(player.collides(bX+vect[0]*scale-W/2f, bY+vect[1]*scale-H/2f, W, H)) {
            bulletListen.onBulletHit(id);
        }
        Side colside = field.collides(bX+vect[0]*scale, bY+vect[1]*scale, W, H);
        if(colside != Side.NONE)
        {
            Log.d("BULLET_COLLISION", String.format(Locale.getDefault(),"collision on side %s", colside.name()));
        }
        switch(field.collides(bX+vect[0]*scale, bY+vect[1]*scale, W, H)) {
            case NONE:
                break;
            case TOP:
            case BOTTOM:
                vect[1] = -vect[1];
                age++;
                set_bullet_color();
                break;
            case LEFT:
            case RIGHT:
                vect[0] = -vect[0];
                age++;
                set_bullet_color();
                break;
        }

        if(age > Const.BLT_MAXAGE) {
            bulletListen.onBulletAged(id);
        }

        bX = bX + vect[0]*scale;
        bY = bY + vect[1]*scale;
    }
}
