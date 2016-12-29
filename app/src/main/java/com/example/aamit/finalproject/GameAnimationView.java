package com.example.aamit.finalproject;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * Created by aamit on 12/28/2016.
 * Main view of the game
 */

public class GameAnimationView extends android.support.constraint.ConstraintLayout {

    private Character mainChar;
//    private Obstacle[] obstacles;
    float viewWidth, viewHeight;

    public GameAnimationView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setWillNotDraw(false);

        mainChar = new Character();
//        obstacles = new Obstacle[] {
//                new Obstacle(),
//                new Obstacle(),
//                new Obstacle()
//        };
//        System.out.println("=================================="+mainChar.position.x);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        viewWidth = getWidth();
        viewHeight = getHeight();

        mainChar.setSize(100, 200);
//        for (Obstacle obstacle: obstacles) {
//        }

        super.onSizeChanged(w, h, oldw, oldh);
    }

    @Override
    protected void onDraw(Canvas canvas) {

        mainChar.draw(canvas);
//        for (Obstacle obstacle: obstacles) {
//            obstacle.draw(canvas);
//        }

        postInvalidateOnAnimation();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch(event.getAction()) {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_MOVE:
                if(event.getY() < viewHeight/2)
                    break;
                mainChar.movePlayer((int) event.getX(), (int) event.getY()-100);
        }

        return true;
    }

    class Character extends GameObject{

        Paint paint = new Paint();

        Character() {
            paint.setColor(Color.WHITE);
        }

        void setSize(float width, float height) {
            objWidth = width;
            objHeight = height;
            body.set(viewWidth/2 - objWidth/2, viewHeight*0.75f - objHeight/2, viewWidth/2 + objWidth/2, viewHeight*0.75f + objHeight/2);
        }

        void movePlayer(int x, int y) {
            body.set(x - body.width()/2, y - body.height()/2, x + body.width()/2, y + body.height()/2);
        }

        @Override
        protected void draw(Canvas canvas) {
            canvas.drawOval(body, paint);
//            System.out.println(body.toString());
        }
    }

//    class Obstacle extends GameObject {
//
//        private final float X_SPEED = 28f;
//
//        Obstacle() {
//
//        }
//
//        @Override
//        protected void draw(Canvas canvas) {
//
//        }
//    }
}
