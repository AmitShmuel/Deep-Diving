package com.example.aamit.finalproject;


import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.RectF;
import android.support.v4.view.animation.FastOutSlowInInterpolator;

import static com.example.aamit.finalproject.GameView.screenHeight;
import static com.example.aamit.finalproject.GameView.screenSand;
import static com.example.aamit.finalproject.GameView.screenWidth;
import static com.example.aamit.finalproject.GameViewActivity.rand;

/*
 * Coin
 * This class represent a coin
 * in which main character should collect in order to get higher score
 */
class Coin extends GameObject{

    private Rect[] bodySrc = new Rect[10]; // ten frames for each sprite
    RectF bodyDst = new RectF();
    private RectF scoreDst = new RectF();

    private int frame, frameDuration = 50, populateDuration = 3000;
    private boolean populateTimeFlag = true, frameTimeFlag = true, collected, canPopulate;
    private long populateStartTime, frameStartTime;


    static Coin prepareCoin(Bitmap bitmap) {

        Coin coin = new Coin();
        int coinWidth = (bitmap.getWidth()) / 5;
        int coinHeight = (bitmap.getHeight()) / 2;
        int i = 0;
        coin.setBitmap(bitmap);
        coin.setSize(coinWidth, coinHeight);
        for (int y = 0; y < 2; y++) { // bitmap row
            for (int x = 0; x < 5; x++) { // bitmap column
                coin.bodySrc[i] = new Rect(x * coinWidth, y * coinHeight, (x + 1) * coinWidth, (y + 1) * coinHeight);
                i++;
            }
        }
        return coin;
    }

    void setScorePosition(float w, float h) {
        scoreDst.set(w - this.width - 10, 10, w - 10, 10 + this.height);
    }

    @Override
    void draw(Canvas canvas) {
        canvas.drawBitmap(bitmap, bodySrc[frame], bodyDst, null);
        if(collected) {
            if(bodyDst.left < scoreDst.left) bodyDst.offsetTo(bodyDst.left + 20, bodyDst.top);
            if(bodyDst.top > scoreDst.top ) bodyDst.offsetTo(bodyDst.left, bodyDst.top - 20);
            if(bodyDst.top < scoreDst.top && bodyDst.left > scoreDst.left) {
                collected = false; // done collecting
                canPopulate = true;
            }
        }
    }

    @Override
    void update() {

        if(canPopulate) {
            restartPopulation();
            canPopulate = false;
        }
        else waitBeforePopulate();

        // change frame each frameDuration milliseconds for animation
        if(frameTimeFlag) {
            frameStartTime = System.currentTimeMillis();
            frameTimeFlag = false;
        }
        if(frameDuration < System.currentTimeMillis() - frameStartTime) {
            frame = (++frame == 10 ? 0 : frame);
            frameTimeFlag = true;
        }
    }

    private void waitBeforePopulate() {
        if(populateTimeFlag) {
            populateStartTime = System.currentTimeMillis();
            populateTimeFlag = false;
        }
        if(populateDuration < System.currentTimeMillis() - populateStartTime) {
            populate();
            populateTimeFlag = true;
        }
    }

    private void populate() {
        float initY = rand.nextFloat()*(screenHeight - screenSand - height) + height;
        float initX = rand.nextFloat()*screenWidth*0.75f;
        bodyDst.set(initX, initY - height, initX + width, initY);
    }

    void restartPopulation() {
        populate();
        populateTimeFlag = true;
    }

    void collected() {collected = true;}
    boolean isCollected() {return collected;}
}
