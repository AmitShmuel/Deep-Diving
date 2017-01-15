package com.example.aamit.finalproject;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
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
     * Booleans & View measurement values
     */
    static final float WATER_SPEED = 0.3f, SAND_SPEED = 1.5f;
    static float screenWidth, screenHeight, screenSand = 121;
    private Rect scoreRect = new Rect();

    /**
     * Draw objects
     */
    private Background waterBackground, sandBackground;
    private BackgroundObject[] objects;
    private Character[] characters;
    private MainCharacter mainChar;
    private Coin coin;

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
    Paint paint = new Paint();
    private boolean scoreChanged = true;
    private int score;
    private String scoreString = String.valueOf(0);
    MillisecondsCounter mCounter = new MillisecondsCounter();


    public GameView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setWillNotDraw(false);

        waterBackground = new Background(BitmapFactory.decodeResource(getResources(), R.drawable.water), WATER_SPEED);
        sandBackground = new Background(BitmapFactory.decodeResource(getResources(), R.drawable.sand), SAND_SPEED);

        mainChar = MainCharacter.prepareMainChar(BitmapFactory.decodeResource(getResources(), R.drawable.diver));

        characters = Character.prepareCharacters(BitmapFactory.decodeResource(getResources(), R.drawable.fishes));

        objects = BackgroundObject.prepareBackgroundObjects(BitmapFactory.decodeResource(getResources(), R.drawable.objects));

        coin = Coin.prepareCoin(BitmapFactory.decodeResource(getResources(), R.drawable.coin));

        initScorePaint();

        runUpdater();
    }

    void runUpdater() {
        AsyncHandler.post(updater);
    }

    void detectCollisions() {
        for (Character character : characters) {
            if(mainChar.canGetHit()) {
                if (RectF.intersects(character.bodyDst, mainChar.bodyDst)) {
                    score = (score - 200 < 0) ? 0 : score - 200;
                    scoreString = String.valueOf(score);
                    scoreChanged = true;
                    mainChar.setCanGetHit(false);
                }
            } else if(mCounter.timePassed(2000)) mainChar.setCanGetHit(true);
        }
        if(RectF.intersects(coin.bodyDst, mainChar.bodyDst)) {
            if(!coin.isCollected()) {
                score += 100;
                scoreString = String.valueOf(score);
                scoreChanged = true;
            }
            coin.collected();
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        screenWidth = getWidth();
        screenHeight = getHeight();
        coin.setScorePosition(screenWidth, screenHeight);
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

    private void initScorePaint() {
        paint.setTextSize(50);
        paint.setColor(Color.RED);
        paint.setTextAlign(Paint.Align.LEFT);
    }

    private void drawScore(Canvas canvas) {
        if(scoreChanged) {
            paint.getTextBounds(scoreString, 0, scoreString.length(), scoreRect);
            scoreChanged = false;
        }
        canvas.drawText(scoreString, screenWidth - scoreRect.width() - 10, scoreRect.height() + 10, paint);
    }
}