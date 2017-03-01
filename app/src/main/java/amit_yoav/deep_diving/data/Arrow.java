package amit_yoav.deep_diving.data;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.RectF;

import static amit_yoav.deep_diving.GameView.hit;
import static amit_yoav.deep_diving.GameView.screenWidth;
import static amit_yoav.deep_diving.GameViewActivity.gamePaused;

/**
 * Arrow
 * This class represents the Arrow of the main character's gun.
 * An arrow is shot once the main character holds a gun and uses it's shoot() method.
 * An arrow has the power to kill any fish.
 */

public class Arrow extends GameObject implements Collidable{

    private Rect bodySrc;
    private RectF bodyDst = new RectF();

    private boolean canDraw;
    public boolean populated;

    public static Arrow prepareArrow(Bitmap bitmap) {
        Arrow arrow = new Arrow();
        int arrowWidth = bitmap.getWidth();
        int arrowHeight = bitmap.getHeight();
        arrow.setBitmap(bitmap);
        arrow.setSize(arrowWidth, arrowHeight);
        arrow.bodySrc = new Rect(0, 0, arrowWidth, arrowHeight);

        return arrow;
    }

    @Override
    public void draw(Canvas canvas) {
        if(canDraw) {
            canvas.drawBitmap(bitmap, bodySrc, bodyDst, null);
            if(!gamePaused) bodyDst.offsetTo(bodyDst.left + 17, bodyDst.top);
        }
    }

    @Override
    public void update() {
        if(hit || bodyDst.left > screenWidth) {
            canDraw = populated = hit = false;
            bodyDst.set(-screenWidth, 0, -screenWidth+width, 0); // out of screen
        }
    }

    void populate(float x, float y) {
        bodyDst.set(x, y, x + width, y + height);
        canDraw = true;
        populated = true;
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
