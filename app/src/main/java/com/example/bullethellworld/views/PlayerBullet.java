package com.example.bullethellworld.views;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;

public class PlayerBullet implements DrawableEntity, Collidable {
    Paint p;
    float[] pos, vect;

    boolean dead = false;

    Frame field;


    public PlayerBullet(float[] pos, float[] vect, Frame field) {
        this.pos = pos;
        this.vect = Util.normalize(vect);
        this.field = field;
    }

    public void move() {
        if(field.collides(pos[0]+vect[0]*Const.PL_BLT_SPEED, pos[1]+vect[1]*Const.PL_BLT_SPEED, 2, 2)!=Side.NONE) {
            dead = true;
        } else {
            pos[0] += vect[0]*Const.PL_BLT_SPEED;
            pos[1] += vect[1]*Const.PL_BLT_SPEED;
        }
    }

    public boolean hit_bullet(Bullet b) {
        boolean hit = (pos[0]>(b.bX-b.W)
                && pos[0]<(b.bX+b.W)
                && pos[1]>(b.bY-b.H)
                && pos[1]<(b.bY+b.H));
        if(hit) {
            Log.d("HIT", "Playerbullet hit bullet");
        }

        return hit;
    }

    @Override
    public void draw(Canvas c) {
        c.drawLine(pos[0]-vect[0]*Const.PL_BLT_LEN/2,
                pos[1]-vect[1]*Const.PL_BLT_LEN/2,
                pos[0]+vect[0]*Const.PL_BLT_LEN/2,
                pos[1]+vect[1]*Const.PL_BLT_LEN/2,
                p);
    }

    @Override
    public Paint setPaint() {
        p = new Paint();
        p.setColor(Color.argb(255, 208, 255, 130));
        p.setStrokeWidth(4f);

        return p;
    }

    @Override
    public boolean collides(float x0, float y0, float w, float h) {
        boolean out = (pos[0]>x0 && pos[0] <x0+h && pos[1]>y0 && pos[1]<y0+h);
        if(out) dead = true;

        return out;
    }
}
