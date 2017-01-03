package com.example.aamit.finalproject;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.RectF;

/**
 * Created by aamit on 12/29/2016.
 */

public abstract class GameObject {

    protected Rect body = new Rect();
    protected Paint paint = new Paint();
    protected int objWidth, objHeight;

    protected abstract void draw(Canvas canvas);

}
