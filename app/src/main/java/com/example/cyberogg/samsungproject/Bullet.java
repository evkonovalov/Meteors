package com.example.cyberogg.samsungproject;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

class Bullet {
    private float corX;
    private float corY;
    private float velocity = 30;
    private float vecX;
    private float vecY;
    private int halfLen = 10;
    boolean marked = false;
    private int type;

    Bullet(float nCorX, float nCorY, float nVecX, float nVecY, int type) {
        corX = nCorX;
        corY = nCorY;
        vecX = nVecX;
        vecY = nVecY;
        this.type = type;
        if (type != 0)
            velocity = 20;
    }

    int getHalfLen() {
        return halfLen;
    }

    float getCorX() {
        return corX;
    }

    float getCorY() {
        return corY;
    }

    float getVecX() {
        return vecX;
    }

    float getVecY() {
        return vecY;
    }

    void move() {
        corX += vecX * velocity * GameContent.fpsNormal / GameContent.fps;
        corY += vecY * velocity * GameContent.fpsNormal / GameContent.fps;
    }

    void draw(Canvas canvas) {
        Paint paint = new Paint();
        paint.setColor(Color.WHITE);
        if (type == 0)
            paint.setStrokeWidth(5);
        else
            paint.setStrokeWidth(10);
        canvas.drawLine((corX + vecX * halfLen), (corY + vecY * halfLen),
                (corX - vecX * halfLen), (corY - vecY * halfLen), paint);
    }

    boolean isNotBorder() {
        return (!(corX < 0) && !(corX > GameContent.width) && !(corY < 0)
                && !(corY > GameContent.height));
    }

    boolean checkCollision(Glaider glaider) {
        int radius = glaider.getRadius();
        float[] p = new float[16];
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
        p[14] = corX;
        p[15] = corY;
        for (int i = 0; i < 16; i += 2) {
            if (dist(this.corX, this.corY, p[i], p[i + 1]) <= halfLen)
                return true;
        }
        return false;
    }

    boolean checkCollision(Ufo ufo) {
        float b1 = ufo.getCorX() - ufo.width / 2;
        float b2 = ufo.getCorX() + ufo.width / 2;
        float b3 = ufo.getCorY() - ufo.height / 2;
        float b4 = ufo.getCorY() + ufo.height / 2;
        return (corX >= b1 && corX <= b2 && corY >= b3 && corY <= b4);
    }

    private float dist(float p1x, float p1y, float p2x, float p2y) {
        return (float) Math.sqrt((p1x - p2x) * (p1x - p2x) + (p1y - p2y) * (p1y - p2y));
    }
}
