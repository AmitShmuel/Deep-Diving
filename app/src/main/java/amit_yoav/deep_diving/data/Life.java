package amit_yoav.deep_diving.data;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.RectF;

import amit_yoav.deep_diving.utilities.MillisecondsCounter;

import static amit_yoav.deep_diving.GameView.screenHeight;
import static amit_yoav.deep_diving.GameView.screenSand;
import static amit_yoav.deep_diving.GameView.screenWidth;
import static amit_yoav.deep_diving.GameViewActivity.gamePaused;
import static amit_yoav.deep_diving.GameViewActivity.rand;

/**
 * Life
 * This class represents the life of the main character
 * it draws the amount of life on the top right corner with the relevant bitmap
 * and also populated a life item in which the main character should collect in order to live longer
 */

public class Life extends GameObject {

    private Rect bodySrc = new Rect();
    public RectF bodyDst = new RectF();
    public Point lifePoint = new Point();

    private int life = 3;

    private MillisecondsCounter populationCounter = new MillisecondsCounter();
    private boolean canDraw, firstTime = true;

    public int getLife() { return life; }
    public void setLife(int life) { this.life = life; }


    public static Life prepareLife(Bitmap bitmap) {
        Life life = new Life();
        int lifeWidth = bitmap.getWidth();
        int lifeHeight = bitmap.getHeight();
        life.setBitmap(bitmap);
        life.setSize(lifeWidth, lifeHeight);
        life.bodySrc = new Rect(0, 0, lifeWidth, lifeHeight);

        return life;
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
        if(life < 3 && populationCounter.timePassed(50000)) {
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

    public void collected() {
        bodyDst.set(-screenWidth, 0, -screenWidth+width, 0); // out of screen
        canDraw = false;
        populationCounter.restartCount();
    }

    public void setPoint(Coin coinForMeasure) {
        lifePoint.set((int)(coinForMeasure.getScoreRect().left+coinForMeasure.getWidth()/4),
            (int)(coinForMeasure.getHeight()*1.5f));
    }
}
