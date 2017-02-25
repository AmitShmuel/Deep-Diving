package amit_yoav.deep_diving.data;

import android.graphics.Bitmap;
import android.graphics.Canvas;

/**
 * This Interface represents a game object in the game.
 *
 * Each class that implements this interface must override two UI methods
 * draw and update that determine how the object appear and move in the view.
 */

public abstract class GameObject {

    public Bitmap bitmap;
    float width, height;

    public abstract void draw(Canvas canvas);

    public abstract void update();

    void setBitmap(Bitmap bitmap) {this.bitmap = bitmap;}

    void setSize(float width, float height) {
        this.width = width;
        this.height = height;
    }

    public float getWidth() { return width; }
    public float getHeight() { return height; }
}
