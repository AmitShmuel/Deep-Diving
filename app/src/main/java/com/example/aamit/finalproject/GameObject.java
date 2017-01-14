package com.example.aamit.finalproject;

import android.graphics.Bitmap;
import android.graphics.Canvas;

import java.util.Random;

/**
 * This Interface represents a game object in the game.
 *
 * Each class that implements this interface must override two UI methods
 * draw and update that determine how the object appear and move in the view.
 */

abstract class GameObject {

    Bitmap bitmap;
    float width, height;

    abstract void draw(Canvas canvas);

    abstract void update();

    void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    void setSize(float width, float height) {
        this.width = width;
        this.height = height;
    }
}
