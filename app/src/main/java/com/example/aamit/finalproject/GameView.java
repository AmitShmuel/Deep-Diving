package com.example.aamit.finalproject;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import static android.content.Context.SENSOR_SERVICE;

/**
 *
 * View of the game.
 * It shows an undersea background with sea objects moving around
 */

public class GameView extends View implements SensorEventListener {

    final float SCREEN_SPEED = 0.5f;
    float bgObjectsGap = 0;
    float screenWidth, screenHeight, screenSand = 120;
    boolean collision, touch, gameRunning = true;

    SensorManager sensorManager;
    Sensor accelerometer;
    float sensorX, sensorY;
    long sensorTime;

    enum ObjectKind{PLANT, BUBBLE, ANIMAL}
    Bitmap charactersBitmap, objectsBitmap;
    Background background;
    BackgroundObject[] objects;
    Character[] characters;
    Paint paint = new Paint();
    RectF mainChar = new RectF(0, 0, 70, 70);
    

    public GameView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setWillNotDraw(false);

        sensorManager = (SensorManager) context.getSystemService(SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_GAME);
        sensorTime = System.currentTimeMillis();

        paint.setColor(Color.WHITE);

        background = new Background(BitmapFactory.decodeResource(getResources(), R.drawable.background));

        prepareCharacters();

        prepareBackgroundObjects();

        new Thread(new Runnable() {
            @Override
            public void run() {
                while(gameRunning) {
                    if(screenWidth != 0) {
                        background.update();
                        for (Character ch : characters) ch.update();
                        for (BackgroundObject ob: objects) ob.update();
                        mainCharUpdate();
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

        mainChar.offsetTo(mainChar.left + sensorX, mainChar.top + sensorY);

        for(BackgroundObject ob : objects) ob.draw(canvas);

        for(Character ch : characters) ch.draw(canvas);

        postInvalidateOnAnimation();
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch(event.getAction()) {
            case MotionEvent.ACTION_DOWN:
//                if(mainChar.contains(event.getX(), event.getY()))
                    touch = true;
                break;
            case MotionEvent.ACTION_MOVE:
                if(touch)
//                    mainChar.offsetTo(event.getX()+20, event.getY()-15);
                break;
            case MotionEvent.ACTION_UP:
                touch = false;
        }
        return true;
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        if(sensorEvent.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {

            int elapsedTime = (int)(System.currentTimeMillis() - sensorTime);
            sensorTime = System.currentTimeMillis();

            float ySpeed = 2 * sensorEvent.values[0];
            float xSpeed = sensorEvent.values[1];

            sensorX = Math.abs(xSpeed*elapsedTime/10) > 5 ? xSpeed*elapsedTime/10 : 0;
            sensorY = Math.abs(ySpeed*elapsedTime/10) > 5 ? ySpeed*elapsedTime/10 - 20 : -20;
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {
        // no implementation
    }

    private void mainCharUpdate() {

        if(mainChar.right > screenWidth) {
            mainChar.offsetTo(screenWidth - 70, mainChar.top);
        } else if(mainChar.left < 0) {
            mainChar.offsetTo(0, mainChar.top);
        }
        if(mainChar.bottom > screenHeight-screenSand) {
            mainChar.offsetTo(mainChar.left, screenHeight-screenSand - 70);
        } else if(mainChar.top < 0) {
            mainChar.offsetTo(mainChar.left, 0);
        }
    }

    private void prepareCharacters() {
        characters = new Character[]{
                new Character(1, 1f), new Character(2, 1f), new Character(3, 1f),
                new Character(2, 1f), new Character(2, 1f), new Character(3, 1f),
                new Character(2, 1f), new Character(3, 1f), new Character(3, 1f)
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
                new BackgroundObject(ObjectKind.PLANT), new BackgroundObject(ObjectKind.PLANT),
                new BackgroundObject(ObjectKind.PLANT), new BackgroundObject(ObjectKind.PLANT),
                new BackgroundObject(ObjectKind.PLANT), new BackgroundObject(ObjectKind.PLANT),
                new BackgroundObject(ObjectKind.PLANT), new BackgroundObject(ObjectKind.PLANT),
                new BackgroundObject(ObjectKind.PLANT), new BackgroundObject(ObjectKind.PLANT),
                new BackgroundObject(ObjectKind.PLANT), new BackgroundObject(ObjectKind.PLANT),
                new BackgroundObject(ObjectKind.BUBBLE), new BackgroundObject(ObjectKind.BUBBLE),
                new BackgroundObject(ObjectKind.BUBBLE), new BackgroundObject(ObjectKind.BUBBLE)
        };
        objectsBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.objects);

        int objWidth = (objectsBitmap.getWidth()) / 4;
        int objHeight = (objectsBitmap.getHeight()) / 4;
        int i = 0;
        for (int y = 0; y < 4; y++) { // row
            for (int x = 0; x < 4; x++) { // column
                objects[i].setSize(objWidth, objHeight);
                objects[i].objectSrc = new Rect(x * objWidth, y * objHeight, (x + 1) * objWidth, (y + 1) * objHeight);
                i++;
            }
        }
    }


    class Background extends GameObject{

        Bitmap image;
        float x, y;

        Background(Bitmap image) {
            this.image = image;
            setSize(image.getWidth(), image.getHeight());
        }

        @Override
        void draw(Canvas canvas) {
            int save = canvas.save();

            canvas.scale(screenWidth/width, screenHeight/height);
            canvas.drawBitmap(image, x, y, null);

            if(x < 0){
                canvas.drawBitmap(image, x+width, y, null);
            }
            x -= SCREEN_SPEED;

            canvas.restoreToCount(save);
        }

        @Override
        void update() {
            if(x < -width) {
                x = 0;
            }
        }
    }


    class BackgroundObject extends GameObject {

        Rect objectSrc;
        RectF objectDst = new RectF();
        ObjectKind kind;
        float speed;

        BackgroundObject(ObjectKind kind) {
            this.kind = kind;
        }

        @Override
        void draw(Canvas canvas) {
            int save = canvas.save();

            canvas.drawBitmap(objectsBitmap, objectSrc, objectDst, null);

            switch(kind) {
                case PLANT:
                    objectDst.offsetTo(objectDst.left - SCREEN_SPEED-0.2f, objectDst.top);
                    break;
                case BUBBLE:
                    objectDst.offsetTo(objectDst.left, objectDst.top - speed);
                    break;
                case ANIMAL:
                    objectDst.offsetTo(objectDst.left + SCREEN_SPEED*2, objectDst.top);
                    break;
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
            float rand = (float) Math.random();

            switch(kind) {
                case PLANT:
                    bgObjectsGap += width*1.5f;
                    initX = rand*width + bgObjectsGap;
                    if(bgObjectsGap > screenWidth*3) bgObjectsGap = screenWidth*1.5f;
                    initY = rand*(screenSand-30) + screenHeight - screenSand + 50;
                    break;
                case BUBBLE:
                    initY = screenHeight + height;
                    initX = rand*(screenWidth-width) + width;
                    setSpeed(rand*2 + 0.2f);
                    break;
                case ANIMAL:
                    initY = rand*(screenSand-30) + screenHeight - screenSand + 41;
                    initX = 0;
                    break;
            }
            objectDst.set(initX - width, initY - height, initX, initY);
        }

        void setSpeed(float speed) {
            this.speed = speed;
        }
    }


    class Character extends GameObject {

        Rect[] bodySrc = new Rect[2]; // two frames for each sprite
        RectF bodyDst = new RectF();
        float scale, speed;

        int frame;
        boolean frameFlag = true;
        long frameStartTime, frameDuration = 500;


        Character(int speed, float scale) {
            this.speed = speed;
            this.scale = scale;
        }

        @Override
        void draw(Canvas canvas) {
            int save = canvas.save();

            canvas.scale(scale, scale, screenWidth/2, screenHeight/2);
            canvas.drawBitmap(charactersBitmap, bodySrc[frame], bodyDst, null);
            bodyDst.offsetTo(bodyDst.left - speed, bodyDst.top);

            canvas.restoreToCount(save);
        }

        @Override
        void update() {

            if(bodyDst.right < 0) {
                populate();
            }

            if(RectF.intersects(bodyDst, mainChar)) {
                collision = true;
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
            float rand = (float) Math.random();
            float initY = rand*(screenHeight - screenSand - height) + height;
            float initX = rand*screenWidth + screenWidth;
            bodyDst.set(initX, initY - height, initX + width, initY);
        }
    }
}