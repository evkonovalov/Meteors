package com.example.cyberogg.samsungproject;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;

import io.github.controlwear.virtual.joystick.android.JoystickView;

public class MainActivity extends AppCompatActivity {

    Activity ac = this;
    public TextView scoreboard;
    public TextView golabel;
    public TextView highscorelabel;
    public Button restart;
    public Button exit;
    GameThread d;
    Thread gamethread;

    @Override
    protected void onDestroy() {
        super.onDestroy();
        d.requestStop();
        boolean retry = true;
        while (retry) {
            try {
                gamethread.join();
                retry = false;
            } catch (InterruptedException e) {
                //
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        setContentView(R.layout.activity_main);
        JoystickView joystick = (JoystickView) findViewById(R.id.joystick);
        scoreboard = findViewById(R.id.score);
        restart = findViewById(R.id.restart);
        golabel = findViewById(R.id.gameover);
        exit = findViewById(R.id.exit);
        highscorelabel = findViewById(R.id.highscore);
        Intent intent = getIntent();
        int type = intent.getIntExtra("Game type", 0);
        Log.d("kek", " " + type);
        if (type == 0) {
            d = new GameThread(this, true, false);
        } else {
            Log.d("kek", "wow -" + type);
            d = new GameThread(this, true, true);
        }
        gamethread = new Thread(d);
        restart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                restart();
            }
        });
        exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!GameContent.loading) {
                    restart();
                    GameContent.gameOver = true;
                    GameContent.menuOn = true;
                    GameContent.loading = true;
                    new LoadGame(ac, Menu.class, 0).execute();
                }
            }
        });
        joystick.setOnMoveListener(new JoystickView.OnMoveListener() {
            @Override
            public void onMove(int angle, int strength) {
                if (!GameContent.gameOver) {
                    synchronized (GameContent.glaiderLock) {
                        if (strength != 0)
                            GameContent.glaider.rotate(Math.toRadians(angle));
                        GameContent.glaider.accelerate(strength);
                    }
                }
            }
        });
        Button shoot = (Button) findViewById(R.id.shoot);
        shoot.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!GameContent.gameOver)
                    GameContent.requestedShots++;
            }
        });

        GameContent.loading = false;
        GameContent.menuOn = false;
        gamethread.start();
        restart();
    }

    private void restart() {
        synchronized (GameContent.frameLock) {
            synchronized (GameContent.meteorLock) {
                GameContent.meteors = new ArrayList<>();
            }
            synchronized (GameContent.bulletLock) {
                GameContent.bullets = new ArrayList<>();
            }
            synchronized (GameContent.ufoBulletLock) {
                GameContent.ufoBullets = new ArrayList<>();
            }
            synchronized (GameContent.glaiderLock) {
                GameContent.glaider.setCor(GameContent.width / 2, GameContent.height / 2);
            }
            synchronized (GameContent.ufoLock) {
                GameContent.ufo = new ArrayList<>();
            }
            GameContent.score = 0;
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    restart.setVisibility(View.GONE);
                    golabel.setVisibility(View.GONE);
                    exit.setVisibility(View.GONE);
                    highscorelabel.setVisibility(View.GONE);
                }
            });
            GameContent.gameOver = false;
        }
    }

    @Override
    public void onBackPressed() {
        restart();
        GameContent.gameOver = true;
        GameContent.menuOn = true;
        new LoadGame(ac, Menu.class, 0).execute();
    }
}
