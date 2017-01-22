package com.example.aamit.finalproject.data;


import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.RectF;

import com.example.aamit.finalproject.utilities.MillisecondsCounter;

import static com.example.aamit.finalproject.GameView.screenHeight;
import static com.example.aamit.finalproject.GameView.screenSand;
import static com.example.aamit.finalproject.GameView.screenWidth;
import static com.example.aamit.finalproject.GameView.stagePassed;
import static com.example.aamit.finalproject.GameViewActivity.gamePaused;
import static com.example.aamit.finalproject.GameViewActivity.rand;

/**
 * Coin
 * This class represents a coin
 * in which the main character should collect in order to get higher score
 */
public class Coin extends GameObject{

    private Rect[] bodySrc = new Rect[10]; // ten frames for each sprite
    private Rect scoreDst = new Rect();
    public RectF bodyDst = new RectF();

    private MillisecondsCounter frameCounter = new MillisecondsCounter();
    private MillisecondsCounter populationCounter = new MillisecondsCounter();

    private int frame;
    private final int frameDuration = 50, populateDuration = 3000;
    private boolean collected, canPopulate;
    private boolean firstPopulation = true;


    public static Coin prepareCoin(Bitmap bitmap) {

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

    public void setScorePosition(float screenW) {
        scoreDst.set((int)screenW - (int)this.width - 10, 10, (int)screenW - 10, 10 + (int)this.height);
    }

    @Override
    public void draw(Canvas canvas) {
        canvas.drawBitmap(bitmap, bodySrc[frame], scoreDst, null);
        canvas.drawBitmap(bitmap, bodySrc[frame], bodyDst, null);
        if(collected && !gamePaused) {
            // move the coin to the score direction
            if(bodyDst.left < scoreDst.left) bodyDst.offsetTo(bodyDst.left + 20, bodyDst.top);
            if(bodyDst.top > scoreDst.top ) bodyDst.offsetTo(bodyDst.left, bodyDst.top - 20);
            if(bodyDst.top < scoreDst.top && bodyDst.left > scoreDst.left) {
                /* making him disappear when he reached the top right corner by giving him + 100
                   until he will be populated again to the screen */
                bodyDst.offsetTo(bodyDst.left +100, bodyDst.top - 100);
                collected = false; // done collecting
                canPopulate = true;
            }
        }
    }

    @Override
    public void update() {

        if(firstPopulation) {
            if(populationCounter.timePassed(populateDuration)) {
                populate();
                firstPopulation = false;
            }
        }
        if(canPopulate && stagePassed) {
            populationCounter.restartCount();
            canPopulate = false;
        } else if(!collected && populationCounter.timePassed(populateDuration)) {
            populate();
        }
        // change frame each frameDuration milliseconds for animation
        if(frameCounter.timePassed(frameDuration)) frame = (++frame == 10 ? 0 : frame);
    }

    private void populate() {
        float initY = rand.nextFloat()*(screenHeight - screenSand - height) + height;
        float initX = rand.nextFloat()*screenWidth*0.75f;
        bodyDst.set(initX, initY - height, initX + width, initY);
    }

    public void collected() {collected = true;}

    public boolean isCollected() {return collected;}

    public void stopTime(boolean isPaused) {
        populationCounter.stopTime(isPaused);
    }

    public Rect getScoreRect() { return scoreDst; }
}
