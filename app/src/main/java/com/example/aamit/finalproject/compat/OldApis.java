package com.example.aamit.finalproject.compat;

import android.media.AudioManager;
import android.media.SoundPool;

/**
 * Created by amir on 12/17/16.
 */
@SuppressWarnings("deprecation")
class OldApis {
    public static SoundPool createSoundPool() {
        return new SoundPool(1, AudioManager.STREAM_MUSIC, 0);
    }
}
