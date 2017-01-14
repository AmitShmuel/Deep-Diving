package com.example.aamit.finalproject;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
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
    static float screenWidth, screenHeight, screenSand = 120;

    /**
     * Draw objects
     */
    private Background waterBackground, sandBackground;
    private BackgroundObject[] objects;
    private Character[] characters;
    private MainCharacter mainChar;

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
                    }
                }
            }
        }
    };


    public GameView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setWillNotDraw(false);

        waterBackground = new Background(BitmapFactory.decodeResource(getResources(), R.drawable.water), WATER_SPEED);
        sandBackground = new Background(BitmapFactory.decodeResource(getResources(), R.drawable.sand), SAND_SPEED);

        mainChar = new MainCharacter();

        characters = Character.prepareCharacters(BitmapFactory.decodeResource(getResources(), R.drawable.fishes));

        objects = BackgroundObject.prepareBackgroundObjects(BitmapFactory.decodeResource(getResources(), R.drawable.objects));

        runUpdater();
    }

    void runUpdater() {
        AsyncHandler.post(updater);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        screenWidth = getWidth();
        screenHeight = getHeight();
        super.onSizeChanged(w, h, oldw, oldh);
    }


    @Override
    protected void onDraw(Canvas canvas) {

        waterBackground.draw(canvas);
        for (Character ch : characters) ch.draw(canvas);
        mainChar.draw(canvas);
        // we want bubbles to come behind the sand
        for (BackgroundObject ob : objects) if (ob.kind == BackgroundObject.BUBBLE) ob.draw(canvas);
        sandBackground.draw(canvas);
        for (BackgroundObject ob : objects) if (ob.kind == BackgroundObject.PLANT) ob.draw(canvas);

        postInvalidateOnAnimation();
    }
}