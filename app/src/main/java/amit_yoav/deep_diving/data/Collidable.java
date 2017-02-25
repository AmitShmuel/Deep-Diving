package amit_yoav.deep_diving.data;

import android.graphics.Bitmap;
import android.graphics.RectF;

/**
 * This interface helps in making the CollisionUtil utility more general.
 */

public interface Collidable {

    RectF getBody();
    Bitmap getBitmap();
}
