package com.example.bullethellworld.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.Locale;

public class JoyconView extends View {
    Paint p = new Paint();
    Paint pDark = new Paint();
    float px, py;
    float cX, cY;

    JoyconListener jListen;

    float maxdist;
    boolean firstdraw = true;
    boolean ctrl = true;

    public abstract interface JoyconListener {
        public void onJoyconMoved(float[] coord, boolean ctrl);
    }


    public float[] get_coord() {
        return new float[] {px, py};
    }

    /*public int[] get_shift() {
        return new int[] {(int)((px-getWidth()/2)/getWidth()*6), (int)((py-getHeight()/2)/getHeight()*6)};
    }*/

    public double dist_to_ctr(double[] coord) {

        return Math.sqrt(Math.pow(coord[0],2) + Math.pow(coord[1],2));
    }

    public double[] scale_coords(double[] coords, double dist) {
        double scale = maxdist/dist;

        return new double[] {coords[0]*scale, coords[1]*scale};
    }

    public void setIsCtrl(boolean isCtrl) {
        ctrl = isCtrl;
        if(isCtrl) {
            p.setColor(Color.argb(255, 110,110,255));
            pDark.setColor(Color.argb(255, 22,22,88));
        } else {
            p.setColor(Color.argb(255, 219,92,130));
            pDark.setColor(Color.argb(255, 126,92,130));
        }
    }

    public JoyconView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        p.setColor(Color.argb(255, 110,110,255));
        pDark.setColor(Color.argb(255, 22,22,88));
        jListen = (JoyconListener) context;
        measure(getMeasuredWidth(), getMeasuredHeight());
        invalidate();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(MeasureSpec.getSize(widthMeasureSpec), MeasureSpec.getSize(heightMeasureSpec));

    }


    @Override
    protected void onDraw(@NonNull Canvas canvas) {
        super.onDraw(canvas);
        if(firstdraw) {
            px = 0;
            py = 0;
            cX = getWidth()/2f;
            cY = getHeight()/2f;
            maxdist = getWidth()/3f;
        }
        //canvas.drawRect(getLeft(), getTop(), getRight(), getBottom(), pDark );
        //canvas.drawCircle(getLeft()+getWidth()/2.0f, getTop()+getHeight()/2.0f, getWidth()/2.0f, p);
        p.setStyle(Paint.Style.STROKE);
        p.setStrokeWidth(4);
        canvas.drawCircle(cX, cY, getWidth()/3f, p);

        p.setStyle(Paint.Style.FILL);
        canvas.drawCircle(cX+px, cY+py, getWidth()/8.0f, p);
        firstdraw = false;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        super.onTouchEvent(event);
        if(event.getAction()==MotionEvent.ACTION_DOWN || event.getAction()==MotionEvent.ACTION_MOVE) {
            px = event.getX()-cX;
            py = event.getY()-cY;
            double dist = dist_to_ctr(new double[] {px, py});
            if(dist >= maxdist) {
               // Log.d("JOYCON_TOUCH", String.format(Locale.getDefault(), "dist=%f, max=%f: scaling coords", dist, maxdist));
               // Log.d("JOYCON_TOUCH", String.format(Locale.getDefault(), "  unscaled: x=%f, y=%f", px, py));
                double[] scaled_coords = scale_coords(new double[] {px, py}, dist);
                px = (float) scaled_coords[0];
                py = (float) scaled_coords[1];
               // Log.d("JOYCON_TOUCH", String.format(Locale.getDefault(), "  scaled: x=%f, y=%f", px, py));
            }// else Log.d("JOYCON_TOUCH", String.format(Locale.getDefault(), "dist=%f, max=%f: NOT scaling coords", dist, maxdist));
            jListen.onJoyconMoved(new float[] {px/maxdist*8, py/maxdist*8}, ctrl);
            invalidate();
        } else if(event.getAction()==MotionEvent.ACTION_UP) {
            px = 0;
            py = 0;
            if(ctrl) jListen.onJoyconMoved(new float[] {px, py}, true);
            invalidate();
        }

        return true;
    }
}
