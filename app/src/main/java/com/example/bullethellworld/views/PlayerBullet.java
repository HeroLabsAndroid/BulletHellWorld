package com.example.bullethellworld.views;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;

import com.example.bullethellworld.Const;
import com.example.bullethellworld.Side;
import com.example.bullethellworld.Util;

public class PlayerBullet implements DrawableEntity, Collidable {
    Paint p;
    public float[] pos, vect;

    boolean dead = false;

    Frame field;

    boolean is_from_player;


    public PlayerBullet(float[] pos, float[] vect, Frame field) {
        this(pos, vect, field, true);
    }

    public PlayerBullet(float[] pos, float[] vect, Frame field, boolean is_from_player) {
        this.pos = pos;
        this.vect = Util.normalize(vect);
        this.field = field;
        this.is_from_player = is_from_player;
    }

    public void move() {
        if(field.collides(pos[0]+vect[0]* Const.PL_BLT_SPEED, pos[1]+vect[1]*Const.PL_BLT_SPEED, 2, 2)!= Side.NONE) {
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
        float scale = is_from_player ? Const.PL_BLT_LEN/2f : Const.BLT_BLT_LEN/2f;
        c.drawLine(pos[0]-vect[0]*scale,
                pos[1]-vect[1]*scale,
                pos[0]+vect[0]*scale,
                pos[1]+vect[1]*scale,
                p);

    }

    @Override
    public Paint setPaint() {
        p = new Paint();
        if(is_from_player) {
            p.setColor(Color.argb(255, 208, 255, 130));
            p.setStrokeWidth(4f);
        }
        else {
            p.setColor(Color.argb(255, 222, 84, 146));
            p.setStrokeWidth(8f);
        }


        return p;
    }

    @Override
    public boolean collides(float x0, float y0, int w, int h) {
        boolean out = (pos[0]>x0 && pos[0] <x0+h && pos[1]>y0 && pos[1]<y0+h);
        if(out) dead = true;

        return out;
    }
}
