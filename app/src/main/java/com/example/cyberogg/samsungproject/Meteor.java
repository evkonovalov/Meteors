package com.example.cyberogg.samsungproject;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import java.util.Random;

class Meteor {
    private float corX = 0;
    private float corY = 0;
    private float velocity = 0;
    private float vecX = 1;
    private float vecY = 0;
    private double angle = 0;
    private double angleSpeed = 0;
    private int radius = 25;
    boolean marked = false;
    private float[] p = new float[24];
    private float[] d = new float[12];

    float getCorX() {
        return corX;
    }

    float getCorY() {
        return corY;
    }

    Meteor() {
        Random r = new Random();
        switch (r.nextInt(4)) {
            case 0:
                corX = GameContent.width;
                corY = r.nextInt(GameContent.height);
                break;
            case 1:
                corX = 0;
                corY = r.nextInt(GameContent.height);
                break;
            case 2:
                corY = GameContent.height;
                corX = r.nextInt(GameContent.width);
                break;
            case 3:
                corY = 0;
                corX = r.nextInt(GameContent.width);
                break;
        }
        int angl = r.nextInt(360);
        vecX = (float) Math.sin(Math.toRadians(angl));
        vecY = (float) Math.cos(Math.toRadians(angl));
        velocity = r.nextInt(5) + 5;
        double r2 = radius;
        float[] m = new float[32];
        for (int i = 0; i < 8; i++) {
            double v1 = Math.cos(Math.toRadians(i * 45)) -
                    Math.sin(Math.toRadians(i * 45));
            double v2 = Math.cos(Math.toRadians(i * 45)) +
                    Math.sin(Math.toRadians(i * 45));
            double v3 = Math.cos(Math.toRadians((i + 1) * 45)) -
                    Math.sin(Math.toRadians((i + 1) * 45));
            double v4 = Math.cos(Math.toRadians((i + 1) * 45)) +
                    Math.sin(Math.toRadians((i + 1) * 45));
            m[i * 4] = (float) (r2 * v1);
            m[i * 4 + 1] = (float) (r2 * v2);
            m[i * 4 + 2] = (float) (r2 * v3);
            m[i * 4 + 3] = (float) (r2 * v4);
        }
        angleSpeed = r.nextDouble() * 2 - 1;
        int k = r.nextInt(6) * 4 + 2;
        int k2 = r.nextInt(6) * 4 + 2;
        while (k == k2)
            k2 = r.nextInt(6) * 4 + 2;
        int j = 0;
        for (int i = 0; i < 32; i++) {
            if (!(i - k < 4 && i - k >= 0) && !(i - k2 < 4 && i - k2 >= 0)) {
                p[j] = m[i];
                j++;
            }
        }
        int p1 = r.nextInt(12);
        int p2 = r.nextInt(12);
        while (Math.abs(p1 - p2) < 2)
            p2 = r.nextInt(12);
        int p3 = r.nextInt(12);
        while (Math.abs(p1 - p3) < 2 || Math.abs(p2 - p3) < 2)
            p3 = r.nextInt(12);
        d[0] = p[p1 * 2];
        d[1] = p[p1 * 2 + 1];
        d[2] = p[p2 * 2];
        d[3] = p[p2 * 2 + 1];
        d[4] = p[p2 * 2];
        d[5] = p[p2 * 2 + 1];
        d[6] = p[p3 * 2];
        d[7] = p[p3 * 2 + 1];
        d[8] = p[p3 * 2];
        d[9] = p[p3 * 2 + 1];
        d[10] = p[p1 * 2];
        d[11] = p[p1 * 2 + 1];
    }

    void move() {
        corX += vecX * velocity * GameContent.fpsNormal / GameContent.fps;
        corY += vecY * velocity * GameContent.fpsNormal / GameContent.fps;
        if (corX - radius > GameContent.width)
            corX = -radius + 1;
        if (corX + radius < 0)
            corX = GameContent.width - radius - 1;
        if (corY - radius > GameContent.height)
            corY = -radius + 1;
        if (corY + radius < 0)
            corY = GameContent.height - radius - 1;
        angle += angleSpeed;
    }

    void draw(Canvas canvas) {
        Paint paint = new Paint();
        double r2 = radius * Math.sqrt(1.25);
        paint.setColor(Color.WHITE);
        paint.setStrokeWidth(5);
        float[] corP = new float[24];
        for (int i = 0; i < 24; i++) {
            if (i % 2 == 0)
                corP[i] = (float) (p[i] * Math.cos(Math.toRadians(angle))
                        - p[i + 1] * Math.sin(Math.toRadians(angle)) + corX);
            else
                corP[i] = (float) (p[i] * Math.cos(Math.toRadians(angle))
                        + p[i - 1] * Math.sin(Math.toRadians(angle)) + corY);
        }
        float[] corD = new float[12];
        for (int i = 0; i < 12; i++) {
            if (i % 2 == 0)
                corD[i] = (float) (d[i] * Math.cos(Math.toRadians(angle))
                        - d[i + 1] * Math.sin(Math.toRadians(angle)) + corX);
            else
                corD[i] = (float) (d[i] * Math.cos(Math.toRadians(angle))
                        + d[i - 1] * Math.sin(Math.toRadians(angle)) + corY);
        }
        canvas.drawLines(corP, paint);
        canvas.drawLines(corD, paint);
    }

    boolean checkCollision(Bullet bullet) {
        double r2 = radius * Math.sqrt(1.25);
        float p1x = (bullet.getCorX() + bullet.getHalfLen() * bullet.getVecX());
        float p1y = (bullet.getCorY() + bullet.getHalfLen() * bullet.getVecY());
        float p2x = (bullet.getCorX() - bullet.getHalfLen() * bullet.getVecX());
        float p2y = (bullet.getCorY() - bullet.getHalfLen() * bullet.getVecY());
        float p3x = (bullet.getCorX());
        float p3y = (bullet.getCorY());
        return (len(corX, corY, p1x, p1y) <= r2 ||
                len(corX, corY, p2x, p2y) <= r2 ||
                len(corX, corY, p3x, p3y) <= r2);
    }

    boolean checkCollision(Glaider glaider) {
        int radius = glaider.getRadius();
        float[] p = new float[14];
        double vecX = glaider.getVecX();
        double vecY = glaider.getVecY();
        float corX = glaider.getCorX();
        float corY = glaider.getCorY();
        double v1 = vecX * Math.cos(Math.toRadians(135)) - vecY * Math.sin(Math.toRadians(135));
        double v2 = vecY * Math.cos(Math.toRadians(135)) + vecX * Math.sin(Math.toRadians(135));
        double v3 = vecX * Math.cos(Math.toRadians(-135)) - vecY * Math.sin(Math.toRadians(-135));
        double v4 = vecY * Math.cos(Math.toRadians(-135)) + vecX * Math.sin(Math.toRadians(-135));
        p[0] = (float) (radius * vecX) + corX;
        p[1] = (float) (radius * vecY) + corY;
        p[2] = (float) (radius * Math.sqrt(2) * v1) + corX;
        p[3] = (float) (radius * Math.sqrt(2) * v2) + corY;
        p[4] = (p[0] + p[2]) / 2;
        p[5] = (p[1] + p[3]) / 2;
        p[6] = (float) (-radius / 2 * vecX) + corX;
        p[7] = (float) (-radius / 2 * vecY) + corY;
        p[8] = (float) (radius * Math.sqrt(2) * v3) + corX;
        p[9] = (float) (radius * Math.sqrt(2) * v4) + corY;
        p[10] = (float) (radius * vecX) + corX;
        p[11] = (float) (radius * vecY) + corY;
        p[12] = (p[8] + p[10]) / 2;
        p[13] = (p[9] + p[11]) / 2;
        double r2 = this.radius * Math.sqrt(1.25);
        for (int i = 0; i < 14; i += 2) {
            if (len(this.corX, this.corY, p[i], p[i + 1]) <= r2)
                return true;
        }
        return false;
    }

    boolean checkCollision(Meteor meteor) {
        double r2 = radius * Math.sqrt(1.25);
        return len(corX, corY, meteor.corX, meteor.corY) <= r2 * 2;
    }

    private float len(float p1x, float p1y, float p2x, float p2y) {
        return (float) Math.sqrt((p1x - p2x) * (p1x - p2x) + (p1y - p2y) * (p1y - p2y));
    }
}
