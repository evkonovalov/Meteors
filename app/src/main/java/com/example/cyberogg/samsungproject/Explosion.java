package com.example.cyberogg.samsungproject;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import java.util.ArrayList;
import java.util.Random;

class Explosion {
    private int size = 40;
    private int space = 8;

    class ExplosionFrame {
        ArrayList<Float> pts = new ArrayList<>();

        ExplosionFrame(int rate, float corX, float corY) {
            Random r = new Random();
            int ci = size / 2;
            int cj = size / 2;
            for (int i = 0; i < size; i++) {
                for (int j = 0; j < size; j++) {
                    if ((i - ci) * (i - ci) + (j - cj) * (j - cj) < size / 2) {
                        if (r.nextInt((int) (frames * 1.5)) < rate) {
                            pts.add((float) (i - size / 2) * space + corX);
                            pts.add((float) (j - size / 2) * space + corY);
                        }
                    }
                }
            }
        }

        private ExplosionFrame(ExplosionFrame f) {
            for (Float fl : f.pts)
                pts.add(Float.valueOf(fl));
        }
    }

    private int currentFrame = 0;
    private int frames = 20;
    private boolean finished = false;
    private ArrayList<ExplosionFrame> animation = new ArrayList<>();

    public boolean isFinished() {
        return finished;
    }

    Explosion(float corX, float corY) {
        Random r = new Random();
        space = space + r.nextInt(10) - 4;
        for (int i = 0; i < frames; i++) {
            animation.add(new ExplosionFrame(frames - i, corX, corY));
        }
    }

    void draw(Canvas canvas) {
        if (currentFrame == frames) {
            finished = true;
            return;
        }
        Paint paint = new Paint();
        paint.setColor(Color.WHITE);
        paint.setStrokeWidth(space / 2);
        float[] a = new float[animation.get(currentFrame).pts.size()];
        for (int i = 0; i < a.length; i++) {
            a[i] = animation.get(currentFrame).pts.get(i);
        }
        canvas.drawPoints(a, paint);
        currentFrame++;
    }
}
