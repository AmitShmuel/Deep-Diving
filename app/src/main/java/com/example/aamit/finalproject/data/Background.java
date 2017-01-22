package com.example.aamit.finalproject.data;

import android.graphics.Bitmap;
import android.graphics.Canvas;

import static com.example.aamit.finalproject.GameView.SAND_SPEED;
import static com.example.aamit.finalproject.GameView.screenHeight;
import static com.example.aamit.finalproject.GameView.screenWidth;
import static com.example.aamit.finalproject.GameViewActivity.gamePaused;

/*
 * Background
 * The sand background of the game, consists of background_water, and sand with sea objects on the bottom
 */
public class Background extends GameObject{

    private float x, speed; // y doesn't change

    public Background(Bitmap bitmap, float speed) {
        this.speed = speed;
        setBitmap(bitmap);
        setSize(bitmap.getWidth(), bitmap.getHeight());
    }

    @Override
    public void draw(Canvas canvas) {
        int save = canvas.save();

        // sand width is bigger
        if(speed == SAND_SPEED) canvas.scale(screenWidth/width*2, screenHeight/height);
        else canvas.scale(screenWidth/width, screenHeight/height);

        canvas.drawBitmap(bitmap, x, 0, null);

        if(x < 0) canvas.drawBitmap(bitmap, x+width, 0, null);

        if(!gamePaused) x -= speed;

        canvas.restoreToCount(save);
    }

    @Override
    public void update() {
        if(x < -width) x = 0;
    }
}