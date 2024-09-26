package com.example.bullethellworld.views;

import static com.example.bullethellworld.views.Const.BLT_SIZE;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import com.example.bullethellworld.GameOverDialog;

public class Bullet implements DrawableEntity {

    public interface BulletEventListener {
        void onBulletAged(int id);
        void onBulletHit(int id);
    }

    public float bX, bY;
    public final int W=BLT_SIZE, H=BLT_SIZE;
    private float[] vect=new float[2];

    Paint paint;
    Paint lightpaint;

    Collidable player;
    Frame field;

    BulletEventListener bulletListen;

    private int id;


    private int age = 0;

    /*------------- GETTERS & SETTERS -------------------------*/

    public int getAge() {return age;}
    public int getID() {return id;}

    /*------------- CONSTRUCTORS ------------------------------*/

    public Bullet(float x, float y, float[] vect, Collidable player, Frame field, BulletEventListener bulletListen, int id) {
        bX = x;
        bY = y;
        this.vect = vect;
        paint = setPaint();
        this.player = player;
        this.field = field;
        this.id = id;
        this.bulletListen = bulletListen;
    }

    private float relative_age() {
        return (float)age/(float)Const.BLT_MAXAGE;
    }

    private void set_bullet_color() {
        float rage = relative_age();

        paint.setColor(
                Color.argb(256,
                        (rage*Const.CLR_BLT_OLD[0])+((1-rage)*Const.CLR_BLT_NEW[0]),
                        (rage*Const.CLR_BLT_OLD[1])+((1-rage)*Const.CLR_BLT_NEW[1]),
                        (rage*Const.CLR_BLT_OLD[2])+((1-rage)*Const.CLR_BLT_NEW[2])
                )
        );
        paint.setAlpha(255);


        Color clr = Color.valueOf(paint.getColor());

        //lightpaint.setColor(Color.argb(255, (int) (clr.red()*1.2), (int) (clr.green()*1.2), (int) (clr.blue()*1.2)));
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
        p.setColor(Color.argb(255, 84, 224, 224));
        Color clr = Color.valueOf(p.getColor());
        lightpaint.setColor(0xFF161B29);
        return p;
    }

    public void move() {
        if(player.collides(bX+vect[0]-W/2f, bY+vect[1]-H/2f, W, H)) {
            bulletListen.onBulletHit(id);
        }

        switch(field.collides(bX+vect[0]-W/2f, bY+vect[1]-H/2f, W, H)) {
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

        bX = bX + vect[0];
        bY = bY + vect[1];
    }
}
