package com.example.cyberogg.samsungproject;

import java.util.ArrayList;

public class GameContent {
    static Glaider glaider = new Glaider();
    public static int width;
    public static int height;
    static int requestedShots = 0;
    static ArrayList<Bullet> bullets = new ArrayList<>();
    static ArrayList<Bullet> ufoBullets = new ArrayList<>();
    static ArrayList<Meteor> meteors = new ArrayList<>();
    static ArrayList<Explosion> explosions = new ArrayList<>();
    static boolean gameOver = false;
    static int score = 0;
    static ArrayList<Ufo> ufo = new ArrayList<>();
    static final Object glaiderLock = new Object();
    static final Object ufoLock = new Object();
    static final Object bulletLock = new Object();
    static final Object ufoBulletLock = new Object();
    static final Object meteorLock = new Object();
    static final Object explosionLock = new Object();
    static final Object frameLock = new Object();
    static boolean frameReady = false;
    static boolean menuOn = true;
    static boolean loading = false;
    static double fps = 60;
    static double fpsNormal = 60;
}
