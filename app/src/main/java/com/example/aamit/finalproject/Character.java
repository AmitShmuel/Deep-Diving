package com.example.aamit.finalproject;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.RectF;

import static com.example.aamit.finalproject.GameView.screenHeight;
import static com.example.aamit.finalproject.GameView.screenSand;
import static com.example.aamit.finalproject.GameView.screenWidth;
import static com.example.aamit.finalproject.GameViewActivity.gamePaused;
import static com.example.aamit.finalproject.GameViewActivity.rand;

/**
 * Character
 * This class represents all moving objects
 * which the user should avoid collide with.
*/
class Character extends GameObject {

    RectF bodyDst = new RectF();
    private Rect[] bodySrc = new Rect[2]; // two frames for each sprite
    private float scale, speed;
    private MillisecondsCounter frameCounter = new MillisecondsCounter();
    private MillisecondsCounter populationCounter = new MillisecondsCounter();
    private int populateDuration, frame, frameDuration = 500;


    private Character(int speed, float scale, int populateDuration) {
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

        if(bodyDst.right < 0) {
            if(populationCounter.timePassed(populateDuration)) populate();
        }
        // change frame each frameDuration milliseconds for animation
        if(frameCounter.timePassed(frameDuration)) frame = (frame == 0 ? 1 : 0);
    }

    private void populate() {
        float initY = rand.nextFloat()*(screenHeight - screenSand - height) + height;
        bodyDst.set(screenWidth, initY - height, screenWidth + width, initY);
    }
}