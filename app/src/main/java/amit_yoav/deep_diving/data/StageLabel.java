package amit_yoav.deep_diving.data;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;

import static amit_yoav.deep_diving.GameView.stagePassed;
import static amit_yoav.deep_diving.GameView.screenHeight;
import static amit_yoav.deep_diving.GameView.screenWidth;
import static amit_yoav.deep_diving.GameViewActivity.gamePaused;

/*
 * This class represents a label coming down or up the screen
 * as an indicator of level up or new best score
 */
public class StageLabel extends GameObject {

    private Rect bodySrc = new Rect();
    private RectF bodyDst = new RectF();
    private Paint paint = new Paint();
    private int alpha = 255;
    private boolean toPopulate, isNewRecordLabel;
    public boolean canDraw = true;


    public static StageLabel[] prepareStageLabels(Bitmap bitmap) {

        StageLabel[] stageLabel = new StageLabel[] {
                new StageLabel(), new StageLabel(), new StageLabel(), new StageLabel(), new StageLabel(),
                new StageLabel(), new StageLabel(), new StageLabel(), new StageLabel(), new StageLabel(),
                new StageLabel()
        };

        int stageLabelWidth = bitmap.getWidth();
        int stageLabelHeight = bitmap.getHeight()/11;

        for (int i = 0; i < 11; i++) { // bitmap row
            stageLabel[i].setBitmap(bitmap);
            stageLabel[i].setSize(stageLabelWidth, stageLabelHeight);
            stageLabel[i].bodySrc = new Rect(0, i * stageLabelHeight, stageLabelWidth, (i + 1) * stageLabelHeight);
        }
        stageLabel[stageLabel.length - 1].isNewRecordLabel = true;
        return stageLabel;
    }

    @Override
    public void draw(Canvas canvas) {

        int save = canvas.save();

        if(stagePassed) {
            canvas.drawBitmap(bitmap, bodySrc, bodyDst, paint);
            if (!gamePaused) {
                if (alpha > 0) paint.setAlpha(alpha--);
                if (isNewRecordLabel) {
                    if (bodyDst.top > screenHeight / 2) bodyDst.offsetTo(bodyDst.left, bodyDst.top - 2);
                    else canDraw = false;
                } else { // NewLevelLabel
                    if (bodyDst.bottom < screenHeight / 2) bodyDst.offsetTo(bodyDst.left, bodyDst.top + 2);
                    else stagePassed = false;
                }
            }
        }
        else {
            paint.setAlpha(100);
            canvas.scale(0.4f, 0.4f, screenWidth/2, screenHeight/2);
            canvas.translate(0, -screenHeight*0.9f);
            canvas.drawBitmap(bitmap, bodySrc, bodyDst, paint);
        }
        canvas.restoreToCount(save);
    }

    @Override
    public void update() {
        if(toPopulate){
            populate();
            toPopulate = false;
        }
    }

    private void populate() {
        if(isNewRecordLabel) {
            bodyDst.set(screenWidth/2 - width/2, screenHeight, screenWidth/2 + width/2, screenHeight + height);
        }
        else bodyDst.set(screenWidth/2 - width/2, -height, screenWidth/2 + width/2, 0);
    }

    public void setToPopulate(boolean toPopulate) { this.toPopulate = toPopulate; }
}