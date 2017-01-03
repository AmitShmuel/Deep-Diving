package com.example.aamit.finalproject;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by aamit on 12/28/2016.
 * Main view of the game
 */

public class GameAnimationView extends View {

    Paint paint = new Paint();
    static final int NUM_FRAMES = 1;
    Bitmap charactersBitmap;
    Background background;
    Character[] characters;
    int screenWidth, screenHeight;
    int charWidth, charHeight;
    int frameNum;
    Rect[] frames = new Rect[NUM_FRAMES];
    Rect dest;
    boolean touch = false;
    int screenSandStart;
    int bgHeight, bgWidth;
//    private Character mainChar;


    public GameAnimationView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setWillNotDraw(false);

//        paint.setColor(Color.BLACK);
//        mainChar = new Character();
        characters = new Character[]{
//                new Character(),
//                new Character(),
//                new Character(),
                new Character()
        };

        background = new Background();
        prepareCharacters();

//        System.out.println("==================================");
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        screenWidth = getWidth();
        screenHeight = getHeight();
        screenSandStart = screenHeight - 120;
//        System.out.println(screenHeight);
        float rand, x, y;

        // handle side edges
//        rand = (float) Math.random();
//        x = rand*screenWidth < charWidth ? rand*screenWidth+charWidth: rand*screenWidth - charWidth;

        rand = (float) Math.random();
        y = rand*screenSandStart + charHeight +1;
        dest = new Rect(screenWidth + 100 - charWidth, (int)y - charHeight, screenWidth + 100, (int)y);
//        mainChar.setSize(100, 200);
//        for (Obstacle obstacle: obstacles) {
//            obstacle.populate(100, 100);
//        }

        super.onSizeChanged(w, h, oldw, oldh);
    }

    @Override
    protected void onDraw(Canvas canvas) {

//        mainChar.draw(canvas);
        background.draw(canvas);
        for (Character character: characters) {
            character.draw(canvas);
        }
//        canvas.drawLine(screenWidth, screenHeight-120, 0, screenHeight-120, paint);

        postInvalidateOnAnimation();
//        postInvalidateDelayed(100);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        /*switch(event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if(mainChar.body.contains(event.getX(), event.getY()))
                    touch = true;
                break;
            case MotionEvent.ACTION_MOVE:
                if(touch)
                    mainChar.movePlayer((int) event.getX(), (int) event.getY());
                break;
            case MotionEvent.ACTION_UP:
                touch = false;
        }*/
        return true;
    }


    private void prepareBackground() {

    }


    private void prepareCharacters() {
        charactersBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.fish_1);

        charWidth = (charactersBitmap.getWidth()); //- 64) / 12;
        charHeight = (charactersBitmap.getHeight()); //- 292) / 6*;

        int i = 0; // rect index
        for (int y = 0; y < 1; y++) { // row
            for (int x = 0; x < 1; x++) { // column
                characters[i].body = new Rect(x * charWidth, y * charHeight, (x + 1) * charWidth, (y + 1) * charHeight);
                i++;
                if (i >= NUM_FRAMES) {
                    break;
                }
            }
        }
        //obstacles[0].body.set(0, 0, mCharWidth, mCharHeight);
//        for (Obstacle obstacle: obstacles) {
//            obstacle.body.set(objectsBitmap.getWidth();
//            obstacle.objHeight = objectsBitmap.getHeight();
//        }
    }

    class Background {

        Bitmap image;
        int width, height;
        int x, y, dx = 1;

        public Background() {
            image = BitmapFactory.decodeResource(getResources(), R.drawable.background);
            width = image.getWidth();
            height = image.getHeight();
        }

        public void draw(Canvas canvas) {

            int save = canvas.save();

            canvas.scale(screenWidth/width, screenHeight/height);
            canvas.drawBitmap(image, x, y, null);

            if(x < 0){
                canvas.scale(screenWidth/width, screenHeight/height);
                canvas.drawBitmap(image, x-screenWidth, y, null);
            }

            x -= dx;
            if(x < -screenWidth) {
                x = 0;
            }

            canvas.restoreToCount(save);
        }
    }

    class Character {

        Rect body;
        int objWidth, objHeight;
        float scale;
        int tx = 0;
        int speed = 3;

        Character() {
            //scale = ?;
        }

//        void setSize(float width, float height) {
//            objWidth = width;
//            objHeight = height;
//            body.set(screenWidth / 2 - objWidth / 2, screenHeight * 0.75f - objHeight / 2,
//                    screenWidth / 2 + objWidth / 2, screenHeight * 0.75f + objHeight / 2);
//        }

        void movePlayer(int x, int y) {
            body.offsetTo(x - objWidth / 2, y - objHeight);
        }

        protected void draw(Canvas canvas) {

//            int save = canvas.save();

            canvas.drawBitmap(charactersBitmap, body, dest, null);
            dest.offsetTo(dest.left - speed, dest.top);
//            canvas.translate(tx + charWidth / 2, (screenHeight - charHeight));
//            tx += speed;
//            canvas.restoreToCount(save);
        }
    }
}

 /*   class Obstacle extends GameObject {

        float speed = 3f;

        Obstacle() {
            paint.setColor(Color.BLACK);
        }

        void populate(float width, float height) {
//            objWidth = width;
//            objHeight = height;
            float rand, x, y;

            // handle side edges
            rand = (float) Math.random();
            x = rand*screenWidth < objWidth ? rand*screenWidth+objWidth: rand*screenWidth - objWidth;

            rand = (float) Math.random();
            y = -(rand*screenHeight);

            // gotta make a better gap between the obstacles

            body.set((int)x, (int)y - objHeight/2, (int)x + objWidth, (int)y + objHeight/2);
        }

        @Override
        protected void draw(Canvas canvas) {
//            int save = canvas.save();

            canvas.drawBitmap(objectsBitmap, body, dst, null);
            canvas.drawRect(body, paint);
            body.offsetTo(body.left, body.top + (int)speed);

            // end of screen, populate him again
            if (body.top > screenHeight)
                populate(objWidth, objHeight);

            // collision
//            if (RectF.intersects(mainChar.body, this.body)) {
//                try {
//                    Thread.sleep(200);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//            }

//            canvas.restoreToCount(save);
        }
    }
}
*/