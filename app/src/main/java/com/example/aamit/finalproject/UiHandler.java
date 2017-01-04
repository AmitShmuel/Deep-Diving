/*
 * Copyright (C) 2016 MindTheApps
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.aamit.finalproject;

import android.os.Handler;
import android.os.Looper;

/**
 * Helper class for running easily on the UI thread.
 *
 * This class has a collection of helper methods useful in any app, you can take it as-is or change whatever you want.
 * I use a similar Util class in my production apps, and publish it here for an Android Course I give at Shenkar Engineering College
 *
 * @author amir uval
 */
public final class UiHandler {

    private static final Handler sHandler;

    static {
        /**
         * notice that there is no context kept here, so no risk of memory leak.
         * This handler cal live as long as the app is alive
         */
        sHandler = new Handler(Looper.getMainLooper());
    }

    private UiHandler() {
    }

    public static Handler getHandler() {
        return sHandler;
    }

    /**
     * run anything safely on the thread looper
     *
     * @param r
     */
    public static void post(Runnable r) {
        if (Util.getThreadType() == Util.TH_UI) {
            r.run();
        } else {
            sHandler.post(r);
        }
    }

    /**
     * Caution: posting delayed runnable that is not static will very likely cause a memory leak!
     *
     * @param r
     * @param delayMillis
     */
    public static void postDelayed(Runnable r, long delayMillis) {
        if (delayMillis < 604_800_000L /* == WEEK_IN_MILLIS*/) { // no sense scheduling ui update a week in advances
            sHandler.postDelayed(r, delayMillis);
        }
    }

    /**
     * Caution: posting delayed runnable that is not static will very likely cause a memory leak!
     * beware 2:
     * it will not fire while in deep sleep. (as handler depends on uptimeMillis = not advancing while off).
     * deep sleep will postpone post.
     *
     * @param r
     * @param delayMillis
     * @param removeOlder
     */
    public static void postDelayed(Runnable r, long delayMillis, boolean removeOlder) {

        if (removeOlder) {
            sHandler.removeCallbacks(r);
        }
        sHandler.postDelayed(r, delayMillis);
    }

    public static void removeAllCallbacks() {
        sHandler.removeCallbacksAndMessages(null);
    }

    public static void removeCallbacks(Runnable r) {
        sHandler.removeCallbacks(r);
    }
}
