package com.example.aamit.finalproject;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

/**
 * 
 * View of the game.
 * It shows an undersea background with sea objects moving around
 */

public class GameView extends View {

//    final int NUM_CHARACTERS = 9;
//    final int NUM_OBJECTS = 8;
    final float screen_dx = 0.5f;
    float bgObjectsGap = 0;
    float screenWidth, screenHeight, screenSand = 120;
    boolean gameRunning = true;

    enum ObjectKind{PLANT, BUBBLE}
    Background background;
    BackgroundObject[] objects;
    Character[] characters;
    Paint paint = new Paint();
    boolean touch = false;
    RectF mainChar = new RectF(0, 0, 70, 70);
    Bitmap charactersBitmap, objectsBitmap;


    public GameView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setWillNotDraw(false);

        paint.setColor(Color.WHITE);

        background = new Background(BitmapFactory.decodeResource(getResources(), R.drawable.background));

        characters = new Character[]{
                new Character(1, 1f), new Character(2, 1f), new Character(3, 1f),
                new Character(2, 1f), new Character(2, 1f), new Character(3, 1f),
                new Character(2, 1f), new Character(3, 1f), new Character(3, 1f)
        };

        objects = new BackgroundObject[] {
                new BackgroundObject(ObjectKind.PLANT), new BackgroundObject(ObjectKind.PLANT), new BackgroundObject(ObjectKind.PLANT),
                new BackgroundObject(ObjectKind.PLANT), new BackgroundObject(ObjectKind.PLANT), new BackgroundObject(ObjectKind.PLANT),
                new BackgroundObject(ObjectKind.PLANT), new BackgroundObject(ObjectKind.PLANT), new BackgroundObject(ObjectKind.PLANT),
                new BackgroundObject(ObjectKind.PLANT), new BackgroundObject(ObjectKind.BUBBLE), new BackgroundObject(ObjectKind.BUBBLE)
        };

        prepareCharacters();

        prepareBackgroundObjects();

        new Thread(new Runnable() {
            @Override
            public void run() {
                while(gameRunning) {
                    if(screenWidth != 0) {
                        background.update();
                        for (Character ch : characters) {
                            ch.update();
                        }
                        for (BackgroundObject ob: objects) {
                            ob.update();
                        }
                    }
                }
            }
        }).start();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        screenWidth = getWidth();
        screenHeight = getHeight();

        super.onSizeChanged(w, h, oldw, oldh);
    }

    @Override
    protected void onDraw(Canvas canvas) {

        background.draw(canvas);

        canvas.drawRect(mainChar, paint);

        for (BackgroundObject backgroundObject : objects) {
            backgroundObject.draw(canvas);
        }

        for (Character character : characters) {
            character.draw(canvas);
        }

        postInvalidateOnAnimation();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch(event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if(mainChar.contains(event.getX(), event.getY()))
                    touch = true;
                break;
            case MotionEvent.ACTION_MOVE:
                if(touch)
                    mainChar.offsetTo(event.getX()+20, event.getY()-15);
                break;
            case MotionEvent.ACTION_UP:
                touch = false;
        }
        return true;
    }


    private void prepareCharacters() {

        int charWidth, charHeight, i = 0;
        charactersBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.fishes);

        charWidth = (charactersBitmap.getWidth()) / 6;
        charHeight = (charactersBitmap.getHeight()) / 3;

        for (int y = 0; y < 3; y++) { // bitmap row
            for (int x = 0; x < 6; x+=2) { // bitmap column
                characters[i].setSize(charWidth, charHeight);
                characters[i].bodySrc[0] = new Rect(x * charWidth, y * charHeight, (x + 1) * charWidth, (y + 1) * charHeight);
                characters[i].bodySrc[1] = new Rect((x+1) * charWidth, y * charHeight, (x+2) * charWidth, (y + 1) * charHeight);
                i++;
            }
        }
    }

    void prepareBackgroundObjects() {

        int objWidth, objHeight, i = 0;
        objectsBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.objects);

        objWidth = (objectsBitmap.getWidth()) / 4;
        objHeight = (objectsBitmap.getHeight()) / 3;

        for (int y = 0; y < 3; y++) { // row
            for (int x = 0; x < 4; x++) { // column
                objects[i].setSize(objWidth, objHeight);
                objects[i].objectSrc = new Rect(x * objWidth, y * objHeight, (x + 1) * objWidth, (y + 1) * objHeight);
                i++;
            }
        }
    }


    class Background implements GameObject{

        Bitmap image;
        float width, height, x, y;

        Background(Bitmap image) {
            this.image = image;
            width = image.getWidth();
            height = image.getHeight();
        }

        @Override
        public void draw(Canvas canvas) {
            int save = canvas.save();

            canvas.scale(screenWidth/width, screenHeight/height);
            canvas.drawBitmap(image, x, y, null);

            if(x < 0){
                canvas.drawBitmap(image, x+width, y, null);
            }
            x -= screen_dx;

            canvas.restoreToCount(save);
        }

        @Override
        public void update() {
            if(x < -width) {
                x = 0;
            }
        }
    }


    class BackgroundObject implements GameObject {

        Rect objectSrc;
        RectF objectDst = new RectF();
        float width, height;
        ObjectKind kind;

        BackgroundObject(ObjectKind kind) {
            this.kind = kind;
        }

        @Override
        public void draw(Canvas canvas) {
            int save = canvas.save();

            canvas.drawBitmap(objectsBitmap, objectSrc, objectDst, null);

            switch(kind) {
                case PLANT:
                    objectDst.offsetTo(objectDst.left - screen_dx, objectDst.top);
                    break;
                case BUBBLE:
                    objectDst.offsetTo(objectDst.left, objectDst.top - screen_dx);
                    break;
            }

            canvas.restoreToCount(save);
        }

        @Override
        public void update() {

//            if((kind == ObjectKind.PLANT && objectDst.right < -screenWidth) ||
//               (kind == ObjectKind.BUBBLE && objectDst.bottom < 0)) {
//                populate();
//            }
            switch(kind) {
                case PLANT:
                    if(objectDst.right < 0)
                        populate();
                    break;
                case BUBBLE:
                    if(objectDst.bottom < 0) {
                        populate();
                    }
                    break;
            }
        }

        void populate() {

            float randX, randY, initX = 0, initY = 0;

            bgObjectsGap += width*2;
            randY = (float) Math.random();
            randX = (float) Math.random();

            switch(kind) {
                case PLANT:
                    initY = randY*(screenSand-30) + screenHeight - screenSand + 41;
                    initX = randX*width + bgObjectsGap;
                    break;
                case BUBBLE:
                    initY = screenHeight + height;
                    initX = randX*screenWidth;
                    break;
            }
            bgObjectsGap %= screenWidth*2;

            objectDst.set(initX - width, initY - height, initX, initY);
        }

        void setSize(int width, int height) {
            this.width = width;
            this.height = height;
        }
    }


    class Character implements GameObject {

        Rect[] bodySrc = new Rect[2]; // two frames for each sprite
        RectF bodyDst = new RectF();
        float scale, speed;
        float width, height;

        int frame;
        boolean frameFlag = true;
        long frameStartTime, frameDuration = 500;


        Character(int speed, float scale) {
            this.speed = speed;
            this.scale = scale;
        }

        @Override
        public void draw(Canvas canvas) {
            int save = canvas.save();

            canvas.scale(scale, scale, screenWidth/2, screenHeight/2);
            canvas.drawBitmap(charactersBitmap, bodySrc[frame], bodyDst, null);
            bodyDst.offsetTo(bodyDst.left - speed, bodyDst.top);

            canvas.restoreToCount(save);
        }

        @Override
        public void update() {

            if(bodyDst.right < 0) {
                populate();
            }

            if(RectF.intersects(bodyDst, mainChar)) {
                System.out.println("HIT+++++++++++++++++++++++++");
            }

            if(frameFlag) {
                frameStartTime = System.currentTimeMillis();
                frameFlag = false;
            }

            if(frameDuration < System.currentTimeMillis() - frameStartTime) {
                frame = (frame == 0 ? 1 : 0);
                frameFlag = true;
            }
        }

        void populate() {
            float initY, rand;
            rand = (float) Math.random();
            initY = rand*(screenHeight - screenSand - height) + height;
            bodyDst.set(screenWidth, initY - height, screenWidth+ width, initY);
        }

        void setSize(int width, int height) {
            this.width = width;
            this.height = height;
        }
    }
}