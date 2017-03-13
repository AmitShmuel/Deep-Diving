package amit_yoav.deep_diving.data;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;

import amit_yoav.deep_diving.utilities.MillisecondsCounter;

import static amit_yoav.deep_diving.GameView.screenHeight;
import static amit_yoav.deep_diving.GameView.screenWidth;
import static amit_yoav.deep_diving.GameViewActivity.gamePaused;
import static amit_yoav.deep_diving.GameViewActivity.rand;

/**
 * Shield
 * This class represents a Shield in which the main character can collect.
 * This shield protects the main character from the fishes.
 */

public class Shield extends GameObject implements Collidable{

    private Rect bodySrc;
    private RectF bodyDst = new RectF();
    private RectF mainCharBody;

    public MillisecondsCounter populationCounter = new MillisecondsCounter();
    private boolean canDraw, collected;
    public boolean populated;

    private float scale[] = {1,1.02f,1.04f,1.06f,1.08f,1.1f};
    private int scaleIndex, maxScaleIndex = 5, scalingInterval, indexChanger = 1;
    private Paint blinker = new Paint();
    public boolean blink;
    private float speed = 7;

    public void setSpeed(float speed) {this.speed = speed;}

    public static Shield prepareShield(Bitmap bitmap, RectF mainCharBody) {
        Shield shield = new Shield();
        int shieldWidth = bitmap.getWidth();
        int shieldHeight = bitmap.getHeight();
        shield.setBitmap(bitmap);
        shield.setSize(shieldWidth, shieldHeight);
        shield.bodySrc = new Rect(0, 0, shieldWidth, shieldHeight);
        shield.mainCharBody = mainCharBody;

        return shield;
    }

    @Override
    public void draw(Canvas canvas) {
        int save = canvas.save();
        if(canDraw) {
            if(scalingInterval++ == 5) { // slowing the animation a bit
                if(scaleIndex == 0) indexChanger = 1;
                else if (scaleIndex == maxScaleIndex) indexChanger = -1;
                scaleIndex += indexChanger;
                scalingInterval = 0;
            }
            if(!gamePaused && collected) { // animating the shield by scaling
                canvas.scale(scale[scaleIndex], scale[scaleIndex], bodyDst.centerX(), bodyDst.centerY());
            }
            canvas.drawBitmap(bitmap, bodySrc, bodyDst, blinker);
            if (!gamePaused) {
                if (!collected) {
                    bodyDst.offsetTo(bodyDst.left, bodyDst.top - speed);
                } else {
                    bodyDst.set(mainCharBody.centerX() - width / 2, mainCharBody.centerY() - height / 2,
                            mainCharBody.centerX() + width / 2, mainCharBody.centerY() + height / 2);
                }
            }
        }
        canvas.restoreToCount(save);
    }

    @Override
    public void update() {
        if(!collected && populationCounter.timePassed(35000)) {//35000
            populate();
            canDraw = true;
        }

        // diver did not collect and the shield passes the screen, we want to restart the time.
        if(bodyDst.bottom < 0 && canDraw) {
            populationCounter.restartCount();
            canDraw = blink = populated = false;
        }

        if(blink) blink();
    }

    private void populate() {
        float initX = rand.nextFloat()*(screenWidth-width) + width;
        float initY = screenHeight + height;
        bodyDst.set(initX - width, initY - height, initX, initY);
        populated = true;
    }

    public void collected() {
        collected = true;
        populated = false;
    }

    public void restart() {
        collected = canDraw = blink = false;
        bodyDst.set(-screenWidth, 0, -screenWidth+width, 0); // out of screen
        populationCounter.restartCount();
        blinker.setAlpha(255);
    }

    @Override
    public RectF getBody() {
        return bodyDst;
    }

    @Override
    public Bitmap getBitmap() {
        return bitmap;
    }

    private void blink() {
        if(blinker.getAlpha() == 50) blinker.setAlpha(255);
        else blinker.setAlpha(50);
    }

    public void stopTime(boolean isPaused) { populationCounter.stopTime(isPaused); }
}
