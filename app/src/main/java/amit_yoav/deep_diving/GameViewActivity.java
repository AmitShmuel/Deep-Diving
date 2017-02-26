package amit_yoav.deep_diving;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.Window;
import android.view.WindowManager;

import amit_yoav.deep_diving.dialogs.GameOverDialog;
import amit_yoav.deep_diving.dialogs.PauseDialog;
import amit_yoav.deep_diving.utilities.AsyncHandler;

import java.util.Random;

public class GameViewActivity extends AppCompatActivity implements SensorEventListener {

    public GameView view;
    private PauseDialog pauseDialog;
    private GameOverDialog gameOverDialog;

    private SensorManager sensorManager;
    private boolean firstSensorChanged = true, firstTime = true;
    private float ySensorOffset;
    public static float xAccel, yAccel;
    public static boolean sensorChanged, gameRunning, gamePaused;
    public static Random rand = new Random();
    private long startTime;

    private int bestScore;
    public int getBestScore() {return bestScore;}


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        startTime = System.currentTimeMillis();
        super.onCreate(savedInstanceState);

        // making the view full-screened
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        if(getSupportActionBar() != null) getSupportActionBar().hide();

        pauseDialog = new PauseDialog(this);
        gameOverDialog = new GameOverDialog(this);
        bestScore = ((MainActivity) getParent()).settingsDialog.getBestScore();

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);

        gameRunning = true;

        setContentView(R.layout.activity_game_view);
        view = (GameView) findViewById(R.id.activity_game_view);

        gameOverDialog.setBestScore(bestScore);
    }

    public void gameOver(int score) {
        togglePauseGame();
        MainActivity.soundEffectsUtil.play(R.raw.disqualification);
        MainActivity.musicPlayer.stopMusic(false);
        gameOverDialog.setScores(score);
        if(score > bestScore) {
            gameOverDialog.setBestScore(bestScore = score);
            ((MainActivity) getParent()).settingsDialog.setBestScore(bestScore);
        }
        gameOverDialog.show();
    }

    @Override
    protected void onPause() {
        super.onPause();
        gamePaused = true;
        gameRunning = false;
        sensorManager.unregisterListener(this);
        if(gameOverDialog.isShowing()) return;

        if(!pauseDialog.isShowing()) {
            MainActivity.musicPlayer.stopMusic(true);
            pauseDialog.show();
        }


    }

    @Override
    protected void onResume() {
        super.onResume();

        if(gameOverDialog.isShowing()) return;
        gamePaused = (pauseDialog.isShowing());
        gameRunning = true;
        sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_GAME);
        view.runUpdater();
    }



    @Override
    public void onBackPressed() {
        if(gameOverDialog.isShowing()) return;

        if(!pauseDialog.isShowing()) {
            gamePaused = true;
            MainActivity.musicPlayer.stopMusic(true);
            pauseDialog.show();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(pauseDialog != null) pauseDialog.dismiss();
        if(gameOverDialog != null) gameOverDialog.dismiss();
        AsyncHandler.removeCallbacks(view.updater); // do we need it ? maybe the task has not been handled and the activity dies..
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        if(sensorEvent.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            yAccel = 4 * sensorEvent.values[0] - ySensorOffset;
            xAccel = 4 * sensorEvent.values[1];
            if(firstSensorChanged) {
                ySensorOffset = yAccel;
                firstSensorChanged = false;
            }
            sensorChanged = true;
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if(event.getAction() == MotionEvent.ACTION_DOWN) {
            if(firstTime || System.currentTimeMillis() - startTime > 1000) {
                togglePauseGame();
                MainActivity.soundEffectsUtil.play(R.raw.open_dialog);
                MainActivity.musicPlayer.stopMusic(true);
                pauseDialog.show();
                firstTime = false;
            }
        }
        return true;
    }

    /*
     * No implementation
     */
    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {}

    public void togglePauseGame() { gamePaused = !gamePaused; }
}