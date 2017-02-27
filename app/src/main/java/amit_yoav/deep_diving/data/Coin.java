package amit_yoav.deep_diving.data;


import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.RectF;

import amit_yoav.deep_diving.utilities.MillisecondsCounter;

import static amit_yoav.deep_diving.GameView.screenHeight;
import static amit_yoav.deep_diving.GameView.screenSand;
import static amit_yoav.deep_diving.GameView.screenWidth;
import static amit_yoav.deep_diving.GameView.stagePassed;
import static amit_yoav.deep_diving.GameViewActivity.gamePaused;
import static amit_yoav.deep_diving.GameViewActivity.rand;

/**
 * Coin
 * This class represents a coin
 * in which the main character should collect in order to get higher score
 */
public class Coin extends GameObject implements Collidable{

    private Rect[] bodySrc = new Rect[10]; // ten frames for each sprite
    private Rect scoreDst = new Rect();
    private RectF bodyDst = new RectF();

    private MillisecondsCounter frameCounter = new MillisecondsCounter();
    private MillisecondsCounter populationCounter = new MillisecondsCounter();

    private final int frameDuration = 50;
    private int frame, populateDuration = 3000;
    private boolean collected, canPopulate;
    private boolean firstPopulation = true;


    public static Coin prepareCoin(Bitmap bitmap) {

        Coin coin = new Coin();
        int coinWidth = (bitmap.getWidth()) / 5;
        int coinHeight = (bitmap.getHeight()) / 2;
        int i = 0;
        coin.setBitmap(bitmap);
        coin.setSize(coinWidth, coinHeight);
        for (int y = 0; y < 2; y++) { // bitmap row
            for (int x = 0; x < 5; x++) { // bitmap column
                coin.bodySrc[i] = new Rect(x * coinWidth, y * coinHeight, (x + 1) * coinWidth, (y + 1) * coinHeight);
                i++;
            }
        }
        return coin;
    }

    public void setScorePosition(float screenW) {
        scoreDst.set((int)screenW - (int)this.width - 10, 10, (int)screenW - 10, 10 + (int)this.height);
    }

    @Override
    public void draw(Canvas canvas) {
        canvas.drawBitmap(bitmap, bodySrc[frame], scoreDst, null);
        canvas.drawBitmap(bitmap, bodySrc[frame], bodyDst, null);
        if(collected && !gamePaused) {
            // move the coin to the score direction
            if(bodyDst.left < scoreDst.left) bodyDst.offsetTo(bodyDst.left + 20, bodyDst.top);
            if(bodyDst.top > scoreDst.top ) bodyDst.offsetTo(bodyDst.left, bodyDst.top - 20);
            if(bodyDst.top < scoreDst.top && bodyDst.left > scoreDst.left) {
                /* making him disappear when he reached the top right corner by giving him + 100
                   until he will be populated again to the screen */
                bodyDst.offsetTo(bodyDst.left +500, bodyDst.top - 500);
                collected = false; // done collecting
                canPopulate = true;
                // we want the coin to populate right after it reaches the score
                populateDuration = 500;
            }
        }
    }

    @Override
    public void update() {

        if(firstPopulation) {
            if(populationCounter.timePassed(populateDuration)) {
                populate();
                firstPopulation = false;
            }
        }
        if(canPopulate && stagePassed) {
            populationCounter.restartCount();
            canPopulate = false;
        } else if(!collected && populationCounter.timePassed(populateDuration)) {
            populate();
            populateDuration = 3000; // back to normal population time
        }
        // change frame each frameDuration milliseconds for animation
        if(frameCounter.timePassed(frameDuration)) frame = (++frame == 10 ? 0 : frame);
    }

    private void populate() {
        float initY = rand.nextFloat()*(screenHeight - screenSand - height) + height;
        float initX = rand.nextFloat()*screenWidth*0.75f;
        bodyDst.set(initX, initY - height, initX + width, initY);
    }

    public void collected() {collected = true;}

    public boolean isCollected() {return collected;}

    public void stopTime(boolean isPaused) {
        populationCounter.stopTime(isPaused);
    }

    Rect getScoreRect() { return scoreDst; }

    @Override
    public RectF getBody() {
        return bodyDst;
    }

    @Override
    public Bitmap getBitmap() {
        return bitmap;
    }
}
