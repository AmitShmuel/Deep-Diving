package com.example.aamit.finalproject;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.support.annotation.IntDef;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import java.lang.annotation.Retention;
import java.util.Random;

import static com.example.aamit.finalproject.GameViewActivity.gameRunning;
import static com.example.aamit.finalproject.GameViewActivity.sensorChanged;
import static com.example.aamit.finalproject.GameViewActivity.xAccel;
import static com.example.aamit.finalproject.GameViewActivity.yAccel;
import static java.lang.annotation.RetentionPolicy.CLASS;

/**
 *
 * View of the game.
 * It shows an undersea background with sea objects moving around
 */
public class GameView extends View  {

    /*
     * Booleans & View measurement values
     */
    final float WATER_SPEED = 0.3f, SAND_SPEED = 1.5f;
    float bgObjectsGap = 0;
    float screenWidth, screenHeight, screenSand = 120;
    boolean collision, pauseGame;


    /**
     * Statics invented here to symbol background objects kinds as integers
     *
     * @ ObjectKind
     */
    public static final int PLANT = 1;
    public static final int BUBBLE = 2;
    public static final int ANIMAL = 3;

    /**
     * Draw objects
     */
    private Bitmap charactersBitmap, objectsBitmap;
    private Background waterBackground, sandBackground;
    private BackgroundObject[] objects;
    private Character[] characters;
    private MainCharacter mainChar;

    private Random rand = new Random();

    /*
     * Background runnable updating each object on the view
     */
    Runnable updater = new Runnable() {
        @Override
        public void run() {
            while(gameRunning) {
                if(!pauseGame) {
                    if (screenWidth != 0) {
                        waterBackground.update();
                        sandBackground.update();
                        for (Character ch : characters) ch.update();
                        for (BackgroundObject ob : objects) ob.update();
                        mainChar.update();
                    }
                }
            }
        }
    };


    public GameView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setWillNotDraw(false);

        waterBackground = new Background(BitmapFactory.decodeResource(getResources(), R.drawable.water), WATER_SPEED);
        sandBackground = new Background(BitmapFactory.decodeResource(getResources(), R.drawable.sand), SAND_SPEED);

        mainChar = new MainCharacter();

        prepareCharacters();

        prepareBackgroundObjects();

        runUpdater();
    }

    void runUpdater() {
        AsyncHandler.post(updater);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        screenWidth = getWidth();
        screenHeight = getHeight();
        super.onSizeChanged(w, h, oldw, oldh);
    }


    @Override
    protected void onDraw(Canvas canvas) {

        waterBackground.draw(canvas);
        for (Character ch : characters) ch.draw(canvas);
        mainChar.draw(canvas);
        // we want bubbles to come behind the sand
        for (BackgroundObject ob : objects) if(ob.kind == BUBBLE) ob.draw(canvas);
        sandBackground.draw(canvas);
        for (BackgroundObject ob : objects) if(ob.kind == PLANT) ob.draw(canvas);

        postInvalidateOnAnimation();
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if(event.getAction() == MotionEvent.ACTION_UP)
            pauseGame = !pauseGame;
        return true;
    }


    private void prepareCharacters() {
        characters = new Character[]{
                new Character(4, 1f, 0), new Character(2, 1f, 500), new Character(3, 1f, 500),
                new Character(5, 1f, 0), new Character(2, 1f, 800), new Character(3, 1f, 1000),
                new Character(2, 1f, 1000), new Character(4, 1f, 1500), new Character(6, 1f, 5000)
        };
        charactersBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.fishes);

        int charWidth = (charactersBitmap.getWidth()) / 6;
        int charHeight = (charactersBitmap.getHeight()) / 3;
        int i = 0;
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
        objects = new BackgroundObject[] {
                new BackgroundObject(PLANT), new BackgroundObject(PLANT),
                new BackgroundObject(PLANT), new BackgroundObject(PLANT),
                new BackgroundObject(PLANT), new BackgroundObject(PLANT),
                new BackgroundObject(PLANT), new BackgroundObject(PLANT),
                new BackgroundObject(PLANT), new BackgroundObject(PLANT),
                new BackgroundObject(PLANT), new BackgroundObject(PLANT),
                new BackgroundObject(BUBBLE), new BackgroundObject(BUBBLE),
                new BackgroundObject(BUBBLE), new BackgroundObject(BUBBLE)
        };
        objectsBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.objects);

        int objWidth = (objectsBitmap.getWidth()) / 4;
        int objHeight = (objectsBitmap.getHeight()) / 4;
        int i = 0;
        for (int y = 0; y < 4; y++) { // Bitmap row
            for (int x = 0; x < 4; x++) { // Bitmap column
                objects[i].setSize(objWidth, objHeight);
                objects[i].objectSrc = new Rect(x * objWidth, y * objHeight, (x + 1) * objWidth, (y + 1) * objHeight);
                i++;
            }
        }
    }


    /*
     * MainCharacter
     * The main character of the game
     */
    class MainCharacter extends GameObject{

        private Paint paint = new Paint();
        private RectF body = new RectF(0, 0, 70, 70);
        private float sensorX, sensorY;

        MainCharacter() {
            paint.setColor(Color.WHITE);
        }

        @Override
        void draw(Canvas canvas) {
            canvas.drawRect(body, paint);
            if(!pauseGame) body.offsetTo(body.left + sensorX, body.top + sensorY);
        }

        @Override
        void update() {
            // turns true on onSensorChanged() callback in the activity
            if(sensorChanged) {
                sensorX = Math.abs(xAccel) > 5 ? xAccel : 0;
                sensorY = Math.abs(yAccel) > 5 ? yAccel - 20 : -20;
                sensorChanged = false;
            }
            // block the edges for the main character
            if(body.right > screenWidth - 5) {
                body.offsetTo(screenWidth - 5 - 70, body.top);
            } else if(body.left < 5) {
                body.offsetTo(5, body.top);
            }
            if(body.bottom > screenHeight-screenSand - 5) {
                body.offsetTo(body.left, screenHeight-screenSand - 5 - 70);
            } else if(body.top < 5) {
                body.offsetTo(body.left, 5);
            }
        }
    }


    /*
    * Character
    * This class represents all moving objects
    * which the user should avoid collide with.
    */
    class Character extends GameObject {

        Rect[] bodySrc = new Rect[2]; // two frames for each sprite
        RectF bodyDst = new RectF();
        float scale, speed;
        int populateDuration, frame, frameDuration = 500;

        boolean populateTimeFlag = true, frameTimeFlag = true;
        long populateStartTime, frameStartTime;


        Character(int speed, float scale, int populateDuration) {
            this.speed = speed;
            this.scale = scale;
            this.populateDuration = populateDuration;
        }

        @Override
        void draw(Canvas canvas) {
            int save = canvas.save();

            canvas.scale(scale, scale, screenWidth/2, screenHeight/2);
            canvas.drawBitmap(charactersBitmap, bodySrc[frame], bodyDst, null);
            if(!pauseGame) bodyDst.offsetTo(bodyDst.left - speed, bodyDst.top);

            canvas.restoreToCount(save);
        }

        @Override
        void update() {

            if(RectF.intersects(bodyDst, mainChar.body)) {
                collision = true;
            }

            if(bodyDst.right < 0) {
                // waiting populateDuration milliseconds until populating
                waitBeforePopulate();
            }
            // change frame each frameDuration milliseconds for animation
            if(frameTimeFlag) {
                frameStartTime = System.currentTimeMillis();
                frameTimeFlag = false;
            }
            if(frameDuration < System.currentTimeMillis() - frameStartTime) {
                frame = (frame == 0 ? 1 : 0);
                frameTimeFlag = true;
            }
        }

        void waitBeforePopulate() {
            if(populateTimeFlag) {
                populateStartTime = System.currentTimeMillis();
                populateTimeFlag = false;
            }
            if(populateDuration < System.currentTimeMillis() - populateStartTime) {
                populate();
                populateTimeFlag = true;
            }
        }

        void populate() {
            float initY = rand.nextFloat()*(screenHeight - screenSand - height) + height;
            bodyDst.set(screenWidth, initY - height, screenWidth + width, initY);
        }
    }


    /*
     * Background
     * The background of the game, consists of water, and sand on the bottom
     */
    class Background extends GameObject{

        Bitmap bitmap;
        float x, speed; // y doesn't change

        Background(Bitmap bitmap, float speed) {
            this.bitmap = bitmap;
            this.speed = speed;
            setSize(bitmap.getWidth(), bitmap.getHeight());
        }

        @Override
        void draw(Canvas canvas) {
            int save = canvas.save();

            canvas.scale(screenWidth/width, screenHeight/height);
            canvas.drawBitmap(bitmap, x, 0, null);

            if(x < 0) canvas.drawBitmap(bitmap, x+width, 0, null);

            if(!pauseGame) x -= speed;

            canvas.restoreToCount(save);
        }

        @Override
        void update() {
            if(x < -width) x = 0;
        }
    }


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
    /*
     * BackgroundObject
     * This class represent all background objects
     * which do not interact with the main character
     */
    class BackgroundObject extends GameObject {

        Rect objectSrc;
        RectF objectDst = new RectF();
        @ObjectKind int kind;
        float speed;

        BackgroundObject(@ObjectKind int kind) {
            this.kind = kind;
        }

        @Override
        void draw(Canvas canvas) {
            int save = canvas.save();
            canvas.drawBitmap(objectsBitmap, objectSrc, objectDst, null);
            if(!pauseGame) {
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
}