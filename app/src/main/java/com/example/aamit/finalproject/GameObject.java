package com.example.aamit.finalproject;

import android.graphics.Canvas;

/**
 * This Interface represents a game object in the game.
 *
 * Each class that implements this interface must override two UI methods
 * draw and update that determine how the object appear and move in the view.
 */

interface GameObject {

    void draw(Canvas canvas);

    void update();
}
