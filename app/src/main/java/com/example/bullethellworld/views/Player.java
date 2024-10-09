package com.example.bullethellworld.views;

import static java.lang.Math.PI;
import static java.lang.Math.max;
import static java.lang.Math.min;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;

import com.example.bullethellworld.Const;

import java.util.ArrayList;

public class Player implements DrawableEntity, Collidable {

    public float pX, pY;

    public final int H= Const.PL_SIZE, W=Const.PL_SIZE;

    private Field field;
    private Paint paint;

    private ArrayList<PlayerBullet> bullets = new ArrayList<>();

    public int cooldown = 0;

    public ArrayList<PlayerBullet> getBullets() {
        return bullets;
    }

    public Player(float x, float y, Field f) {
        pX = x;
        pY = y;
        field = f;
        paint = setPaint();
    }

    public void fire(float[] vect) {
        if(cooldown <= 0) {
            PlayerBullet pb = new PlayerBullet(new float[] {pX+W/2f, pY+H/2f }, vect, field);
            pb.setPaint();
            bullets.add(pb);
            cooldown = Const.PL_FIRERATE;
            Log.d("Player.fire", "pewpew");
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

    public void move(float x, float y) {
       // Log.d("PLAYER", String.format(Locale.getDefault(), "shift is (%.1f, %.1f)", x, y));
       // Log.d("PLAYER", String.format(Locale.getDefault(), "fieldsize is (%d, %d)", field.width(), field.height()));
      //  Log.d("PLAYER", String.format(Locale.getDefault(), "pos+shift is (%.1f, %.1f)", pX+x, pY+y));
        pX = min(field.width()-W, max(0, pX+x));
        pY = min(field.height()-H, max(0, pY+y));
        if(!bullets.isEmpty()) moveBullets();
     //   Log.d("PLAYER", String.format(Locale.getDefault(), "moved player to (%.1f, %.1f)", pX, pY));
    }

    @Override
    public void draw(Canvas c) {
        c.drawRoundRect(pX,pY,pX+W,pY+H,4f, 4f, paint);

        paint.setStyle(Paint.Style.FILL_AND_STROKE);
        c.drawCircle(pX+W/5f, pY+H/3f, W/12f, paint);
        c.drawCircle(pX+4*W/5f, pY+H/3f, W/12f, paint);
        paint.setStyle(Paint.Style.STROKE);
        c.drawArc(pX+W/4f, pY+4*H/6f, pX+3*W/4f, pY+5*H/6f, 0f, 90f, false, paint);
        for(PlayerBullet pb: bullets) {
            pb.draw(c);
        }
    }

    @Override
    public Paint setPaint() {
        Paint p = new Paint();
        p.setStyle(Paint.Style.STROKE);
        p.setStrokeWidth(2);
        p.setColor(Color.argb(255, 196, 96, 224));

        return p;
    }

    @Override
    public boolean collides(float x0, float y0, int w, int h) {
        return (pX+W >= x0 && pX <= x0+w && pY+H >= y0 && pY <= y0+h);
    }
}
