package com.example.bullethellworld.views;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import com.example.bullethellworld.Side;

public class Field implements Frame, DrawableEntity{
    private int wField, hField;
    Paint p;

    public int width() {
        return wField;
    }

    public int height() {
        return hField;
    }

    public Field(int w, int h) {
        wField = w;
        hField = h;
        setPaint();
    }

    @Override
    public Side collides(float x0, float y0, int w, int h) {
        if(x0 < 0) return Side.LEFT;
        else if( x0 > wField-w) return Side.RIGHT;
        else if(y0 < 0) return Side.TOP;
        else if (y0 > hField-h) return Side.BOTTOM;
        else return Side.NONE;
    }

    @Override
    public void draw(Canvas c) {
        c.drawRect(0,0,wField,hField,p);
    }

    @Override
    public Paint setPaint() {
        p = new Paint();
        p.setStrokeWidth(4);
        p.setStyle(Paint.Style.STROKE);
        p.setColor(Color.valueOf(0xFFCEF3F2).toArgb());
        return p;
    }
}
