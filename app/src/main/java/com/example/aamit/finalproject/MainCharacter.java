package com.example.aamit.finalproject;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;

import static com.example.aamit.finalproject.GameView.screenHeight;
import static com.example.aamit.finalproject.GameView.screenSand;
import static com.example.aamit.finalproject.GameView.screenWidth;
import static com.example.aamit.finalproject.GameViewActivity.gamePaused;
import static com.example.aamit.finalproject.GameViewActivity.sensorChanged;
import static com.example.aamit.finalproject.GameViewActivity.xAccel;
import static com.example.aamit.finalproject.GameViewActivity.yAccel;

/*
     * MainCharacter
     * The main character of the game
     */
class MainCharacter extends GameObject{

    RectF body = new RectF(0, 0, 70, 70);
    private Paint paint = new Paint();
    private float sensorX, sensorY;
    private boolean canGetHit = true;

    MainCharacter() {
        paint.setColor(Color.WHITE);
    }

    boolean canGetHit() {return canGetHit;}

    void setCanGetHit(boolean canGetHit) {this.canGetHit = canGetHit;};

    @Override
    void draw(Canvas canvas) {
        canvas.drawRect(body, paint);
        if(!gamePaused) body.offsetTo(body.left + sensorX, body.top + sensorY);
    }

    @Override
    void update() {
        // turns true on onSensorChanged() callback in the activity
        if(sensorChanged) {
            sensorX = Math.abs(xAccel) > 5 ? xAccel : 0;
            sensorY = Math.abs(yAccel) > 5 ? yAccel - 20 : -20;
            sensorChanged = false;
        }
        // block the edges for the main character
        if(body.right > screenWidth - 5) {
            body.offsetTo(screenWidth - 5 - 70, body.top);
        } else if(body.left < 5) {
            body.offsetTo(5, body.top);
        }
        if(body.bottom > screenHeight-screenSand - 5) {
            body.offsetTo(body.left, screenHeight-screenSand - 5 - 70);
        } else if(body.top < 5) {
            body.offsetTo(body.left, 5);
        }
    }
}