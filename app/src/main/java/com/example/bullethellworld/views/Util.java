package com.example.bullethellworld.views;

public class Util {
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
}
