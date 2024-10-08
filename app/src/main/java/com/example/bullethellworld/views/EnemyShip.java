package com.example.bullethellworld.views;

import static java.lang.Math.max;
import static java.lang.Math.min;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.Log;

import com.example.bullethellworld.Const;
import com.example.bullethellworld.R;
import com.example.bullethellworld.Side;
import com.example.bullethellworld.Util;

import java.util.ArrayList;
import java.util.Locale;
import java.util.Random;

public class EnemyShip implements DrawableEntity, Collidable {

    public float pX, pY;

    public final int H= Const.NME_SIZE, W=Const.NME_SIZE;

    private Field field;
    private Collidable player;
    private Paint paint;
    private Bullet.BulletEventListener bel;

    private ArrayList<Bullet> bullets = new ArrayList<>();

    public int cooldown = 0;
    public int cooldown_Time = 0;
    float[] vect = new float[2];

    int bullet_ctr = 0;

    private Bitmap sprite;
    private Context con;

    public ArrayList<Bullet> getBullets() {
        return bullets;
    }

    public EnemyShip(float x, float y, float[] vect, Field f, Collidable player, Bullet.BulletEventListener bel, int cooldown, Context con) {
        pX = x;
        pY = y;
        this.con = con;
        this.vect = vect;
        this.player = player;
        field = f;
        paint = setPaint();
        this.bel = bel;

        sprite = Util.getBitmap(con, R.drawable.nme_ship);
        this.cooldown = cooldown;
        cooldown_Time = cooldown;
    }


    public void fire() {
        float[] v = new float[2];
        float[] pos = new float[2];

        Random rdm = new Random();

        pos[0] = pX;
        pos[1] = pY;
        v[0] = (rdm.nextBoolean() ? -1 : 1) * rdm.nextFloat();
        v[1] = (rdm.nextBoolean() ? -1 : 1) * rdm.nextFloat();
        v = Util.normalize(v, Const.BLT_SPEED_BASE);

        bullets.add(new Bullet(pos[0], pos[1], v, player, field, bel, bullet_ctr, Const.BLT_FIRERATE+rdm.nextInt(69)));
        bullet_ctr++;

    }

    public void moveBullets(float scale) {
        for(int i=bullets.size()-1; i>=0; i--) {
            if(bullets.get(i).getAge()>Const.BLT_MAXAGE) bullets.remove(i);
        }
        for(Bullet b: bullets) {
            b.move(scale);
        }
    }

    public int bulletIndex(int bullet_id) {
        for(int i=0; i<bullets.size(); i++) {
            if(bullet_id==bullets.get(i).getID()) return i;
        }
        return -1;
    }

    public int bulletCount() {
        return bullets.size();
    }

    public void move(float scale) {
        if(cooldown<=0 && Util.vectval(new double[] {player.getPos()[0]-pX, player.getPos()[1]-pY})>Const.BLT_MINDIST_FIRE) {
            fire();
            cooldown = cooldown_Time;
        } else cooldown--;
        // Log.d("PLAYER", String.format(Locale.getDefault(), "shift is (%.1f, %.1f)", x, y));
        // Log.d("PLAYER", String.format(Locale.getDefault(), "fieldsize is (%d, %d)", field.width(), field.height()));
        //  Log.d("PLAYER", String.format(Locale.getDefault(), "pos+shift is (%.1f, %.1f)", pX+x, pY+y));
        if(!bullets.isEmpty()) moveBullets(scale);
        //   Log.d("PLAYER", String.format(Locale.getDefault(), "moved player to (%.1f, %.1f)", pX, pY));

        if(player.collides(pX+vect[0]*scale, pY+vect[1]*scale, W, H)) {
            bel.onBulletHit("player hit enemy ship", -1);
        }
        Side colside = field.collides(pX+vect[0]*scale, pY+vect[1]*scale, W, H);
        if(colside != Side.NONE)
        {
            Log.d("BULLET_COLLISION", String.format(Locale.getDefault(),"collision on side %s", colside.name()));
        }
        switch(colside) {
            case NONE:
                break;
            case TOP:
            case BOTTOM:
                vect[1] = -vect[1];
                break;
            case LEFT:
            case RIGHT:
                vect[0] = -vect[0];
                break;
        }


        pX = pX + vect[0]*scale;
        pY = pY + vect[1]*scale;
    }

    @Override
    public void draw(Canvas c) {
        /*c.drawRoundRect(pX,pY,pX+W,pY+H,4f, 4f, paint);
        c.drawLine(pX+W/6f, pY+H/4f, pX+2*W/6f, pY+2*H/5f, paint);
        c.drawCircle(pX+W/5f, pY+3*H/7f, W/12f, paint);
        c.drawLine(pX+W-W/6f, pY+H/4f, pX+W-2*W/6f, pY+2*H/5f, paint);
        c.drawCircle(pX+4*W/5f, pY+3*H/7f, W/12f, paint);
        c.drawArc(pX+2*W/5f, pY+4*H/6f, pX+3*W/5f, pY+5*H/6f, 0f, 180f, false, paint);*/
        c.drawBitmap(sprite, null, new Rect((int) pX, (int) pY, (int) (pX+W), (int) (pY+W)), paint);
        for(Bullet pb: bullets) {
            pb.draw(c);
        }
    }

    @Override
    public Paint setPaint() {
        Paint p = new Paint();
        p.setStyle(Paint.Style.STROKE);
        p.setStrokeWidth(2);
        p.setColor(Color.argb(255, 242, 218, 245));

        return p;
    }

    @Override
    public boolean collides(float x0, float y0, int w, int h) {
        return (pX+W >= x0 && pX <= x0+w && pY+H >= y0 && pY <= y0+h);
    }

    @Override
    public float[] getPos() {
        return new float[] {pX, pY};
    }
}
