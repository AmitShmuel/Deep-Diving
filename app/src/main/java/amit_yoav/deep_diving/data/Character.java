package amit_yoav.deep_diving.data;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.RectF;

import amit_yoav.deep_diving.utilities.MillisecondsCounter;

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

    public RectF bodyDst = new RectF();
    private Rect[] bodySrc;
    private float scale, speed;
    private MillisecondsCounter frameCounter = new MillisecondsCounter();
    private MillisecondsCounter populateCounter = new MillisecondsCounter();
    private MillisecondsCounter firstPopulateCounter = new MillisecondsCounter();
    private int populateDuration, frameDuration, frame;
    private boolean firstPopulation = true, waitOnFirstPopulation;

    private Character(float speed, float scale, int populateDuration) {
        this.speed = speed;
        this.scale = scale;
        this.populateDuration = populateDuration;
    }

    public static Character[] prepareCharacters(Bitmap greenFishBitmap,
                    Bitmap hammerFishBitmap, Bitmap piranhaFishBitmap, Bitmap greatSharkFishBitmap,
                    Bitmap redFishBitmap, Bitmap goldFishBitmap,Bitmap parrotFishBitmap,
                    Bitmap dogFishBitmap, Bitmap lionFishBitmap, Bitmap swordFishBitmap,
                    Bitmap catFishBitmap) {

        Character[] characters = new Character[]{
                new Character(3,1f,100)/*Gold Fish*/,
                new Character(4.5f,1f,250)/*Dog Fish*/,
                new Character(5,1f,500)/*Parrot Fish*/,
                new Character(6,1f,800)/*Cat Fish*/,
                new Character(6,1f,1000)/*Lion Fish*/,
                new Character(7,1f,1500)/*Green fish*/,
                new Character(9,1f,2500)/*Sword Fish*/,
                new Character(10,1f,2000)/*Red Fish*/,
                new Character(14,1f,3000)/*Piranha Fish*/,
                new Character(15,1f,4000)/*Hammer Shark*/,
                new Character(20,1f,7000)/*Great White Shark*/
        };
        initSpecialFishes(characters, goldFishBitmap, 0, 5, 1, 60);
        initSpecialFishes(characters, dogFishBitmap, 1,5,1,60);
        initSpecialFishes(characters, parrotFishBitmap, 2, 5, 1, 60);
        initSpecialFishes(characters, catFishBitmap, 3, 5, 1, 60);
        initSpecialFishes(characters, lionFishBitmap, 4, 5, 1, 80);
        initSpecialFishes(characters, greenFishBitmap, 5, 6, 1, 50);
        initSpecialFishes(characters, swordFishBitmap, 6, 4, 4, 100);
        initSpecialFishes(characters, redFishBitmap, 7, 5, 1, 100);
        initSpecialFishes(characters, piranhaFishBitmap, 8, 6, 1, 100);
        initSpecialFishes(characters, hammerFishBitmap, 9, 4, 4, 120);
        initSpecialFishes(characters, greatSharkFishBitmap, 10, 11, 1, 100);

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
            canvas.scale(scale, scale, screenWidth / 2, screenHeight / 2);
            canvas.drawBitmap(bitmap, bodySrc[frame], bodyDst, null);
            if (!gamePaused) bodyDst.offsetTo(bodyDst.left - speed, bodyDst.top);
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

        if(bodyDst.right < 0) {
            if(populateCounter.timePassed(populateDuration)) populate();
        }
        // change frame each frameDuration milliseconds for animation
        if(frameCounter.timePassed(frameDuration)) frame = (++frame == bodySrc.length ? 0 : frame);
    }

    private void populate() {
        float initY = rand.nextFloat()*(screenHeight - screenSand - height) + height;
        bodyDst.set(screenWidth, initY - height, screenWidth + width, initY);
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