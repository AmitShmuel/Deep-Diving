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
 * Gun
 * This class represents the Gun of the main character
 * it draws the amount of Gun on the top right corner with the relevant bitmap
 * and also populated a Gun item in which the main character should collect in order to live longer
 */

public class Gun extends GameObject implements Collidable{

    private Rect bodySrc = new Rect();
    private RectF bodyDst = new RectF();

    private MillisecondsCounter populationCounter = new MillisecondsCounter();
    private boolean canDraw, firstTime = true;

    public static Gun prepareGun(Bitmap bitmap) {
        Gun gun = new Gun();
        int gunWidth = bitmap.getWidth();
        int gunHeight = bitmap.getHeight();
        gun.setBitmap(bitmap);
        gun.setSize(gunWidth, gunHeight);
        gun.bodySrc = new Rect(0, 0, gunWidth, gunHeight);
        return gun;
    }

    @Override
    public void draw(Canvas canvas) {
        if(canDraw) {
            canvas.drawBitmap(bitmap, bodySrc, bodyDst, null);
            if(!gamePaused) bodyDst.offsetTo(bodyDst.left - 4f, bodyDst.top);
        }
    }

    @Override
    public void update() {
        if(populationCounter.timePassed(10000)) {
            populate();
            canDraw = true;
        }
        if(bodyDst.right < 0 && canDraw) collected();

        if(firstTime) {
            bodyDst.set(-screenWidth, 0, -screenWidth+width, 0); // out of screen
            firstTime = false;
        }
    }

    private void populate() {
        float initY = rand.nextFloat()*(screenHeight-screenSand-height) + height;
        bodyDst.set(screenWidth, initY - height, screenWidth + width, initY);
    }

    private void collected() {
        bodyDst.set(-screenWidth, 0, -screenWidth+width, 0); // out of screen
        canDraw = false;
        populationCounter.restartCount();
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
