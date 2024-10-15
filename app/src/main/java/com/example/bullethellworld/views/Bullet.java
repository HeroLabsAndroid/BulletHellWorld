package com.example.bullethellworld.views;

import static com.example.bullethellworld.Const.BLT_BLT_SPEED;
import static com.example.bullethellworld.Const.BLT_SIZE;
import static com.example.bullethellworld.Const.CLR_BLT_NEW;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;

import com.example.bullethellworld.Const;
import com.example.bullethellworld.Side;
import com.example.bullethellworld.Util;

import java.util.ArrayList;
import java.util.Locale;
import java.util.Random;
import java.util.UUID;


public class Bullet implements DrawableEntity {

    public enum BulletType {
        SINGLE, MULTI, BURST, NONE;

        public static BulletType fromOrdinal(int o) {
            switch (o) {
                case 0:
                    return SINGLE;
                case 1:
                    return MULTI;
                case 2:
                    return BURST;
                default:
                    return NONE;
            }
        }
    }

    public static final BulletType type= BulletType.NONE;
    public static final double[] SPAWNCHANCES = new double[] {0.6, 0.2, 0.2};

    public interface BulletEventListener {
        void onBulletAged(UUID id);
        void onBulletHit(String msg, UUID id);
    }

    public float bX, bY;
    public final int W=BLT_SIZE, H=BLT_SIZE;
    private float[] vct;

    int cooldown;
    int cooldown_Time;

    Paint paint;
    Paint lightpaint;

    Collidable player;
    Frame field;

    BulletEventListener bulletListen;

    protected ArrayList<PlayerBullet> bullets = new ArrayList<>();

    protected UUID id;


    protected int age = 0;

    protected Bitmap sprite;
    protected int hit_cooldown = -1;
    protected boolean shielded = true;

    /*------------- GETTERS & SETTERS -------------------------*/

    public boolean isShielded() {
        return shielded;
    }

    public int getAge() {return age;}
    public UUID getID() {return id;}

    public ArrayList<PlayerBullet> getBullets() {return bullets;}

    /*------------- CONSTRUCTORS ------------------------------*/

    public Bullet(float x, float y, float[] vct, Collidable player, Frame field, BulletEventListener bulletListen, UUID id, int cooldown) {
        bX = x;
        bY = y;
        this.vct = vct;
        age = -1;
        paint = setPaint();
        this.player = player;
        this.field = field;
        this.id = id;
        this.bulletListen = bulletListen;
        this.cooldown = cooldown;
        cooldown_Time = cooldown;
    }

    private float relative_age() {
        return (float)age/(float) Const.BLT_MAXAGE;
    }

    protected void remove() {
        age = Const.BLT_MAXAGE+1;
    }

    private void set_bullet_color() {
        float rage = relative_age();
        //rage = 0;
        paint.setColor(
                Color.argb(255,
                        ((int)(rage*Const.CLR_BLT_OLD[0])+(int)((1-rage)*Const.CLR_BLT_NEW[0])),
                        ((int)(rage*Const.CLR_BLT_OLD[1])+(int)((1-rage)*Const.CLR_BLT_NEW[1])),
                        ((int)(rage*Const.CLR_BLT_OLD[2])+(int)((1-rage)*Const.CLR_BLT_NEW[2]))
                )
        );
        paint.setAlpha(255);


        Color clr = Color.valueOf(paint.getColor());

        //lightpaint.setColor(Color.argb(255, (int) (clr.red()*1.2), (int) (clr.green()*1.2), (int) (clr.blue()*1.2)));
    }

    public void fire(float[] vect) {
            shielded = false;
            PlayerBullet pb = new PlayerBullet(new float[] {bX+W/2f, bY+H/2f }, vect, field, false);
            pb.setPaint();
            bullets.add(pb);
            cooldown = cooldown_Time;
            Log.d("bullet.fire", "pewpew");

    }

    public void moveBullets() {

        for(int i=bullets.size()-1; i>=0; i--) {
            if(bullets.get(i).dead) bullets.remove(i);
        }
        for(PlayerBullet pb: bullets) {
            pb.move();
            if(!pb.dead && player.collides(pb.pos[0], pb.pos[1], 1, 1)) {
                bulletListen.onBulletHit("hit by smol bullet from bullet nr"+id.toString(), id);
            }
        }
    }

    public void hit() {
        hit_cooldown = Const.HIT_COOLDOWN;
    }

    @Override
    public void draw(Canvas c) {
        c.drawCircle(bX,bY,W,paint);
        c.drawCircle(bX,bY, (float) (3 * W) /4,lightpaint);
        for(PlayerBullet pb: bullets) {
            pb.draw(c);
        }
    }

    @Override
    public Paint setPaint() {
        lightpaint = new Paint();
        Paint p = new Paint();
        p.setColor(Color.argb(255, (int)CLR_BLT_NEW[0], (int)CLR_BLT_NEW[1], (int)CLR_BLT_NEW[2]));
        Color clr = Color.valueOf(p.getColor());
        lightpaint.setColor(0xFF161B29);
        return p;
    }

    public void move(float scale) {
        if(hit_cooldown==-1) {
            if(cooldown <= 0) {
                if(Util.vectval(new double[] {player.getPos()[0]-bX, player.getPos()[1]-bY}) > Const.BLT_MINDIST_FIRE) {
                    Random rdm = new Random();
                    float[] v0 = new float[]{player.getPos()[0] - bX, player.getPos()[1] - bY};//{2*rdm.nextFloat()-1, 2*rdm.nextFloat()-1};
                    v0 = Util.normalize(v0, BLT_BLT_SPEED);
            /*float[] v1 = Util.normalize(Util.orthvect(v0));
            float[] v2 = Util.normalize(new float[] {-v0[0], -v0[1]});
            float[] v3 = Util.normalize(new float[] {-v1[0], -v1[1]});*/
                    fire(v0);
            /*fire(v1);
            fire(v2);
            fire(v3);*/
                }
            } else cooldown--;
            moveBullets();
            if(player.collides(bX+ vct[0]*scale-W/2f, bY+ vct[1]*scale-H/2f, W, H)) {
                bulletListen.onBulletHit("player hit bullet nr "+id.toString(), id);
                Log.d("BULLET_COLLISION", String.format(Locale.getDefault(),"collision on player"));
            }
            Side colside = field.collides(bX+ vct[0]*scale, bY+ vct[1]*scale, W, H);
            if(colside != Side.NONE)
            {
                Log.d("BULLET_COLLISION", String.format(Locale.getDefault(),"collision on side %s", colside.name()));
            }
            switch(field.collides(bX+ vct[0]*scale, bY+ vct[1]*scale, W, H)) {
                case NONE:
                    break;
                case TOP:
                case BOTTOM:
                    vct[1] = -vct[1];
                    age++;
                    set_bullet_color();
                    break;
                case LEFT:
                case RIGHT:
                    vct[0] = -vct[0];
                    age++;
                    set_bullet_color();
                    break;
            }

            if(age > Const.BLT_MAXAGE) {
                bulletListen.onBulletAged(id);
            }

            bX = bX + vct[0]*scale;
            bY = bY + vct[1]*scale;
        } else {
            hit_cooldown--;
            if(hit_cooldown == 0) remove();
        }

    }
}
