package com.example.aamit.finalproject;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.RectF;

/**
 * Created by aamit on 12/29/2016.
 */

public abstract class GameObject {

    protected RectF body = new RectF();
    protected Paint paint = new Paint();
    protected float objWidth, objHeight;
    protected int color;

    protected abstract void draw(Canvas canvas);

}
