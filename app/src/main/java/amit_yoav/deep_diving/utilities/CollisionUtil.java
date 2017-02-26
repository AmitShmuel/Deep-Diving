package amit_yoav.deep_diving.utilities;

import android.graphics.Color;
import android.graphics.RectF;

import amit_yoav.deep_diving.data.Collidable;

/**
 * Helper class to detect collisions.
 * First level checks intersection of bitmap rectangles
 * Second and more precise level checks for pixel level collisions
 */
public class CollisionUtil {

    public static boolean isCollisionDetected(Collidable sprite1, Collidable sprite2){

        RectF bounds1 = sprite1.getBody();
        RectF bounds2 = sprite2.getBody();

        // First level detection
        if( RectF.intersects(bounds1, bounds2) ){

            // Second level
            // get the intersection rectangle
            RectF collisionBounds = getCollisionBounds(bounds1, bounds2);
            // looping through this intersection rectangle
            for (int i = (int)collisionBounds.left; i < collisionBounds.right; i++) {
                for (int j = (int)collisionBounds.top; j < collisionBounds.bottom; j++) {
                    // get a pixel from the bitmap
                    int sprite1Pixel = getBitmapPixel(sprite1, i, j);
                    int sprite2Pixel = getBitmapPixel(sprite2, i, j);
                    // check if the pixel is not transparent
                    if( sprite1Pixel != -1 && sprite2Pixel != -1 && isFilled(sprite1Pixel) && isFilled(sprite2Pixel)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private static int getBitmapPixel(Collidable sprite, int i, int j) {

        int x = ((int)(i - sprite.getBody().left));
        int y = ((int)(j - sprite.getBody().top));

        // protecting the edges in case we got an out-of-bitmap-bound pixel
        if(     (x > 1) && (x < sprite.getBitmap().getWidth()) &&
                (y > 1) && (y < sprite.getBitmap().getHeight())) {

            return sprite.getBitmap().getPixel(x, y);
        }
        return -1;
    }

    private static RectF getCollisionBounds(RectF rect1, RectF rect2) {
        int left = (int) Math.max(rect1.left, rect2.left);
        int top = (int) Math.max(rect1.top, rect2.top);
        int right = (int) Math.min(rect1.right, rect2.right);
        int bottom = (int) Math.min(rect1.bottom, rect2.bottom);
        return new RectF(left, top, right, bottom);
    }

    private static boolean isFilled(int pixel) {return pixel != Color.TRANSPARENT;}
}