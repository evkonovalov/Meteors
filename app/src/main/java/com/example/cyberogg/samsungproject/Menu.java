package com.example.cyberogg.samsungproject;

import android.content.pm.ActivityInfo;
import android.graphics.Point;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Display;
import android.view.View;
import android.view.Window;
import android.widget.Button;

public class Menu extends AppCompatActivity {

    Menu menu = this;
    GameThread d = new GameThread(false);
    Thread gamethread = new Thread(d);

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
        setContentView(R.layout.activity_menu);
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        GameContent.width = size.x;
        GameContent.height = size.y;
        GameContent.menuOn = true;
        GameContent.gameOver = true;
        GameContent.loading = false;
        Button classic = findViewById(R.id.classic);
        Button mayhem = findViewById(R.id.ufomayhem);
        Button exit = findViewById(R.id.exit);
        gamethread.start();
        classic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!GameContent.loading) {
                    d.requestStop();
                    GameContent.gameOver = false;
                    GameContent.loading = true;
                    GameContent.menuOn = false;
                    new LoadGame(menu, MainActivity.class, 0).execute();
                }
            }
        });
        mayhem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!GameContent.loading) {
                    d.requestStop();
                    GameContent.gameOver = false;
                    GameContent.loading = true;
                    GameContent.menuOn = false;
                    new LoadGame(menu, MainActivity.class, 1).execute();
                }
            }
        });
        exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                menu.finish();
            }
        });
    }

    @Override
    public void onBackPressed() {
        menu.finish();
        super.onBackPressed();
    }
}
