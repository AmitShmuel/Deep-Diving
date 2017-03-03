package amit_yoav.deep_diving.data;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;

import amit_yoav.deep_diving.MainActivity;
import amit_yoav.deep_diving.R;
import amit_yoav.deep_diving.utilities.MillisecondsCounter;

import static amit_yoav.deep_diving.GameView.isDark;
import static amit_yoav.deep_diving.GameView.screenHeight;
import static amit_yoav.deep_diving.GameView.screenSand;
import static amit_yoav.deep_diving.GameView.screenWidth;
import static amit_yoav.deep_diving.GameViewActivity.gamePaused;
import static amit_yoav.deep_diving.GameViewActivity.rand;

/**
 * Character
 * This class represents all moving fishes
 * which the user should avoid collide with.
 */
public class Character extends GameObject implements Collidable{

    private RectF bodyDst = new RectF();
    private Rect[] bodySrc;
    private float scale, speed;
    private MillisecondsCounter frameCounter = new MillisecondsCounter();
    private MillisecondsCounter populateCounter = new MillisecondsCounter();
    private MillisecondsCounter firstPopulateCounter = new MillisecondsCounter();
    private int populateDuration, frameDuration, frame;
    private boolean firstPopulation = true, waitOnFirstPopulation;
    public boolean killed, populated;
    /*octopus*/
    private float stopGoDown, left, top;
    private boolean isOctopus, drawInk, firstDraw;
    private Bitmap attackBitmap, swimBitmap, inkBitmap;
    private char alpha = 255;
    private Paint inkPaint;
    public boolean term; // a helpful flag which helps to determine if we draw the ink
    public static int octopusIndex = 0;

    private Character(float speed, float scale, int populateDuration) {
        this.speed = speed;
        this.scale = scale;
        this.populateDuration = populateDuration;
    }

    public static Character[] prepareCharacters(Bitmap greenFishBitmap,
                    Bitmap hammerFishBitmap,Bitmap piranhaFishBitmap,   Bitmap greatSharkFishBitmap,
                    Bitmap redFishBitmap,   Bitmap goldFishBitmap,      Bitmap parrotFishBitmap,
                    Bitmap dogFishBitmap,   Bitmap lionFishBitmap,      Bitmap swordFishBitmap,
                    Bitmap catFishBitmap,   Bitmap octopusSwimBitmap,   Bitmap octopusAttackBitmap,
                    Bitmap octopusInkBitmap) {

        Character[] characters = new Character[]{
                new Character(2.5f ,1f,1000)/*octopus*/,
                new Character(3,1f,100)/*Gold Fish*/,
                new Character(4.5f,1f,250)/*Dog Fish*/,
                new Character(5,1f,500)/*Parrot Fish*/,
                new Character(6,1f,800)/*Cat Fish*/,
                new Character(6,1f,1500)/*Lion Fish*/,
                new Character(7,1f,2000)/*Green fish*/,
                new Character(9,1f,2500)/*Sword Fish*/,
                new Character(10,1f,3000)/*Red Fish*/,
                new Character(14,1f,4000)/*Piranha Fish*/,
                new Character(15,1f,6000)/*Hammer Shark*/,
                new Character(20,1f,10000)/*Great White Shark*/
        };
        characters[octopusIndex].isOctopus = true;
        characters[octopusIndex].inkPaint = new Paint();
        characters[octopusIndex].swimBitmap = octopusSwimBitmap;
        characters[octopusIndex].attackBitmap = octopusAttackBitmap;
        characters[octopusIndex].inkBitmap = octopusInkBitmap;
        initSpecialFishes(characters, goldFishBitmap, 6 , 5, 1, 60);
        initSpecialFishes(characters, dogFishBitmap, 1,5,1,60);
        initSpecialFishes(characters, parrotFishBitmap, 2, 5, 1, 60);
        initSpecialFishes(characters, catFishBitmap, 3, 5, 1, 60);
        initSpecialFishes(characters, lionFishBitmap, 4, 5, 1, 80);
        initSpecialFishes(characters, greenFishBitmap, 5, 6, 1, 50);
        initSpecialFishes(characters, octopusSwimBitmap, 0, 4, 4, 200);
        initSpecialFishes(characters, swordFishBitmap, 7, 4, 4, 100);
        initSpecialFishes(characters, redFishBitmap, 8, 5, 1, 100);
        initSpecialFishes(characters, piranhaFishBitmap, 9, 6, 1, 100);
        initSpecialFishes(characters, greatSharkFishBitmap, 10, 11, 1, 100);
        initSpecialFishes(characters, hammerFishBitmap, 11, 4, 4, 120);

        return characters;
    }


    private static void initSpecialFishes(Character[] characters,Bitmap bitmap,
                  int characterIndex, int rowFrames, int columnFrames, int frameDuration) {
        int charWidth = bitmap.getWidth() / rowFrames;
        int charHeight = bitmap.getHeight() / columnFrames;

        characters[characterIndex].setBitmap(bitmap);
        characters[characterIndex].setSize(charWidth, charHeight);
        characters[characterIndex].bodySrc = new Rect[rowFrames*columnFrames];
        characters[characterIndex].frameDuration = frameDuration;
        int frame = 0;
        for (int x = 0; x < rowFrames; x++) { // bitmap row
            for(int y = 0; y < columnFrames; y++) {
                characters[characterIndex].bodySrc[frame] = new Rect(x * charWidth, y * charHeight, (x + 1) * charWidth,(y+1) * charHeight);
                frame++;
            }
        }
    }


    @Override
    public void draw(Canvas canvas) {
        int save = canvas.save();

        if(!firstPopulation) { // just draw..
//            canvas.scale(scale, scale, screenWidth / 2, screenHeight / 2); we aren't scaling.. but we have the option
            // once all "AND" conditions apply, term becomes the cond which determines if we enter this if or not
            if(term || (drawInk && isOctopus && !killed && bodyDst.bottom >= stopGoDown)) {
                if(firstDraw) {
                    left = bodyDst.left;
                    top = bodyDst.top;
                    firstDraw = false;
                    isDark = term = true;
                    MainActivity.soundEffectsUtil.play(R.raw.drop_inks);
                }
                canvas.drawBitmap(inkBitmap, left, top, inkPaint);
                inkPaint.setAlpha(--alpha);
                if(alpha == 0) drawInk = term = false;
            }
            if(killed) canvas.rotate(180, bodyDst.centerX(), bodyDst.centerY());
            canvas.drawBitmap(bitmap, bodySrc[frame], bodyDst, null);
            if (!gamePaused) {
                if(killed || (isOctopus && bodyDst.bottom < stopGoDown)) {
                    bodyDst.offsetTo(bodyDst.left, bodyDst.top + speed);
                }
                else {
                    bodyDst.offsetTo(bodyDst.left - speed, bodyDst.top);
                }
            }
        } // wait before populate (new stage)
        else if (waitOnFirstPopulation || firstPopulateCounter.timePassed(4000)) {
            // after 4 seconds we want to go inside and wait populateDuration time
            waitOnFirstPopulation = true;
            if(populateCounter.timePassed(populateDuration)) {
                firstPopulation = false;
                waitOnFirstPopulation = false;
            }
        }
        canvas.restoreToCount(save);
    }

    @Override
    public void update() {
        if(bodyDst.right < 0 || bodyDst.top > screenHeight) {
            populated = false;
            if(isOctopus) isDark = term = false; // octopus isn't on screen so background back to light
            if(populateCounter.timePassed(populateDuration)) populate();
        }
        if(isOctopus && bodyDst.bottom >= stopGoDown) {
            setBitmap(attackBitmap);
            speed = 4;
            frameDuration = 100;
        }
        // change frame each frameDuration milliseconds for animation
        if(frameCounter.timePassed(frameDuration)) frame = (++frame == bodySrc.length ? 0 : frame);
    }

    private void populate() {
        if(isOctopus) {
            bodyDst.set(screenWidth - width*2, -height, screenWidth - width, 0);
            stopGoDown = rand.nextFloat()*(screenHeight - screenSand - height) + height;
            setBitmap(swimBitmap);
            frameDuration = 84;
            drawInk = firstDraw = true;
            speed = 1.5f;
            alpha = 255;
        } else {
            float initY = rand.nextFloat()*(screenHeight - screenSand - height) + height;
            bodyDst.set(screenWidth, initY - height, screenWidth + width, initY);
        }
        killed = false;
        populated = true;
    }

    public void stopTime(boolean isPaused) {
        populateCounter.stopTime(isPaused);
    }

    public void setFirstPopulation(boolean value) { firstPopulation = value; }

    public void restartPopulation() {
        populateCounter.restartCount();
        populate();
    }

    @Override
    public RectF getBody() {
        return bodyDst;
    }

    @Override
    public Bitmap getBitmap() {
        return bitmap;
    }
}