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

import java.util.Locale;

public class PlayerView extends View {
    float px, py;
    Paint p = new Paint();

    float wField, hField;


    public PlayerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        p.setColor(Color.argb(255, 196, 96, 224));
    }

    public void setField(float w, float h) {
        wField = w;
        hField = h;
        Log.d("PLAYER", String.format(Locale.getDefault(), "fieldsize is (%f, %f)", w, h));
        px=wField/2f;
        py=hField/2f;
    }

    public void move(float x, float y) {
        Log.d("PLAYER", String.format(Locale.getDefault(), "shift is (%.1f, %.1f)", x, y));
        Log.d("PLAYER", String.format(Locale.getDefault(), "fieldsize is (%f, %f)", wField, hField));
        Log.d("PLAYER", String.format(Locale.getDefault(), "pos+shift is (%.1f, %.1f)", px+x, py+y));
        px = min(wField, max(0, px+x));
        py = min(hField, max(0, py+y));
        Log.d("PLAYER", String.format(Locale.getDefault(), "moved player to (%.1f, %.1f)", px, py));
        invalidate();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(MeasureSpec.getSize(widthMeasureSpec), MeasureSpec.getSize(heightMeasureSpec));
        setField(MeasureSpec.getSize(widthMeasureSpec), MeasureSpec.getSize(heightMeasureSpec));
    }

    @Override
    protected void onDraw(@NonNull Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawRoundRect(px,py,px+32,py+32,4f, 4f, p);
    }
}
