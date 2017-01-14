package com.example.aamit.finalproject;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.RectF;

import java.util.Random;

import static com.example.aamit.finalproject.GameView.screenHeight;
import static com.example.aamit.finalproject.GameView.screenSand;
import static com.example.aamit.finalproject.GameView.screenWidth;
import static com.example.aamit.finalproject.GameViewActivity.gamePaused;
import static com.example.aamit.finalproject.GameViewActivity.rand;

/*
    * Character
    * This class represents all moving objects
    * which the user should avoid collide with.
    */
class Character extends GameObject {

    Rect[] bodySrc = new Rect[2]; // two frames for each sprite
    RectF bodyDst = new RectF();
    float scale, speed;
    int populateDuration, frame, frameDuration = 500;

    boolean populateTimeFlag = true, frameTimeFlag = true;
    long populateStartTime, frameStartTime;


    Character(int speed, float scale, int populateDuration) {
        this.speed = speed;
        this.scale = scale;
        this.populateDuration = populateDuration;
    }

    static Character[] prepareCharacters(Bitmap bitmap) {
        Character[] characters = new Character[]{
                new Character(4, 1f, 0), new Character(2, 1f, 500), new Character(3, 1f, 500),
                new Character(5, 1f, 0), new Character(2, 1f, 800), new Character(3, 1f, 1000),
                new Character(2, 1f, 1000), new Character(4, 1f, 1500), new Character(6, 1f, 5000)
        };
//        charactersBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.fishes);

        int charWidth = (bitmap.getWidth()) / 6;
        int charHeight = (bitmap.getHeight()) / 3;
        int i = 0;
        for (int y = 0; y < 3; y++) { // bitmap row
            for (int x = 0; x < 6; x+=2) { // bitmap column
                characters[i].setBitmap(bitmap);
                characters[i].setSize(charWidth, charHeight);
                characters[i].bodySrc[0] = new Rect(x * charWidth, y * charHeight, (x + 1) * charWidth, (y + 1) * charHeight);
                characters[i].bodySrc[1] = new Rect((x+1) * charWidth, y * charHeight, (x+2) * charWidth, (y + 1) * charHeight);
                i++;
            }
        }
        return characters;
    }

    @Override
    void draw(Canvas canvas) {
        int save = canvas.save();

        canvas.scale(scale, scale, screenWidth/2, screenHeight/2);
        canvas.drawBitmap(bitmap, bodySrc[frame], bodyDst, null);
        if(!gamePaused) bodyDst.offsetTo(bodyDst.left - speed, bodyDst.top);

        canvas.restoreToCount(save);
    }

    @Override
    void update() {

//        updateif(RectF.intersects(bodyDst, mainChar.body)) {
//            collision = true;
//        }

        if(bodyDst.right < 0) {
            // waiting populateDuration milliseconds until populating
            waitBeforePopulate();
        }
        // change frame each frameDuration milliseconds for animation
        if(frameTimeFlag) {
            frameStartTime = System.currentTimeMillis();
            frameTimeFlag = false;
        }
        if(frameDuration < System.currentTimeMillis() - frameStartTime) {
            frame = (frame == 0 ? 1 : 0);
            frameTimeFlag = true;
        }
    }

    void waitBeforePopulate() {
        if(populateTimeFlag) {
            populateStartTime = System.currentTimeMillis();
            populateTimeFlag = false;
        }
        if(populateDuration < System.currentTimeMillis() - populateStartTime) {
            populate();
            populateTimeFlag = true;
        }
    }

    void populate() {
        float initY = rand.nextFloat()*(screenHeight - screenSand - height) + height;
        bodyDst.set(screenWidth, initY - height, screenWidth + width, initY);
    }
}