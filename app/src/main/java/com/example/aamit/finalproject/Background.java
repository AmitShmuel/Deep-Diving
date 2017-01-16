package com.example.aamit.finalproject;

import android.graphics.Bitmap;
import android.graphics.Canvas;

import static com.example.aamit.finalproject.GameView.screenHeight;
import static com.example.aamit.finalproject.GameView.screenWidth;
import static com.example.aamit.finalproject.GameViewActivity.gamePaused;

/*
 * Background
 * The background of the game, consists of water, and sand on the bottom
 */
class Background extends GameObject{

    private float x, speed; // y doesn't change

    Background(Bitmap bitmap, float speed) {
        this.speed = speed;
        setBitmap(bitmap);
        setSize(bitmap.getWidth(), bitmap.getHeight());
    }

    @Override
    void draw(Canvas canvas) {
        int save = canvas.save();

        canvas.scale(screenWidth/width, screenHeight/height);
        canvas.drawBitmap(bitmap, x, 0, null);

        if(x < 0) canvas.drawBitmap(bitmap, x+width, 0, null);

        if(!gamePaused) x -= speed;

        canvas.restoreToCount(save);
    }

    @Override
    void update() {
        if(x < -width) x = 0;
    }
}