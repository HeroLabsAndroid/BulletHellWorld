package com.example.bullethellworld;

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
}
