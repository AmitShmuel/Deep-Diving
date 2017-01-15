package com.example.aamit.finalproject;

/**
 * Created by aamit on 1/15/2017.
 */

public class MillisecondsCounter {

    private boolean flag = true;
    private long startTime;

    public boolean timePassed(long millisecondsToWait) {

        if(flag) {
            startTime = System.currentTimeMillis();
            flag = false;
        }
        if(millisecondsToWait < System.currentTimeMillis() - startTime) {
            return flag = true;
        }
        return false;
    }
}
