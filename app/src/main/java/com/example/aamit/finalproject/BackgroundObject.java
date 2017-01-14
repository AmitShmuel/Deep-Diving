package com.example.aamit.finalproject;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.RectF;
import android.support.annotation.IntDef;
import java.lang.annotation.Retention;

import static com.example.aamit.finalproject.GameView.SAND_SPEED;
import static com.example.aamit.finalproject.GameView.WATER_SPEED;
import static com.example.aamit.finalproject.GameView.screenHeight;
import static com.example.aamit.finalproject.GameView.screenSand;
import static com.example.aamit.finalproject.GameView.screenWidth;
import static com.example.aamit.finalproject.GameViewActivity.gamePaused;
import static com.example.aamit.finalproject.GameViewActivity.rand;
import static java.lang.annotation.RetentionPolicy.CLASS;


/*
 * BackgroundObject
 * This class represent all background objects
 * which do not interact with the main character
 */
class BackgroundObject extends GameObject {

    /**
     * Statics invented here to symbol background objects kinds as integers
     *
     * @ ObjectKind
     */
    public static final int PLANT = 1;
    public static final int BUBBLE = 2;
    public static final int ANIMAL = 3;

    /*
     * ObjectKind
     * Differentiate between background objects kinds as integers
     */
    @Retention(CLASS)
    @IntDef({
            PLANT,
            BUBBLE,
            ANIMAL
    })
    public @interface ObjectKind {}


    Rect objectSrc;
    RectF objectDst = new RectF();
    @ObjectKind int kind;
    float speed;
    private static float bgObjectsGap;


    BackgroundObject(@ObjectKind int kind) {
        this.kind = kind;
    }

    static BackgroundObject[] prepareBackgroundObjects(Bitmap bitmap) {
        BackgroundObject[] objects = new BackgroundObject[] {
                new BackgroundObject(PLANT), new BackgroundObject(PLANT),
                new BackgroundObject(PLANT), new BackgroundObject(PLANT),
                new BackgroundObject(PLANT), new BackgroundObject(PLANT),
                new BackgroundObject(PLANT), new BackgroundObject(PLANT),
                new BackgroundObject(PLANT), new BackgroundObject(PLANT),
                new BackgroundObject(PLANT), new BackgroundObject(PLANT),
                new BackgroundObject(BUBBLE), new BackgroundObject(BUBBLE),
                new BackgroundObject(BUBBLE), new BackgroundObject(BUBBLE)
        };
//        bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.objects);

        int objWidth = (bitmap.getWidth()) / 4;
        int objHeight = (bitmap.getHeight()) / 4;
        int i = 0;
        for (int y = 0; y < 4; y++) { // Bitmap row
            for (int x = 0; x < 4; x++) { // Bitmap column
                objects[i].setBitmap(bitmap);
                objects[i].setSize(objWidth, objHeight);
                objects[i].objectSrc = new Rect(x * objWidth, y * objHeight, (x + 1) * objWidth, (y + 1) * objHeight);
                i++;
            }
        }
        return objects;
    }

    @Override
    void draw(Canvas canvas) {
        int save = canvas.save();
        canvas.drawBitmap(bitmap, objectSrc, objectDst, null);
        if(!gamePaused) {
            switch (kind) {
                case PLANT:
                    objectDst.offsetTo(objectDst.left - SAND_SPEED, objectDst.top);
                    break;
                case BUBBLE:
                    objectDst.offsetTo(objectDst.left, objectDst.top - speed);
                    break;
                case ANIMAL:
                    objectDst.offsetTo(objectDst.left + WATER_SPEED * 2, objectDst.top);
                    break;
            }
        }
        canvas.restoreToCount(save);
    }

    @Override
    void update() {
        switch(kind) {
            case PLANT:
                if(objectDst.right < 0)
                    populate();
                break;
            case BUBBLE:
                if(objectDst.bottom < 0 || objectDst.isEmpty()) {
                    populate();
                }
                break;
            case ANIMAL:
                if(objectDst.left > screenWidth)
                    populate();
        }
    }

    void populate() {

        float initX = 0, initY = 0;
        float randX = rand.nextFloat(), randY = rand.nextFloat();

        switch(kind) {
            case PLANT:
                bgObjectsGap += width*1.5f;
                initX = randX*width + bgObjectsGap;
                if(bgObjectsGap > screenWidth*3) bgObjectsGap = screenWidth*1.5f;
                initY = randY*(screenSand-30) + screenHeight - screenSand + 65;
                break;
            case BUBBLE:
                initY = screenHeight + height;
                initX = randX*(screenWidth-width) + width;
                setSpeed(rand.nextFloat()*5f + 0.3f);
                break;
            case ANIMAL:
                initY = randY*(screenSand-30) + screenHeight - screenSand + 40;
                initX = 0;
                break;
        }
        objectDst.set(initX - width, initY - height, initX, initY);
    }

    void setSpeed(float speed) {
        this.speed = speed;
    }
}