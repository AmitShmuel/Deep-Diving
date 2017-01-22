package com.example.aamit.finalproject.compat;

import android.annotation.TargetApi;
import android.media.AudioAttributes;
import android.media.SoundPool;
import android.os.Build;

/**
 * Created by amir on 12/17/16.
 */
@TargetApi(Build.VERSION_CODES.LOLLIPOP)
class Api21Plus {

    public static SoundPool createSoundPool() {
        // Initialize AudioAttributes.
        AudioAttributes attributes = new AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_GAME)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build();

        SoundPool soundPool = new SoundPool.Builder()
                .setAudioAttributes(attributes)
                .setMaxStreams(7)
                .build();

        return soundPool;
    }
}
