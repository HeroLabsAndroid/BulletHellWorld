package com.example.bullethellworld.views;

public class Field implements Frame{
    private int wField, hField;

    public int width() {
        return wField;
    }

    public int height() {
        return hField;
    }

    public Field(int w, int h) {
        wField = w;
        hField = h;
    }

    @Override
    public Side collides(float x0, float y0, float w, float h) {
        if(x0 < 0) return Side.LEFT;
        else if( x0+w > wField) return Side.RIGHT;
        else if(y0 < 0) return Side.TOP;
        else if (y0+h > hField) return Side.BOTTOM;
        else return Side.NONE;
    }
}
