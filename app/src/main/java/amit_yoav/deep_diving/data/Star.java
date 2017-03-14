package amit_yoav.deep_diving.data;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.RectF;

import amit_yoav.deep_diving.utilities.MillisecondsCounter;

import static amit_yoav.deep_diving.GameView.screenHeight;
import static amit_yoav.deep_diving.GameView.screenWidth;
import static amit_yoav.deep_diving.GameViewActivity.rand;

/**
 * At the end of the game, stars pop on the screen to congratulate the user
 */

public class Star extends GameObject {

    private RectF bodyDst = new RectF();
    private Rect[] bodySrc;
    private MillisecondsCounter frameCounter = new MillisecondsCounter();
    private int frameDuration, frame;
    public boolean toPopulate, canDraw;
    private float randX, randY;


    public static Star[] prepareStar(Bitmap bitmap) {
        Star[] stars = {
               new Star(), new Star(), new Star(), new Star()
        };

        int starWidth = bitmap.getWidth() / 3;
        int starHeight = bitmap.getHeight() / 3;

        for(int i = 0; i < stars.length; i++) {
            stars[i].setBitmap(bitmap);
            stars[i].setSize(starWidth, starHeight);
            stars[i].bodySrc = new Rect[9];
            stars[i].frameDuration = 50;

            int frame = 0;
            for (int x = 0; x < 3; x++) { // bitmap row
                for(int y = 0; y < 3; y++) {
                    stars[i].bodySrc[frame] = new Rect(x * starWidth, y * starHeight, (x + 1) * starWidth,(y+1) * starHeight);
                    frame++;
                }
            }
        }

        return stars;
    }

    @Override
    public void draw(Canvas canvas) {
        canvas.drawBitmap(bitmap, bodySrc[frame], bodyDst, null);
        bodyDst.offsetTo(randX, randY);
    }

    @Override
    public void update() {
        if(toPopulate) {
            populate();
            toPopulate = false;
            canDraw = true;
        }
        randX = rand.nextFloat()*screenWidth;
        randY = rand.nextFloat()*screenHeight;
        if(frameCounter.timePassed(frameDuration)) frame = (frame+1 == bodySrc.length ? 0 : ++frame);
    }

    private void populate() {
        float initX = rand.nextFloat()*screenWidth;
        float initY = rand.nextFloat()*screenHeight;
        bodyDst.set(initX - width, initY - height, initX, initY);
    }
}
