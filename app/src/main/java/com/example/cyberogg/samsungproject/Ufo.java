package com.example.cyberogg.samsungproject;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import java.util.Random;

class Ufo {

    enum UfoState {
        Chilling, Moving;
    }

    private float corX = 0;
    private float corY = 0;
    private float targetX = 0;
    private float targetY = 0;
    private float velocity = 10;
    private float vecX = 1;
    private float vecY = 0;
    int width = 80;
    int height = 40;
    private UfoState state = UfoState.Chilling;
    private int chilingRate = 400;
    private int chilingNow = 0;
    private int shootingRate = 300;
    private int shootingNow = 0;
    private int movedRate = 200;
    private int movedNow = 0;
    boolean isAlive = false;
    boolean isStupid = false;

    Ufo(boolean type) {
        isStupid = type;
    }

    void setCor(int nCorX, int nCorY) {
        corX = nCorX;
        corY = nCorY;
        state = UfoState.Chilling;
        shootingNow = 0;
        chilingNow = (int) (GameContent.fps * 9);
    }

    float getCorX() {
        return corX;
    }

    float getCorY() {
        return corY;
    }

    void doSomething() {
        chilingRate = (int) (GameContent.fps * 10);
        if (isStupid)
            shootingRate = (int) (GameContent.fps * 2);
        else
            shootingRate = (int) (GameContent.fps * 8);
        movedRate = (int) (GameContent.fps * 8);
        Random r = new Random();
        shootingNow += r.nextInt(3) + 1;
        if (shootingNow > shootingRate && !GameContent.gameOver) {
            shootingNow = 0;
            shoot();
        }
        if (state == UfoState.Chilling) {
            chilingNow += r.nextInt(3) + 1;
            if (chilingNow > chilingRate) {
                state = UfoState.Moving;
                velocity = 10 + r.nextInt(4) - 2;
                targetX = r.nextInt(GameContent.width - 100) + 100;
                targetY = r.nextInt(GameContent.height - 100) + 100;
                float l = len(corX, corY, targetX, targetY);
                vecX = (targetX - corX) / l;
                vecY = (targetY - corY) / l;
                chilingNow = 0;
            }
        } else if (state == UfoState.Moving) {
            movedNow += r.nextInt(3) + 1;
            move();
            if ((Math.abs(corX - targetX) < velocity * 2 && Math.abs(corY - targetY) < velocity * 2)
                    || movedNow > movedRate) {
                state = UfoState.Chilling;
                chilingNow = 0;
                movedNow = 0;
            }
        }
    }

    private void move() {
        corX += vecX * velocity * GameContent.fpsNormal / GameContent.fps;
        corY += vecY * velocity * GameContent.fpsNormal / GameContent.fps;
        if (corX - width > GameContent.width)
            corX = -width + 1;
        if (corX + width < 0)
            corX = GameContent.width - height - 1;
        if (corY - height > GameContent.height)
            corY = -height + 1;
        if (corY + height < 0)
            corY = GameContent.height - height - 1;
    }

    private void shoot() {
        Random r = new Random();
        if (isStupid) {
            int angl = r.nextInt(360);
            GameContent.ufoBullets.add(new Bullet(corX, corY,
                    (float) Math.sin(Math.toRadians(angl)),
                    (float) Math.cos(Math.toRadians(angl)), 1));
        } else {
            float gx = GameContent.glaider.getCorX();
            float gy = GameContent.glaider.getCorY();
            float l = len(corX, corY, gx, gy);
            float vx = (gx - corX) / l;
            float vy = (gy - corY) / l;
            GameContent.ufoBullets.add(new Bullet(corX, corY, vx, vy, 1));
        }
    }

    void draw(Canvas canvas) {
        Paint paint = new Paint();
        paint.setColor(Color.WHITE);
        paint.setStrokeWidth(5);
        float[] p = new float[28];
        p[0] = (float) width / 4 + corX;
        p[1] = corY;
        p[2] = (float) width / 4 + corX;
        p[3] = corY - height / 2;
        p[4] = (float) width / 4 + corX;
        p[5] = corY - height / 2;
        p[6] = (float) -width / 4 + corX;
        p[7] = corY - height / 2;
        p[8] = (float) -width / 4 + corX;
        p[9] = corY - height / 2;
        p[10] = (float) -width / 4 + corX;
        p[11] = corY;
        p[12] = (float) -width / 4 + corX;
        p[13] = corY;
        p[14] = (float) -width / 2 + corX;
        p[15] = corY + height / 2;
        p[16] = (float) -width / 2 + corX;
        p[17] = corY + height / 2;
        p[18] = (float) width / 2 + corX;
        p[19] = corY + height / 2;
        p[20] = (float) width / 2 + corX;
        p[21] = corY + height / 2;
        p[22] = (float) width / 4 + corX;
        p[23] = corY;
        p[24] = (float) width / 4 + corX;
        p[25] = corY;
        p[26] = (float) -width / 4 + corX;
        p[27] = corY;
        canvas.drawLines(p, paint);
        if (!isStupid) {
            canvas.drawLine((float) width / 4 + corX, corY,
                    (float) -width / 4 + corX, corY + height / 2, paint);
        }
    }

    private float len(float p1x, float p1y, float p2x, float p2y) {
        return (float) Math.sqrt((p1x - p2x) * (p1x - p2x) + (p1y - p2y) * (p1y - p2y));
    }
}
