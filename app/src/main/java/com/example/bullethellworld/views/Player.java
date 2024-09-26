package com.example.bullethellworld.views;

import static java.lang.Math.max;
import static java.lang.Math.min;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;

import java.util.Locale;

public class Player implements DrawableEntity, Collidable {

    public float pX, pY;

    public final int H=Const.PL_SIZE, W=Const.PL_SIZE;

    private Field field;
    private Paint paint;

    public Player(float x, float y, Field f) {
        pX = x;
        pY = y;
        field = f;
        paint = setPaint();
    }

    public void move(float x, float y) {
        Log.d("PLAYER", String.format(Locale.getDefault(), "shift is (%.1f, %.1f)", x, y));
        Log.d("PLAYER", String.format(Locale.getDefault(), "fieldsize is (%d, %d)", field.width(), field.height()));
        Log.d("PLAYER", String.format(Locale.getDefault(), "pos+shift is (%.1f, %.1f)", pX+x, pY+y));
        pX = min(field.width()-(W/2f), max(0, pX+x));
        pY = min(field.height()-(H/2f), max(0, pY+y));
        Log.d("PLAYER", String.format(Locale.getDefault(), "moved player to (%.1f, %.1f)", pX, pY));
    }

    @Override
    public void draw(Canvas c) {
        c.drawRoundRect(pX,pY,pX+W,pY+H,4f, 4f, paint);
    }

    @Override
    public Paint setPaint() {
        Paint p = new Paint();
        p.setColor(Color.argb(255, 196, 96, 224));

        return p;
    }

    @Override
    public boolean collides(float x0, float y0, float w, float h) {
        return (pX+W >= x0 && pX <= x0+w && pY+H >= y0 && pY <= y0+h);
    }
}
