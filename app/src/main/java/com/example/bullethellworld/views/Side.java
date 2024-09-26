package com.example.bullethellworld.views;

import java.util.Random;

public enum Side {
    NONE, LEFT, RIGHT, TOP, BOTTOM;

    public static Side rdm_side() {
        int side = new Random().nextInt(4);
        switch (side) {
            case 0:
                return LEFT;
            case 1:
                return RIGHT;
            case 2:
                return TOP;
            case 3:
                return BOTTOM;
        }

        return NONE;
    }
}