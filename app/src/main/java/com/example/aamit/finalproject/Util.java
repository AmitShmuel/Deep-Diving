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

import android.content.Context;
import android.os.Looper;
import android.support.annotation.IntDef;
import android.support.v4.util.Pools;
import android.util.Log;
import android.util.TypedValue;

import java.lang.annotation.Retention;
import java.util.Arrays;

import static java.lang.annotation.RetentionPolicy.SOURCE;

/**
 * This class has a collection of helper methods useful in any app, you can take it as-is or change whatever you want.
 * I use a similar Util class in my production apps, and publish it here for an Android Course I give at Shenkar Engineering College
 * <p>
 * Utilities include:
 * (1) getThreadType() - easily check at runtime if you're on the main thread or a user thread.
 * (2) assertTrue() - convenience method to let your app crash during development, if an
 * assumption you have fails. During production you can set it to just report back to you instead
 * of crashing the app. (use Fabric for the reporting part).
 * (3) acquireStringBuilder() / releaseStringBuilder() - smart pooling of a frequently used
 * class, avoiding many allocations and garbage collection.
 * You can use this pattern for other frequently used classes. (for example: RectF)
 * (4) dpToPx() - convenient for low level graphics, where the platform won't do this for you.
 * <p>
 * Everything in this class was originally developed by Amir Uval (MindTheApps).
 * Open sourced under Apache License, Version 2.0 (Attribution required)
 */
public class Util {


    /**
     * Statics invented here to symbol thread types as integers
     *
     * @THREAD_TYPE
     */
    public static final int TH_ANY = 0;
    public static final int TH_UI = 1;
    public static final int TH_WORKER = 2;
    private static Pools.SimplePool<StringBuilder> _sbPool = null;

    static {
        _sbPool = new Pools.SimplePool<>(20);
    }

    /**
     * StringBuffer Pool - use this instead of "new StringBuilder()" in your code
     * <p>
     * after using the StringBuilder, make sure to call:
     * Util.releaseStringBuilder(sb);
     *
     * @return an empty StringBuilder
     */
    public static StringBuilder acquireStringBuilder() {
        StringBuilder sb = _sbPool.acquire();
        if (sb == null) {
            sb = new StringBuilder();
        } else {
            sb.setLength(0);
        }
        return sb;
    }

    /**
     * A useful "assertion" method:
     * <p>
     * example use:
     * say you call a method and expect the return value ret to be 3, you can make sure of that this way:
     * <p>
     * Util.assertTrue(ret==3,"Wtf? I've just got %s instead of 3 from this method",ret);
     *
     * @param a
     * @param f
     * @param o
     * @throws AssertionError
     */
    public static void assertTrue(boolean a, String f, Object... o)
            throws AssertionError {
        if (!a) {
/*

I disabled here a trick to avoid assertions in layout view mode.
To enable it, set this value in this class statically to true, and in runtime set this value to false.

            if (IS_IN_EDIT_MODE) {
                return;
            }

*/


            // THE ASSERTION HAS FAILED!!
            // We want now to crash the app if we're in development mode (so we'll immediately see the stack trace and fix it)
            // But in production we don't want the app to crash, just report to us.

            String out = o != null ? String.format(f, o) : "[no-comment]";

            if (BuildConfig.DEBUG) { // << this magic flag will be false in production!

                if (out.contains("\n")) {
                    Log.e("assert", out);
                    out = "see above..";
                }

                throw generateAssertException(out);

            } else {
                // IN PRODUCTION!!
                // BUT marked as beta, so send the exception to Crashlytics or whatever you'll use

                // Crashlytics.logException(generateAssertException(out), null);
            }
        }
    }

    /**
     * Convert device independent pixel (dp) dimension to px, according to screen pixel density.
     * If you need this conversion a lot, it's best to calculate this once for 1 dp
     * and put the result in a static variable for a more efficient use later.
     *
     * @param context
     * @param dp
     * @return
     */
    public static float dpToPx(Context context, int dp) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, context.getResources().getDisplayMetrics());
    }


    /**
     * Some low level stuff to make the exception point to the correct method throwing the exception.
     * Or else all the assertions will point to the "assertTrue" method as the crash point.
     *
     * @param comment
     * @return
     */
    private static AssertionError generateAssertException(String comment) {
        AssertionError exception = new AssertionError(comment);
//  you can add some more info to the comment reported to you, for example .. = new AssertionError(comment + "/" + DEBUG_PROCESS_ID) :
        StackTraceElement[] stackTrace = exception.getStackTrace();
        StackTraceElement[] newArray = Arrays.copyOfRange(stackTrace, 2, stackTrace.length);
        exception.setStackTrace(newArray);
        return exception;
    }

    /**
     * This is
     *
     * @return
     */
    public static
    @THREAD_TYPE
    int getThreadType() {
        return Looper.myLooper().equals(Looper.getMainLooper()) ? TH_UI : TH_WORKER;
    }

    public static void releaseStringBuilder(StringBuilder sb) {
        try {
            _sbPool.release(sb);
        } catch (IllegalStateException e) {
            assertTrue(false, "Trying to release a StringBuilder already in the pool. Check your " +
                    "code! %s", e);
        }
    }


    /**
     * This is how to define statics in a way that take advantage of InteliJ auto complete (code time) and Lint checks (compile time)
     * and that has zero cost in runtime.
     * This way of declaring such "magic" variables with @interface is called "Annotations"
     * <p>
     * Read more about it here:
     * https://developer.android.com/studio/write/annotations.html
     * <p>
     * Session wrapper id
     */
    @Retention(SOURCE)
    @IntDef({
            TH_ANY,//0
            TH_UI,//1
            TH_WORKER,//2
    })
    public @interface THREAD_TYPE {
    }


}
