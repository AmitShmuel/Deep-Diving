package com.example.aamit.finalproject;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.support.annotation.IntDef;
import android.util.Log;

import java.lang.annotation.Retention;

import static java.lang.annotation.RetentionPolicy.CLASS;

/**
 * Helper class for managing the background thread used to perform io operations
 * and handle async broadcasts.
 *
 * This class has a collection of helper methods useful in any ap.
 *
 * Credit to: Amir Uval
 */

public final class AsyncHandler {

    private static final String TAG = AsyncHandler.class.getSimpleName();
    private static final Handler sHandler;
    private static final HandlerThread sHandlerThread = new HandlerThread(AsyncHandler.class.getSimpleName());

    public static final int EXAMPLE_MESSAGE_1 = 1;
    public static final int EXAMPLE_MESSAGE_2 = 2;

    static {
        sHandlerThread.start();
        sHandler = new Handler(sHandlerThread.getLooper()) {
            @Override
            public void handleMessage(Message msg) {
                @AsyncMessage int what = msg.what;
                switch (what) {
                    case EXAMPLE_MESSAGE_1:
                        Log.i(TAG, "Hey, got message no. 1!! (I could do something useful with it)");
                        break;
                    case EXAMPLE_MESSAGE_2:
                        Log.i(TAG, "Hey, got message no. 2!! (I could do something else)");
                        break;
                }

            }
        };
    }


    /**
     * We don't need to instantiate this class. Everything is static here.
     */
    private AsyncHandler() {
    }

    /**
     * run anything safely on the thread looper
     *
     * @param r
     */
    public static void post(Runnable r) {
        sHandler.post(r);
    }

    /**
     * beware:
     * it will not fire while in deep sleep. (as handler depends on
     * uptimeMillis = not advancing while off).
     * deep sleep will postpone post.
     *
     * @param r
     * @param timeStamp
     * @param removeOlder
     */
    public static void postAtTime(Runnable r, long timeStamp, boolean removeOlder) {
        if (removeOlder) {
            sHandler.removeCallbacks(r);
        }
        sHandler.postDelayed(r, timeStamp - System.currentTimeMillis());
    }

    /**
     * beware:
     * it will not fire while in deep sleep. (as handler depends on
     * uptimeMillis = not advancing while off).
     * deep sleep will postpone post.
     *
     * @param r
     * @param delayMillis
     * @param removeOlder
     */
    public static void postDelayed(Runnable r, long delayMillis,
                                   boolean removeOlder) {
        if (removeOlder) {
            sHandler.removeCallbacks(r);
        }
        sHandler.postDelayed(r, delayMillis);
    }

    /**
     * Quick way to run common things asynchronously
     *
     * @param what
     * @param obj
     */
    public static void postMessage(@AsyncMessage int what, Object obj, String comment) {
        Message message = sHandler.obtainMessage(what);
        message.obj = obj;
        message.getData().putString("c", comment);
        sHandler.sendMessage(message);

    }

    public static void removeAllCallbacks() {
        sHandler.removeCallbacksAndMessages(null);
    }

    public static void removeCallbacks(Runnable r) {
        sHandler.removeCallbacks(r);

    }

    @Retention(CLASS)
    @IntDef({
            EXAMPLE_MESSAGE_1,
            EXAMPLE_MESSAGE_2,
            // you can add more
    })
    public @interface AsyncMessage {
    }

}
