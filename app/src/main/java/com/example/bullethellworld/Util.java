package com.example.bullethellworld;


import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;

import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;

public class Util {
    public static float[] orthvect(float[] vect) {
        float[] out = new float[2];
        out[0] = 1/vect[0];
        out[1] = -1/vect[1];

        return normalize(out);
    }

    public static float[] normalize(float[] vect, float scale) {
        float[] out = new float[vect.length];
        double sq_sum_root = 0;
        for(float f: vect) {
            sq_sum_root += Math.pow(f, 2);
        }
        sq_sum_root = Math.sqrt(sq_sum_root);

        for(int i=0; i<vect.length; i++) {
            out[i] = (float)(vect[i]/sq_sum_root)*scale;
        }

        return out;
    }

    public static float[] normalize(float[] vect) {
        return normalize(vect, 1);
    }

    public static Bitmap getBitmap(Context c, int drawableRes) {
        Drawable drawable = ContextCompat.getDrawable(c, drawableRes);
        Canvas canvas = new Canvas();
        assert drawable != null;
        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        canvas.setBitmap(bitmap);
        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
        drawable.draw(canvas);

        return bitmap;
    }

    public static double vectval(double[] v) {
        double val = 0;
        for(int i=0; i<v.length; i++) {
            val += Math.pow(v[i],2);
        }
        return Math.sqrt(val);
    }

    public static double vectang(double[] v1, double[] v2) {
        double scprod = 0;
        double prprod = vectval(v1) * vectval(v2);
        for(int i=0; i< v1.length; i++) {
            scprod += v1[i]*v2[i];
        }
        return Math.acos(scprod/prprod);
    }
}
