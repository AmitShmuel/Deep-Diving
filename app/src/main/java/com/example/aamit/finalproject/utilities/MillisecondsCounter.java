package com.example.aamit.finalproject.utilities;


import com.example.aamit.finalproject.GameView;
import com.example.aamit.finalproject.GameViewActivity;

import static com.example.aamit.finalproject.GameViewActivity.gamePaused;

/**
 *
 * Helper class used to count milliseconds.
 */
public class MillisecondsCounter {

    private boolean flag = true;
    private long startTime, timeSaver;

    /*
     * This method receives amount of milliseconds to wait.
     * when millisecondsToWait have passed, it will return true;
     * e.g:
     * if(instantiated_object.timePassed(1000)) do_something();
     * else do_something_else()
     */
    public boolean timePassed(long millisecondsToWait) {
        if (flag) {
            startTime = System.currentTimeMillis();
            flag = false;
        }
        return millisecondsToWait < System.currentTimeMillis() - startTime && (flag = true);
    }

    public void restartCount() {flag = true;}

    public void stopTime(boolean isPaused) {
        if(isPaused) timeSaver = System.currentTimeMillis();
        // adding the amount of time the game was paused to startTime so we remain synced!
        else startTime += System.currentTimeMillis() - timeSaver;
    }
}