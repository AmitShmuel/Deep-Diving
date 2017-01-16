package com.example.aamit.finalproject;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.Window;
import android.view.WindowManager;

import java.util.Random;

public class GameViewActivity extends AppCompatActivity implements SensorEventListener {

    private GameView view;
    private SensorManager sensorManager;
    static float xAccel, yAccel;
    static boolean sensorChanged, gameRunning, gamePaused;
    static Random rand = new Random();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // making the view full-screened
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        if(getSupportActionBar() != null) getSupportActionBar().hide();

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);

        gameRunning = true;

        setContentView(R.layout.activity_game_view);
        view = (GameView) findViewById(R.id.activity_game_view);
    }

    @Override
    protected void onResume() {
        super.onResume();
        gameRunning = true;
        sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_GAME);
        view.runUpdater();
    }

    @Override
    protected void onPause() {
        gamePaused = true;
        gameRunning = false;
        sensorManager.unregisterListener(this);
        super.onPause();
    }

    @Override
    public void onBackPressed() {
//        super.onBackPressed();
        gamePaused = !gamePaused;
    }

    @Override
    protected void onDestroy() {
        AsyncHandler.removeCallbacks(view.updater); // do we need it ? maybe the task has not been handled and the activity dies..
        super.onDestroy();
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        if(sensorEvent.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            yAccel = 4 * sensorEvent.values[0];
            xAccel = 4 * sensorEvent.values[1];
            sensorChanged = true;
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if(event.getAction() == MotionEvent.ACTION_UP)
            gamePaused = !gamePaused;
        return true;
    }

    /*
     * No implementation
     */
    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {}
}