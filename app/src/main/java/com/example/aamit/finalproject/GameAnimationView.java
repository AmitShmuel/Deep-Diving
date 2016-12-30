package com.example.aamit.finalproject;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * Created by aamit on 12/28/2016.
 * Main view of the game
 */

public class GameAnimationView extends android.support.constraint.ConstraintLayout {

    private Character mainChar;
    private Obstacle[] obstacles;
    private float screenWidth, screenHeight;
    boolean touch = false;

    public GameAnimationView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setWillNotDraw(false);


        mainChar = new Character();
        obstacles = new Obstacle[] {
                new Obstacle(),
                new Obstacle(),
                new Obstacle(),
                new Obstacle()
        };
//        System.out.println("==================================");
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        screenWidth = getWidth();
        screenHeight = getHeight();
//        System.out.println(screenHeight);

        mainChar.setSize(100, 200);
        for (Obstacle obstacle: obstacles) {
            obstacle.populate(100, 100);
        }

        super.onSizeChanged(w, h, oldw, oldh);
    }

    @Override
    protected void onDraw(Canvas canvas) {

        mainChar.draw(canvas);
        for (Obstacle obstacle: obstacles) {
            obstacle.draw(canvas);
        }

        postInvalidateOnAnimation();
//        postInvalidateDelayed(100);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch(event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if(mainChar.body.contains(event.getX(), event.getY()))
                    touch = true;
                break;
            case MotionEvent.ACTION_MOVE:
                if(touch == true)
                    mainChar.movePlayer((int) event.getX(), (int) event.getY());
                break;
            case MotionEvent.ACTION_UP:
                touch = false;
        }
        return true;
    }


    class Character extends GameObject{

        Character() {
            paint.setColor(Color.WHITE);
        }

        void setSize(float width, float height) {
            objWidth = width;
            objHeight = height;
            body.set(screenWidth /2 - objWidth/2, screenHeight *0.75f - objHeight/2,
                     screenWidth /2 + objWidth/2, screenHeight *0.75f + objHeight/2);
        }

        void movePlayer(int x, int y) {
            body.offsetTo(x - objWidth/2, y - objHeight);
        }

        @Override
        protected void draw(Canvas canvas) {
            canvas.drawOval(body, paint);
        }
    }

    class Obstacle extends GameObject {

        float speed = 3f;

        Obstacle() {
            paint.setColor(Color.BLACK);
        }

        void populate(float width, float height) {
            objWidth = width;
            objHeight = height;
            float rand, x, y;

            // handle side edges
            rand = (float) Math.random();
            x = rand*screenWidth < objWidth ? rand*screenWidth+objWidth: rand*screenWidth - objWidth;

            rand = (float) Math.random();
            y = -(rand*screenHeight);

            // gotta make a better gap between the obstacles

            body.set(x, y - objHeight/2, x + objWidth, y + objHeight/2);
        }

        @Override
        protected void draw(Canvas canvas) {
//            int save = canvas.save();

            canvas.drawRect(body, paint);
            body.offsetTo(body.left, body.top + speed);

            // end of screen, populate him again
            if (body.top > screenHeight)
                populate(objWidth, objHeight);

            // collision
            if (RectF.intersects(mainChar.body, this.body)) {
                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

//            canvas.restoreToCount(save);
        }
    }
}
