package com.example.bullethellworld.views;

import static com.example.bullethellworld.Const.BLT_BLT_SPEED;
import static com.example.bullethellworld.views.Bullet.BulletType.BURST;
import static com.example.bullethellworld.views.Bullet.BulletType.MULTI;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.util.Log;

import com.example.bullethellworld.Const;
import com.example.bullethellworld.R;
import com.example.bullethellworld.Side;
import com.example.bullethellworld.Util;

import java.util.Locale;
import java.util.UUID;

public class BulletMultishot extends Bullet {

    public static final BulletType type = MULTI;
    Context con;
    float[] vect;
    public BulletMultishot(float x, float y, float[] vct, Collidable player, Frame field, BulletEventListener bulletListen, UUID id, int cooldown, Context con) {
        super(x, y, vct, player, field, bulletListen, id, cooldown);
        this.con = con;
        sprite = Util.getBitmap(con, R.drawable.nme_multishot);
        this.vect = vct;
    }

    @Override
    public void draw(Canvas c) {
        if(hit_cooldown == -1) {
            c.drawBitmap(sprite, null, new Rect((int) bX, (int) bY, (int) (bX+W), (int) (bY+H)), paint);
        } else {
            float size =  W*(hit_cooldown/(float)Const.HIT_COOLDOWN);
            c.drawBitmap(sprite, null, new Rect((int) (bX+size/2f), (int)(bY+size/2f), (int) (bX+size), (int) (bY+size)), paint);
        }

        for(PlayerBullet pb: bullets) {
            pb.draw(c);
        }
    }

    @Override
    public void fire(float[] vect) {
            shielded = false;
            PlayerBullet pb = new PlayerBullet(new float[] {bX+W/2f, bY+H/2f }, vect, field, false);
            pb.setPaint();
            bullets.add(pb);
            Log.d("bullet.fire", "pewpew");

    }

    @Override
    public void move(float scale) {
        if(age<0) age++;
        moveBullets();
        if(hit_cooldown==-1) {
            cooldown--;
            if(cooldown <=50 && cooldown % 10 == 0) {
                if(Util.vectval(new double[] {player.getPos()[0]-bX, player.getPos()[1]-bY}) > Const.BLT_MINDIST_FIRE) {
                    float[] v0 = new float[]{player.getPos()[0] - bX, player.getPos()[1] - bY};
                    v0 = Util.normalize(v0, BLT_BLT_SPEED);
                    fire(v0);
                }

                if(cooldown<=0) cooldown = cooldown_Time;
            }
            if(!(age>=Const.BLT_MAXAGE) && player.collides(bX+vect[0]*scale, bY+vect[1]*scale, W, H)) {
                bulletListen.onBulletHit("player hit bullet nr "+id, id);
                Log.d("BULLET_COLLISION", String.format(Locale.getDefault(),"collision on player"));
            }
            Side colside = field.collides(bX+vect[0]*scale, bY+vect[1]*scale, W, H);
            if(colside != Side.NONE)
            {
                Log.d("BULLET_COLLISION", String.format(Locale.getDefault(),"collision on side %s", colside.name()));
            }
            switch(field.collides(bX+vect[0]*scale, bY+vect[1]*scale, W, H)) {
                case NONE:
                    break;
                case TOP:
                case BOTTOM:
                    vect[1] = -vect[1];
                    age++;
                    break;
                case LEFT:
                case RIGHT:
                    vect[0] = -vect[0];
                    age++;
                    break;
            }

            if(age > Const.BLT_MAXAGE) {
                bulletListen.onBulletAged(id);
            }

            bX = bX + vect[0]*scale;
            bY = bY + vect[1]*scale;
        } else {
            if(hit_cooldown==0) remove();
            else hit_cooldown--;
        }
    }
}
