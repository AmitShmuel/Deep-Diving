package com.example.aamit.finalproject;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.View;

import static com.example.aamit.finalproject.GameViewActivity.gamePaused;
import static com.example.aamit.finalproject.GameViewActivity.gameRunning;

/**
 *
 * View of the game.
 * It shows an undersea background with sea objects moving around
 */
public class GameView extends View {

    /*
     * View measurement values
     */
    static final float WATER_SPEED = 0.3f, SAND_SPEED = 1.5f;
    static float screenWidth, screenHeight, screenSand;

    /*
     * Draw objects
     */
    private Background waterBackground, sandBackground;
    private BackgroundObject[] objects;
    private Character[] characters;
    private MainCharacter mainChar;
    private Coin coin;

    /*
     * Score related types
     */
    private int score;
    private Paint paint = new Paint();
    private Rect scoreRect = new Rect();
    private boolean scoreChanged = true;
    private static StringBuilder sbScore = Util.acquireStringBuilder();

    /*
     * Background runnable updating each object on the view
     */
    Runnable updater = new Runnable() {
        @Override
        public void run() {
        while (gameRunning) {
            if (!gamePaused) {
                if (screenWidth != 0) {
                    waterBackground.update();
                    sandBackground.update();
                    for (Character ch : characters) ch.update();
                    for (BackgroundObject ob : objects) ob.update();
                    mainChar.update();
                    coin.update();
                    detectCollisions();
                }
            }
        }
        }
    };

    /*
     * Helps to count milliseconds before doing somethings
     */
    MillisecondsCounter mCounter = new MillisecondsCounter();


    public GameView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setWillNotDraw(false);

        initDrawObjects();

        initScorePaint();

        updateScore(0);

        runUpdater();
    }

    private void initDrawObjects() {
        waterBackground = new Background(BitmapFactory.decodeResource(getResources(), R.drawable.water), WATER_SPEED);
        sandBackground = new Background(BitmapFactory.decodeResource(getResources(), R.drawable.sand), SAND_SPEED);
        mainChar = MainCharacter.prepareMainChar(BitmapFactory.decodeResource(getResources(), R.drawable.diver));
        characters = Character.prepareCharacters(BitmapFactory.decodeResource(getResources(), R.drawable.fishes));
        objects = BackgroundObject.prepareBackgroundObjects(BitmapFactory.decodeResource(getResources(), R.drawable.objects));
        coin = Coin.prepareCoin(BitmapFactory.decodeResource(getResources(), R.drawable.coin));
    }

    private void initScorePaint() {
        paint.setTextSize(60);
        paint.setAntiAlias(true);
        paint.setTypeface(Typeface.DEFAULT_BOLD);
        paint.setColor(0xff_dd_c4_52);
    }

    private void updateScore(int newScore) {
        score = newScore;
        sbScore.setLength(0);
        sbScore.append(String.valueOf(score)).append(" x");
        scoreChanged = true;
    }

    void runUpdater() {
        AsyncHandler.post(updater);
    }


    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        screenWidth = getWidth();
        screenHeight = getHeight();
        screenSand = screenHeight/5;
        coin.setScorePosition(screenWidth);
        super.onSizeChanged(w, h, oldw, oldh);
    }


    @Override
    protected void onDraw(Canvas canvas) {

        waterBackground.draw(canvas);
        coin.draw(canvas);
        for (Character ch : characters) ch.draw(canvas);
        mainChar.draw(canvas);
        // we want bubbles to come behind the sand
        for (BackgroundObject ob : objects) if (ob.kind == BackgroundObject.BUBBLE) ob.draw(canvas);
        sandBackground.draw(canvas);
        for (BackgroundObject ob : objects) if (ob.kind == BackgroundObject.PLANT) ob.draw(canvas);
        drawScore(canvas);

        postInvalidateOnAnimation();
    }


    private void drawScore(Canvas canvas) {
        if(scoreChanged) {
            paint.getTextBounds(sbScore.toString(), 0, sbScore.length(), scoreRect);
            scoreChanged = false;
        }
        canvas.drawText(sbScore.toString(), screenWidth - scoreRect.width() - coin.width*1.5f, scoreRect.height() + coin.height/3, paint);
    }

    void detectCollisions() {
        for (Character character : characters) {
            if(mainChar.canGetHit()) {
                if (RectF.intersects(character.bodyDst, mainChar.bodyDst)) {
                    updateScore((--score < 0) ? 0 : score);
                    mainChar.setCanGetHit(false);
                }
            } else if(mCounter.timePassed(2000)) {
                mainChar.setCanGetHit(true);
                mainChar.makeVisible();
            } else mainChar.blink();
        }
        if(RectF.intersects(coin.bodyDst, mainChar.bodyDst)) {
            if(!coin.isCollected()) {
                updateScore(score+1);
            }
            coin.collected();
        }
    }
}