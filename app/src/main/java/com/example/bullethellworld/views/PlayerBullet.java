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
    Paint hilitep;
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
        float scale = is_from_player ? 2*Const.PL_BLT_LEN/3f : Const.BLT_BLT_LEN;
        c.drawCircle(pos[0], pos[1], scale, p);
        c.drawCircle(pos[0]-1, pos[1]-1, scale/2, hilitep);
        /*c.drawLine(pos[0]-vect[0]*scale,
                pos[1]-vect[1]*scale,
                pos[0]+vect[0]*scale,
                pos[1]+vect[1]*scale,
                hilitep);*/

    }

    @Override
    public Paint setPaint() {
        p = new Paint();
        hilitep = new Paint();
        if(is_from_player) {
            p.setColor(Color.argb(255, 67, 76, 42));
            p.setStrokeWidth(4f);
            hilitep.setColor(Color.argb(255, 198, 235, 136));
            hilitep.setStrokeWidth(2f);
        }
        else {
            p.setColor(Color.argb(255, 61, 37, 21));
            p.setStrokeWidth(8f);
            hilitep.setColor(Color.argb(255,198, 180, 136));
            hilitep.setStrokeWidth(4f);
        }


        return p;
    }

    @Override
    public boolean collides(float x0, float y0, int w, int h) {
        boolean out = (pos[0]>x0 && pos[0] <x0+h && pos[1]>y0 && pos[1]<y0+h);
        if(out) dead = true;

        return out;
    }

    @Override
    public float[] getPos() {
        return pos;
    }
}
