package com.example.aamit.finalproject;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;

import static com.example.aamit.finalproject.GameView.screenHeight;
import static com.example.aamit.finalproject.GameView.screenSand;
import static com.example.aamit.finalproject.GameView.screenWidth;
import static com.example.aamit.finalproject.GameViewActivity.gamePaused;
import static com.example.aamit.finalproject.GameViewActivity.rand;
import static com.example.aamit.finalproject.GameViewActivity.sensorChanged;
import static com.example.aamit.finalproject.GameViewActivity.xAccel;
import static com.example.aamit.finalproject.GameViewActivity.yAccel;

/*
 * MainCharacter
 * The main character of the game
 */
class MainCharacter extends GameObject{

    RectF bodyDst = new RectF();
    private Rect[] bodySrc = new Rect[16]; // 16 frames for the diver
    private float sensorX, sensorY, frameDuration = 150;
    private boolean canGetHit = true, populated;
    private int frame;
    private MillisecondsCounter frameCounter = new MillisecondsCounter();
    private Paint blinker = new Paint();


    boolean canGetHit() { return canGetHit; }
    void setCanGetHit(boolean value) { this.canGetHit = value; }


    static MainCharacter prepareMainChar(Bitmap bitmap) {

        MainCharacter mainChar = new MainCharacter();
        int charWidth = (bitmap.getWidth()) / 4;
        int charHeight = (bitmap.getHeight()) / 4;
        int i = 0;
        mainChar.setBitmap(bitmap);
        mainChar.setSize(charWidth, charHeight);
        for (int y = 0; y < 4; y++) { // bitmap row
            for (int x = 0; x < 4; x++) { // bitmap column
                mainChar.bodySrc[i] = new Rect(x * charWidth, y * charHeight, (x + 1) * charWidth, (y + 1) * charHeight);
                i++;
            }
        }
        return mainChar;
    }

    @Override
    void draw(Canvas canvas) {
        canvas.drawBitmap(bitmap, bodySrc[frame], bodyDst, blinker);
        if(!gamePaused) bodyDst.offsetTo(bodyDst.left + sensorX, bodyDst.top + sensorY);
    }

    @Override
    void update() {

        if(!populated) {
            float initY = rand.nextFloat()*(screenHeight - screenSand - height) + height;
            float initX = rand.nextFloat()*screenWidth*0.75f;
            bodyDst.set(initX, initY - height, initX + width, initY);
            populated = true;
        }

        // turns true on onSensorChanged() callback in the activity
        if(sensorChanged) {
            sensorX = Math.abs(xAccel) > 5 ? xAccel : 0;
            sensorY = Math.abs(yAccel) > 5 ? yAccel - 20 : -20;
            // making the animation faster according to user movements
            frameDuration = 150 -
                    (Math.abs(sensorX) > Math.abs(sensorY) ? Math.abs(sensorX)*10 : Math.abs(sensorY)*10);
            if(frameDuration < 0) frameDuration = 30;
            sensorChanged = false;
        }

        // block the edges for the main character
        if(bodyDst.right > screenWidth - 5) bodyDst.offsetTo(screenWidth - 5 - width, bodyDst.top);
        else if(bodyDst.left < 5) bodyDst.offsetTo(5, bodyDst.top);

        if(bodyDst.bottom > screenHeight-screenSand - 5) bodyDst.offsetTo(bodyDst.left, screenHeight-screenSand - 5 - height);
        else if(bodyDst.top < 5) bodyDst.offsetTo(bodyDst.left, 5);

        // change frame each frameDuration milliseconds for animation
        if(frameCounter.timePassed((long)frameDuration)) frame = (++frame == 16 ? 0 : frame);
    }

    void makeVisible() { blinker.setAlpha(255); }

    void blink() {
        if(blinker.getAlpha() == 50) blinker.setAlpha(255);
        else blinker.setAlpha(50);
    }
}