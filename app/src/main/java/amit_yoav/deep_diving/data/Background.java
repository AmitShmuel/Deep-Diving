package amit_yoav.deep_diving.data;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.LightingColorFilter;
import android.graphics.Paint;

import static amit_yoav.deep_diving.GameView.SAND_SPEED;
import static amit_yoav.deep_diving.GameView.screenHeight;
import static amit_yoav.deep_diving.GameView.screenWidth;
import static amit_yoav.deep_diving.GameViewActivity.gamePaused;

/*
 * Background
 * The sand background of the game, consists of background_water, and sand with sea objects on the bottom
 */
public class Background extends GameObject{

    private float x, speed; // y doesn't change
    private Paint p;
    public  boolean isDark;

    public Background(Bitmap bitmap, float speed) {
        this.speed = speed;
        setBitmap(bitmap);
        setSize(bitmap.getWidth(), bitmap.getHeight());

        p = new Paint(Color.RED);
        ColorFilter filter = new LightingColorFilter(0xFF7F7F7F, 0x00000000);    // darken
        p.setColorFilter(filter);
    }

    @Override
    public void draw(Canvas canvas) {
        int save = canvas.save();
        Paint paint = isDark ? p : null;

        // sand width is bigger
        if(speed == SAND_SPEED) canvas.scale(screenWidth/width*2, screenHeight/height);
        else canvas.scale(screenWidth/width, screenHeight/height);

        canvas.drawBitmap(bitmap, x, 0, paint);

        if(x < 0) canvas.drawBitmap(bitmap, x+width, 0, paint);

        if(!gamePaused) x -= speed;

        canvas.restoreToCount(save);
    }

    @Override
    public void update() {
        if(x < -width) x = 0;
    }
}