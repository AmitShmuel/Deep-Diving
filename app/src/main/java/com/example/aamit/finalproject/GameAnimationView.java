package com.example.aamit.finalproject;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by aamit on 12/28/2016.
 * Main view of the game
 */

public class GameAnimationView extends View {

//    Paint paint = new Paint();
    static final int NUM_FRAMES = 9;
    Bitmap charactersBitmap;
    Background background;
    Character[] characters;
    float screenWidth, screenHeight;
    int charWidth, charHeight;
    int frameNum;
    Rect[] frames = new Rect[NUM_FRAMES];
    boolean touch = false;
    int screenSandStart;


    public GameAnimationView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setWillNotDraw(false);

        background = new Background(BitmapFactory.decodeResource(getResources(), R.drawable.background));
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        screenWidth = getWidth();
        screenHeight = getHeight();
        screenSandStart = (int)screenHeight - 120;

        prepareCharacters();

        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }


    @Override
    protected void onDraw(Canvas canvas) {

        background.draw(canvas);
        for (Character character: characters) {
            character.draw(canvas);
        }
//        canvas.drawLine(screenWidth, screenHeight-120, 0, screenHeight-120, paint);
        postInvalidateOnAnimation();
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


    private void prepareCharacters() {

        characters = new Character[]{

                new Character(1, 1.1f), new Character(2, 0.6f),
                new Character(5, 1.2f), new Character(3, 1.2f),
                new Character(2, 0.8f), new Character(1, 0.5f),
                new Character(2, 1f), new Character(3, 1.1f), new Character(3, 0.9f)
        };

        charactersBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.fishes);

        charWidth = (charactersBitmap.getWidth()) / 6;
        charHeight = (charactersBitmap.getHeight()) / 3;

//        System.out.println("==============="+charHeight+" "+charWidth);
//        System.out.println("==============="+charactersBitmap.getHeight()+" "+charactersBitmap.getWidth());

        float rand, initHeight;


        int i = 0; // rect index
        for (int y = 0; y < 3; y++) { // row
            for (int x = 0; x < 6; x+=2) { // column
                rand = (float) Math.random();
                initHeight = rand*screenSandStart + charHeight;
                characters[i].bodyDst = new Rect((int)screenWidth + 100 - charWidth, (int)initHeight - charHeight, (int)screenWidth + 100, (int)initHeight);
                characters[i].bodySrc[0] = new Rect(x * charWidth, y * charHeight, (x + 1) * charWidth, (y + 1) * charHeight);

                characters[i].bodySrc[1] = new Rect((x+1) * charWidth, y * charHeight, (x+2) * charWidth, (y + 1) * charHeight);
                System.out.println(i++);
//                if (i >= NUM_FRAMES) {
//                    break;
//                }
            }
        }
//        System.out.println("==============================="+screenHeight);
    }

    class Background {

        Bitmap image;
        float width, height;
        float x, y, dx = 0.2f;

        public Background(Bitmap image) {
            this.image = image;
            width = image.getWidth();
            height = image.getHeight();
        }

        public void draw(Canvas canvas) {
            int save = canvas.save();

            canvas.scale(screenWidth/width +0.1f, screenHeight/height);
            canvas.drawBitmap(image, x, y, null);

            if(x < 0){
                canvas.drawBitmap(image, x+width, y, null);
            }

            x -= dx;
            if(x < -width) {
                x = 0;
            }
            canvas.restoreToCount(save);
        }
    }

    class Character {

        Rect[] bodySrc = new Rect[2]; // two frame for each sprite
        Rect bodyDst;
        float scale;
        int speed;
        int animation = 0;
        int frame = 0;

        Character(int speed, float scale) {
            this.speed = speed;
            this.scale = scale;
        }

        void draw(Canvas canvas) {

            int save = canvas.save();

            canvas.scale(scale, scale, screenWidth/2, screenHeight/2);
            canvas.drawBitmap(charactersBitmap, bodySrc[frame], bodyDst, null);
            bodyDst.offsetTo(bodyDst.left - speed, bodyDst.top);
            if(animation++ % 15 == 0) {
                frame++;
                frame %= 2;
            }

            canvas.restoreToCount(save);
        }
    }
}