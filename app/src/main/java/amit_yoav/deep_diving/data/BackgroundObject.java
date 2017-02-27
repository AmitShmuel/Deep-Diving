package amit_yoav.deep_diving.data;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.support.annotation.IntDef;

import java.lang.annotation.Retention;

import static amit_yoav.deep_diving.GameView.screenHeight;
import static amit_yoav.deep_diving.GameView.screenSand;
import static amit_yoav.deep_diving.GameView.screenWidth;
import static amit_yoav.deep_diving.GameViewActivity.gamePaused;
import static amit_yoav.deep_diving.GameViewActivity.rand;
import static java.lang.annotation.RetentionPolicy.CLASS;


/*
 * BackgroundObject
 * This class represent all background_sand objects
 * which do not interact with the main character
 */
public class BackgroundObject extends GameObject {

    /**
     * Statics invented here to symbol background objects kinds as integers
     *
     * @ ObjectKind
     */
    static final int FISH = 1;
    static final int BUBBLE = 2;
    public static final int BUBBLE_LENGTH = 8;


    /*
     * ObjectKind
     * Differentiate between background objects kinds as integers
     */
    @Retention(CLASS)
    @IntDef({
            FISH,
            BUBBLE
    })
    @interface ObjectKind {}

    @ObjectKind public int kind;
    private Rect objectSrc;
    private RectF objectDst = new RectF();
    private float speed;
    private boolean firstPopulation = true;
    private static Paint fishAlphaPaint = new Paint();

    static {
        fishAlphaPaint.setAlpha(100);
    }


    private BackgroundObject(@ObjectKind int kind) {
        this.kind = kind;
    }

    public static BackgroundObject[] prepareBackgroundObjects(Bitmap bubblesBitmap, Bitmap fishesBitmap) {
        BackgroundObject[] objects = new BackgroundObject[] {
                new BackgroundObject(BUBBLE), new BackgroundObject(BUBBLE),
                new BackgroundObject(BUBBLE), new BackgroundObject(BUBBLE),
                new BackgroundObject(BUBBLE), new BackgroundObject(BUBBLE),
                new BackgroundObject(BUBBLE), new BackgroundObject(BUBBLE),
                new BackgroundObject(FISH), new BackgroundObject(FISH), new BackgroundObject(FISH),
                new BackgroundObject(FISH), new BackgroundObject(FISH), new BackgroundObject(FISH),
                new BackgroundObject(FISH), new BackgroundObject(FISH), new BackgroundObject(FISH),
        };

        initObjects(objects, bubblesBitmap, 0, 2, 4);
        initObjects(objects, fishesBitmap, BUBBLE_LENGTH, 3, 3);

        return objects;
    }

    private static void initObjects(BackgroundObject[] objects, Bitmap bitmap, int index, int row, int col) {
        int objWidth = (bitmap.getWidth()) / col;
        int objHeight = (bitmap.getHeight()) / row;
        for (int y = 0; y < row; y++) { // Bitmap row
            for (int x = 0; x < col; x++) { // Bitmap column
                // give speed just to fishes at the moment because it won't change again
                if(index >= BUBBLE_LENGTH) objects[index].setSpeed(rand.nextFloat()*1.5f + 0.3f);
                objects[index].setBitmap(bitmap);
                objects[index].setSize(objWidth, objHeight);
                objects[index].objectSrc = new Rect(x * objWidth, y * objHeight, (x + 1) * objWidth, (y + 1) * objHeight);
                index++;
            }
        }
    }

    @Override
    public void draw(Canvas canvas) {

        if(kind == FISH) canvas.drawBitmap(bitmap, objectSrc, objectDst, fishAlphaPaint);
        else canvas.drawBitmap(bitmap, objectSrc, objectDst, null);

        if(!gamePaused) {
            switch (kind) {
                case FISH:
                    objectDst.offsetTo(objectDst.left - speed, objectDst.top);
                    break;
                case BUBBLE:
                    objectDst.offsetTo(objectDst.left, objectDst.top - speed);
                    break;
            }
        }
    }

    @Override
    public void update() {
        switch(kind) {
            case FISH:
                if(objectDst.right < 0) populate();
                break;
            case BUBBLE:
                if(objectDst.bottom < 0 || objectDst.isEmpty()) populate();
                break;
        }
    }

    private void populate() {

        float initX = 0, initY = 0;
        float randX = rand.nextFloat(), randY = rand.nextFloat();

        switch(kind) {
            case FISH:
                // we want first population on the screen and after that from the right
                if(firstPopulation) {
                    initX = (randX * screenWidth);
                    firstPopulation = false;
                }
                else initX = randX*screenWidth/2 + screenWidth;
                initY = randY*(screenHeight - screenSand - height) + height;
                break;
            case BUBBLE:
                initY = screenHeight + height;
                initX = randX*(screenWidth-width) + width;
                setSpeed(rand.nextFloat()*5f + 0.3f);
                break;
        }
        objectDst.set(initX - width, initY - height, initX, initY);
    }

    private void setSpeed(float speed) {
        this.speed = speed;
    }
}