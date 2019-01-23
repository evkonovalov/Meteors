package com.example.cyberogg.samsungproject;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.view.SurfaceHolder;

import java.util.LinkedList;

public class DrawThread extends Activity implements Runnable {

    LinkedList<Long> times = new LinkedList<Long>() {{
        add(System.nanoTime());
    }};

    private final int MAX_SIZE = 100;
    private final double NANOS = 1000000000.0;

    private final SurfaceHolder surfaceHolder;
    private volatile boolean running = true;

    public DrawThread(Context context, SurfaceHolder surfaceHolder) {
        this.surfaceHolder = surfaceHolder;
    }

    public void requestStop() {
        running = false;
    }


    private double fps() {
        long lastTime = System.nanoTime();
        double difference = (lastTime - times.getFirst()) / NANOS;
        times.addLast(lastTime);
        int size = times.size();
        if (size > MAX_SIZE) {
            times.removeFirst();
        }
        return difference > 0 ? times.size() / difference : 60;
    }

    @Override
    public void run() {
        while (running) {
            synchronized (surfaceHolder) {
                Canvas canvas = surfaceHolder.lockCanvas();
                if (canvas != null) {
                    synchronized (GameContent.frameLock) {
                        GameContent.fps = fps();
                        try {
                            canvas.drawColor(Color.BLACK);
                            synchronized (GameContent.glaiderLock) {
                                if (!GameContent.gameOver)
                                    GameContent.glaider.draw(canvas);
                            }
                            synchronized (GameContent.bulletLock) {
                                for (Bullet bullet : GameContent.bullets) {
                                    bullet.draw(canvas);
                                }
                            }
                            synchronized (GameContent.ufoBulletLock) {
                                for (Bullet bullet : GameContent.ufoBullets) {
                                    bullet.draw(canvas);
                                }
                            }
                            synchronized (GameContent.meteorLock) {
                                for (Meteor meteor : GameContent.meteors) {
                                    meteor.draw(canvas);
                                }
                            }
                            synchronized (GameContent.explosionLock) {
                                for (Explosion e : GameContent.explosions) {
                                    e.draw(canvas);
                                }
                            }
                            synchronized (GameContent.ufoLock) {
                                for (Ufo u : GameContent.ufo) {
                                    u.draw(canvas);
                                }
                            }
                            GameContent.frameReady = false;
                        } finally {
                            surfaceHolder.unlockCanvasAndPost(canvas);
                        }
                    }
                }
            }
        }
    }
}