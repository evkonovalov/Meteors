package com.example.cyberogg.samsungproject;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

class Glaider {
    private float corX = 0;
    private float corY = 0;
    //Velocity length
    private float velocity = 0;
    //Velocity vector
    private float vecX = 1;
    private float vecY = 0;
    private int radius = 20;

    void setCor(int nCorX, int nCorY) {
        corX = nCorX;
        corY = nCorY;
        velocity = 0;
        vecX = 1;
        vecY = 0;
    }

    int getRadius(){
        return radius;
    }

    float getCorX(){
        return corX;
    }

    float getCorY(){
        return corY;
    }

    float getVecX(){
        return vecX;
    }

    float getVecY(){
        return vecY;
    }

    void move() {
        velocity *= 0.99;
        if(velocity < 0)
            velocity = 0;
        corX += vecX * velocity * GameContent.fpsNormal / GameContent.fps;
        corY += vecY * velocity* GameContent.fpsNormal / GameContent.fps;
        if(corX - radius > GameContent.width)
            corX = -radius + 1;
        if(corX + radius < 0)
            corX = GameContent.width - radius - 1;
        if(corY - radius > GameContent.height)
            corY = -radius + 1;
        if(corY + radius < 0)
            corY = GameContent.height - radius - 1;
    }

    void rotate(double angle) {
        vecX = (float) Math.cos(angle);
        vecY = (float) Math.sin(-angle);
    }

    void accelerate(double ac) {
        velocity += ac/200 * GameContent.fpsNormal / GameContent.fps;
        if (velocity > 10)
            velocity = 10;
    }

    void draw(Canvas canvas) {
        Paint paint = new Paint();
        paint.setColor(Color.WHITE);
        paint.setStrokeWidth(5);
        float[] p = new float[16];
        double v1 = vecX * Math.cos(Math.toRadians(135)) - vecY * Math.sin(Math.toRadians(135));
        double v2 = vecY * Math.cos(Math.toRadians(135)) + vecX * Math.sin(Math.toRadians(135));
        double v3 = vecX * Math.cos(Math.toRadians(-135)) - vecY * Math.sin(Math.toRadians(-135));
        double v4 = vecY * Math.cos(Math.toRadians(-135)) + vecX * Math.sin(Math.toRadians(-135));
        p[0] = (float) (radius * vecX) + corX;
        p[1] = (float) (radius * vecY) + corY;
        p[2] = (float) (radius * Math.sqrt(2) * v1) + corX;
        p[3] = (float) (radius * Math.sqrt(2) * v2) + corY;
        p[4] = (float) (radius * Math.sqrt(2) * v1) + corX;
        p[5] = (float) (radius * Math.sqrt(2) * v2) + corY;
        p[6] = (float) (-radius / 2 * vecX) + corX;
        p[7] = (float) (-radius / 2 * vecY) + corY;
        p[8] = (float) (-radius / 2 * vecX) + corX;
        p[9] = (float) (-radius / 2 * vecY) + corY;
        p[10] = (float) (radius * Math.sqrt(2) * v3) + corX;
        p[11] = (float) (radius * Math.sqrt(2) * v4) + corY;
        p[12] = (float) (radius * Math.sqrt(2) * v3) + corX;
        p[13] = (float) (radius * Math.sqrt(2) * v4) + corY;
        p[14] = (float) (radius * vecX) + corX;
        p[15] = (float) (radius * vecY) + corY;
        canvas.drawLines(p, paint);
    }
}
