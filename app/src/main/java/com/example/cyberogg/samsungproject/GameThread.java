package com.example.cyberogg.samsungproject;

import android.app.Activity;
import android.view.View;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

public class GameThread extends Activity implements Runnable {
    private volatile boolean running = true;
    private boolean shown = false;
    private Random r = new Random();
    private MainActivity main;
    private boolean isGame;
    private boolean isUfoMayhem = false;

    GameThread(MainActivity main, boolean isGame, boolean isUfoMayhem) {
        this.isGame = isGame;
        this.main = main;
        this.isUfoMayhem = isUfoMayhem;
    }

    GameThread(boolean isGame) {
        this.isGame = isGame;
    }

    private void ufoControl() {
        boolean spawn;
        if (isUfoMayhem) {
            spawn = (r.nextInt((int) (GameContent.fps)) == 0);
        } else {
            spawn = r.nextInt((int) (GameContent.fps * 20)) == 0 && GameContent.ufo.size() < 3;
        }
        if (spawn) {
            Ufo ufo = new Ufo(r.nextBoolean());
            ufo.isAlive = true;
            switch (r.nextInt(4)) {
                case 0:
                    ufo.setCor(GameContent.width, r.nextInt(GameContent.height));
                    break;
                case 1:
                    ufo.setCor(0, r.nextInt(GameContent.height));
                    break;
                case 2:
                    ufo.setCor(r.nextInt(GameContent.width), GameContent.height);
                    break;
                case 3:
                    ufo.setCor(r.nextInt(GameContent.width), 0);
                    break;
            }
            GameContent.ufo.add(ufo);
        }
        for (Ufo u : GameContent.ufo) {
            synchronized (GameContent.bulletLock) {
                for (Bullet bullet : GameContent.bullets) {
                    if (bullet.checkCollision(u)) {
                        bullet.marked = true;
                        u.isAlive = false;
                        synchronized (GameContent.explosionLock) {
                            GameContent.explosions.add(new Explosion(u.getCorX(), u.getCorY()));
                        }
                        if (u.isStupid)
                            GameContent.score += 500;
                        else
                            GameContent.score += 1000;
                        break;
                    }
                }
            }
            synchronized (GameContent.ufoBulletLock) {
                for (Bullet bullet : GameContent.ufoBullets) {
                    if (bullet.checkCollision(GameContent.glaider)) {
                        bullet.marked = true;
                        synchronized (GameContent.explosionLock) {
                            GameContent.explosions.add(new Explosion(GameContent.glaider.getCorX(),
                                    GameContent.glaider.getCorY()));
                        }
                        GameContent.gameOver = true;
                        break;
                    }
                }
            }
        }
        ArrayList<Ufo> nufo = new ArrayList<>();
        for (Ufo u : GameContent.ufo) {
            if (u.isAlive) {
                nufo.add(u);
                synchronized (GameContent.ufoBulletLock) {
                    u.doSomething();
                }
            }
        }
        GameContent.ufo = nufo;
    }

    void meteorsControl() {
        if (r.nextInt((int) (GameContent.fps * 0.8)) == 0 && !isUfoMayhem) {
            GameContent.meteors.add(new Meteor());
        }
        ArrayList<Meteor> nMeteors = new ArrayList<>();
        for (Meteor meteor : GameContent.meteors) {
            meteor.move();
            if (meteor.checkCollision(GameContent.glaider) && isGame) {
                synchronized (GameContent.explosionLock) {
                    GameContent.explosions.add(new Explosion(meteor.getCorX(), meteor.getCorY()));
                    GameContent.explosions.add(new Explosion(GameContent.glaider.getCorX(),
                            GameContent.glaider.getCorY()));
                }
                meteor.marked = true;
                GameContent.gameOver = true;
                break;
            }
            for (Meteor meteor2 : GameContent.meteors) {
                if (meteor != meteor2 && meteor.checkCollision(meteor2)) {
                    meteor2.marked = true;
                    meteor.marked = true;
                    synchronized (GameContent.explosionLock) {
                        GameContent.explosions.add(new Explosion(meteor.getCorX(),
                                meteor.getCorY()));
                        GameContent.explosions.add(new Explosion(meteor2.getCorX(),
                                meteor2.getCorY()));
                    }
                    break;
                }
            }
            if (!meteor.marked) {
                for (Bullet bullet : GameContent.bullets) {
                    if (meteor.checkCollision(bullet)) {
                        bullet.marked = true;
                        meteor.marked = true;
                        synchronized (GameContent.explosionLock) {
                            GameContent.explosions.add(new Explosion(meteor.getCorX(),
                                    meteor.getCorY()));
                        }
                        GameContent.score += 100;
                        break;
                    }
                }
            }
        }
        for (Meteor meteor : GameContent.meteors) {
            if (!meteor.marked)
                nMeteors.add(meteor);
        }
        GameContent.meteors = nMeteors;

    }

    void glaiderControl() {
        GameContent.glaider.move();
    }

    void bulletControl() {
        if (GameContent.requestedShots > 0) {
            GameContent.bullets.add(new Bullet(GameContent.glaider.getCorX(),
                    GameContent.glaider.getCorY(), GameContent.glaider.getVecX(),
                    GameContent.glaider.getVecY(), 0));
            GameContent.requestedShots--;
        }
        ArrayList<Bullet> nBullets = new ArrayList<>();
        for (Bullet bullet : GameContent.bullets) {
            bullet.move();
            if (bullet.isNotBorder() && !bullet.marked)
                nBullets.add(bullet);
        }
        GameContent.bullets = nBullets;
    }

    void ufoBulletControl() {
        ArrayList<Bullet> nUfoBullets = new ArrayList<>();
        for (Bullet bullet : GameContent.ufoBullets) {
            bullet.move();
            if (bullet.isNotBorder() && !bullet.marked)
                nUfoBullets.add(bullet);
        }
        GameContent.ufoBullets = nUfoBullets;
    }

    void explosionControl() {
        ArrayList<Explosion> nExplosions = new ArrayList<>();
        for (Explosion e : GameContent.explosions) {
            if (!e.isFinished())
                nExplosions.add(e);
        }
        GameContent.explosions = nExplosions;
    }

    public void requestStop() {
        running = false;
    }

    private void writeHighScore() {
        int oldHighScore = 0;
        String fileName;
        if (isUfoMayhem)
            fileName = "data2.dat";
        else
            fileName = "data.dat";
        try {
            FileInputStream fIn = main.openFileInput(fileName);
            InputStreamReader isr = new InputStreamReader(fIn);
            Scanner sc = new Scanner(isr);
            oldHighScore = sc.nextInt();
            isr.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (oldHighScore < GameContent.score) {
            try {
                FileOutputStream fOut = main.openFileOutput(fileName,
                        MODE_PRIVATE);
                OutputStreamWriter osw = new OutputStreamWriter(fOut);
                PrintWriter pw = new PrintWriter(osw);
                pw.write(Integer.toString(GameContent.score));
                pw.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private int getHighScore() {
        String fileName;
        if (isUfoMayhem)
            fileName = "data2.dat";
        else
            fileName = "data.dat";
        try {
            FileInputStream fIn = main.openFileInput(fileName);
            InputStreamReader isr = new InputStreamReader(fIn);
            Scanner sc = new Scanner(isr);
            return sc.nextInt();
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    @Override
    public void run() {
        while (running) {
            if (!GameContent.frameReady && !GameContent.loading) {
                synchronized (GameContent.frameLock) {
                    if (!GameContent.gameOver)
                        shown = false;
                    synchronized (GameContent.glaiderLock) {
                        if (isGame)
                            glaiderControl();
                    }
                    synchronized (GameContent.meteorLock) {
                        meteorsControl();
                    }
                    synchronized (GameContent.ufoLock) {
                        ufoControl();
                    }
                    synchronized (GameContent.bulletLock) {
                        bulletControl();
                    }
                    synchronized (GameContent.ufoBulletLock) {
                        ufoBulletControl();
                    }
                    synchronized (GameContent.explosionLock) {
                        explosionControl();
                    }
                    if (GameContent.gameOver && isGame) {
                        if (!shown) {
                            writeHighScore();
                            final int h = getHighScore();
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    main.highscorelabel.setText(main.getString(
                                            R.string.high_score) + " " + GameContent.score);
                                    main.restart.setVisibility(View.VISIBLE);
                                    main.golabel.setVisibility(View.VISIBLE);
                                    main.exit.setVisibility(View.VISIBLE);
                                    main.highscorelabel.setVisibility(View.VISIBLE);
                                }
                            });
                            shown = true;
                        }
                    }
                    if (isGame) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                main.scoreboard.setText(
                                        main.getString(R.string.score) + " " + GameContent.score);
                            }
                        });
                    }
                    GameContent.frameReady = true;
                }
            }
        }
    }
}
