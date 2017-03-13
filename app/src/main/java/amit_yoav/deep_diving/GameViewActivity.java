package amit_yoav.deep_diving;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.games.Games;

import amit_yoav.deep_diving.dialogs.GameOverDialog;
import amit_yoav.deep_diving.dialogs.PauseDialog;
import amit_yoav.deep_diving.dialogs.PauseSettingsDialog;
import amit_yoav.deep_diving.utilities.AsyncHandler;

import java.util.Random;

import static amit_yoav.deep_diving.GameView.pauseHeight;
import static amit_yoav.deep_diving.GameView.pauseWidth;

public class GameViewActivity extends AppCompatActivity implements SensorEventListener {

    /*
     * Views
     */
    public GameView view;
    private PauseDialog pauseDialog;
    private PauseSettingsDialog pauseSettingsDialog;
    private GameOverDialog gameOverDialog;

    /*
     * Device manipulating
     */
    public Vibrator vibrator;
    private SensorManager sensorManager;
    private boolean firstSensorChanged = true, firstTime = true;
    private float ySensorOffset;
    private int sign = 1, index;
    public static float xAccel, yAccel;

    /*
     * Static Booleans controllers
     */
    public static boolean sensorChanged, gameRunning, gamePaused, canShoot, shoot;

    /*
     * Other
     */
    public GoogleApiClient mGoogleApiClient;
    public static Random rand = new Random();
    private long startTime;

    /*
     * Shared Preferences types
     */
    private int bestScore, mainCharacterBitmap;
    public int getBestScore() {return bestScore;}
    public int getMainCharResource() {return mainCharacterBitmap;}



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
        pauseSettingsDialog = new PauseSettingsDialog(this);

        gameOverDialog = new GameOverDialog(this);
        bestScore = ((MainActivity) getParent()).settingsDialog.getBestScore();

        //getting the color of the diver
        int charIndex = ((MainActivity) getParent()).settingsDialog.getMainCharacter();
        if(charIndex == 0) mainCharacterBitmap = R.drawable.black_diver;
        else if(charIndex == 1) mainCharacterBitmap = R.drawable.magenta_diver;
        else mainCharacterBitmap = R.drawable.pink_diver;

        vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Games.API).addScope(Games.SCOPE_GAMES)
                .build();
        mGoogleApiClient.connect();

        gameRunning = firstSensorChanged = true;
        canShoot = false;

        setContentView(R.layout.activity_game_view);
        view = (GameView) findViewById(R.id.activity_game_view);

        gameOverDialog.setBestScore(bestScore);
    }

    public void showSettings() {
        MainActivity.soundEffectsUtil.play(R.raw.open_dialog);
        pauseSettingsDialog.show();
    }

    public void closeDialog(View view) {
        MainActivity.soundEffectsUtil.play(R.raw.quit_dialog);
        pauseSettingsDialog.dismiss();
    }

    public void gameOver(int score) {
//        canShoot = false;
        togglePauseGame();
        MainActivity.soundEffectsUtil.play(R.raw.disqualification);
        MainActivity.musicPlayer.stopMusic(false);
        gameOverDialog.setScores(score);
        if(score > bestScore) {
            gameOverDialog.setBestScore(bestScore = score);
            ((MainActivity) getParent()).settingsDialog.setBestScore(bestScore);
            MainActivity.setBestScore(bestScore);
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

                // the values you were calculating originally here were over 10000!
            xAccel = (int) Math.pow(sensorEvent.values[1], 2);
            yAccel = (int) Math.pow(sensorEvent.values[2], 2);

//            yAccel = sign * ((4 * sensorEvent.values[index]) - (sign*ySensorOffset));
//            xAccel = 4 * sensorEvent.values[1];
//
//            if(firstSensorChanged) {
//                firstSensorChanged = false;
//                ySensorOffset = 4 * sensorEvent.values[0];
//                System.out.println(ySensorOffset +"========================");
//                 user is either lying on his back or sitting and the device is right in front of him
//                if(ySensorOffset > 25) {
//                    sign = -1;
//                    index = 2;
                    //CONT FROM HERE
//                }
            }
//            if(4 * sensorEvent.values[0] > )
//            else{
//                yAccel = -(4 * sensorEvent.values[2] + ySensorOffset);
//                System.out.println(ySensorOffset + "===============");
//                xAccel = 4 * sensorEvent.values[1];
//                if (firstSensorChanged) {
//                    firstSensorChanged = false;
//                    ySensorOffset = yAccel;
//                }
//            }
//            sensorChanged = true;
//        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if(event.getAction() == MotionEvent.ACTION_DOWN) {
            if((firstTime || System.currentTimeMillis() - startTime > 1000) &&
                    event.getX() <= pauseWidth*1.5 && event.getY() <= pauseHeight*1.5)  {

                togglePauseGame();
                MainActivity.soundEffectsUtil.play(R.raw.open_dialog);
                MainActivity.musicPlayer.stopMusic(true);
                pauseDialog.show();
                firstTime = false;
            }
            else if(canShoot) {
                MainActivity.soundEffectsUtil.play(R.raw.shoot);
                shoot = true;
                canShoot = false;
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