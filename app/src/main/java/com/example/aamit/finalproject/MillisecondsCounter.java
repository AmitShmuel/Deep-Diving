package com.example.aamit.finalproject;

/**
 *
 * Helper class used to count milliseconds.
 */
class MillisecondsCounter {

    private boolean flag = true;
    private long startTime;

    /*
     * This method receives amount of milliseconds to wait.
     * when millisecondsToWait have passed, it will return true;
     * e.g:
     * if(instantiated_object.timePassed(1000)) do_something();
     * else do_something_else()
     */
    boolean timePassed(long millisecondsToWait) {
        if (flag) {
            startTime = System.currentTimeMillis();
            flag = false;
        }
        return millisecondsToWait < System.currentTimeMillis() - startTime && (flag = true);
    }

    void restartCount() {flag = true;}
}
