package com.example.aamit.finalproject;

import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.SparseIntArray;
import android.view.View;
import android.view.WindowManager;

import com.crashlytics.android.Crashlytics;
import com.crashlytics.android.ndk.CrashlyticsNdk;
import com.example.aamit.finalproject.compat.Compat;
import com.example.aamit.finalproject.dialogs.InfoDialog;
import com.example.aamit.finalproject.dialogs.QuitDialog;
import com.example.aamit.finalproject.dialogs.SettingsDialog;
import com.example.aamit.finalproject.utilities.AsyncHandler;

import io.fabric.sdk.android.Fabric;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    public InfoDialog infoDialog;
    public static SettingsDialog settingsDialog;
    public QuitDialog quitDialog;

    public static MyMusicRunnable musicPlayer;
    public static MySFxRunnable soundEffectsUtil;

    private static float volume = 0;
    private static boolean isSoundOn, gameStarted, isBackPressed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics(), new CrashlyticsNdk());

        setContentView(R.layout.activity_main);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        if(getSupportActionBar() != null) getSupportActionBar().hide();

        infoDialog = new InfoDialog(this);
        settingsDialog = new SettingsDialog(this);
        quitDialog = new QuitDialog(this);

        volume = settingsDialog.getVolume();
        isSoundOn = settingsDialog.getSound();

        if (musicPlayer == null) {
            musicPlayer = new MyMusicRunnable(this);
        }
        AsyncHandler.post(musicPlayer);

        if (soundEffectsUtil == null) {
            soundEffectsUtil = new MySFxRunnable(this);
        }

//        musicPlayer.switchMusic(true);
    }

    public void startGame(View view) {
        soundEffectsUtil.play(R.raw.start_bubble);
        gameStarted = true;
        musicPlayer.switchMusic(false);
        Intent intent = new Intent(this, GameViewActivity.class);
        startActivity(intent);

        // Following the documentation, right after starting the activity
        // we override the transition
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);

    }

    public void quitGame(View view) {
        soundEffectsUtil.play(R.raw.quit_dialog);
        quitDialog.dismiss();
        finish();
    }

    public void openDialog(View view) {
        soundEffectsUtil.play(R.raw.open_dialog);
        if(findViewById(R.id.infoButton) == view) infoDialog.show();
        else settingsDialog.show();
    }

    public void closeDialog(View view) {
        soundEffectsUtil.play(R.raw.quit_dialog);
        if(infoDialog.isShowing()) infoDialog.dismiss();
        else if(settingsDialog.isShowing()) settingsDialog.dismiss();
        else quitDialog.dismiss();
    }

    public void playToggleSoundEffect(boolean on) {
        boolean tmp = isSoundOn;
        isSoundOn = true;
        if(on) soundEffectsUtil.play(R.raw.toggle_on);
        else soundEffectsUtil.play(R.raw.toggle_off);
        isSoundOn = tmp;
    }

    public void setVolumeMusic(float volume) {
        musicPlayer.mPlayer.setVolume((this.volume = volume), volume);
    }

    public void setIsSoundOn(boolean sound) {
        isSoundOn = sound;
    }

    @Override
    public void onResume() {
        super.onResume();
//        System.out.println("onResume");
        gameStarted = false;
        musicPlayer.switchMusic(true);
        if(musicPlayer.mPlayer != null)
            musicPlayer.startMusic(true);
    }

    @Override
    protected void onStop() {
        super.onStop();
//        System.out.println("onStop");
        if(!gameStarted) {
            musicPlayer.stopMusic(true);
        }
    }

    @Override
    protected void onRestart() {
//        System.out.println("onRestart");
        super.onRestart();
    }

    @Override
    public void onBackPressed() {
        soundEffectsUtil.play(R.raw.open_dialog);
        quitDialog.show();
    }

    public static class MySFxRunnable implements Runnable {
        Context appContext;
        SoundPool soundPool;
        /**
         * like a hash map, but more efficient
         */
        SparseIntArray soundsMap = new SparseIntArray();
        private boolean prepared = false;

        public MySFxRunnable(Context c) {
            // be careful not to leak the activity context.
            // can keep the app context instead.
            appContext = c.getApplicationContext();

            // init this object on a user thread.
            // The rest of the use of this class can be on the UI thread
            AsyncHandler.post(this);
        }

        /**
         * load and init the sound effects, so later I'll be able to play them instantly from the
         * UI thread.
         */
        @Override
        public void run() {

            soundPool = Compat.createSoundPool();

            /**
             * a callback when prepared -- we need to prevent playing before prepared
             */
            soundPool.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {
                @Override
                public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {
                    prepared = true;
                }
            });

            /**
             * the load() returns a stream id that can be used to play the sound.
             * I use the "R.raw.xyz" integer as key, because it's useless to invent new keys for
             * them
             */
            soundsMap.put(R.raw.open_dialog, soundPool.load(appContext, R.raw.open_dialog, 1));
            soundsMap.put(R.raw.quit_dialog, soundPool.load(appContext, R.raw.quit_dialog, 1));
            soundsMap.put(R.raw.start_bubble, soundPool.load(appContext, R.raw.start_bubble, 1));
            soundsMap.put(R.raw.toggle_on, soundPool.load(appContext, R.raw.toggle_on, 1));
            soundsMap.put(R.raw.toggle_off, soundPool.load(appContext, R.raw.toggle_off, 1));
            soundsMap.put(R.raw.coin_collected, soundPool.load(appContext, R.raw.coin_collected, 1));
            soundsMap.put(R.raw.hit, soundPool.load(appContext, R.raw.hit, 1));
            soundsMap.put(R.raw.disqualification, soundPool.load(appContext, R.raw.disqualification, 1));
            soundsMap.put(R.raw.level_complete, soundPool.load(appContext, R.raw.level_complete, 1));
            soundsMap.put(R.raw.new_record, soundPool.load(appContext, R.raw.new_record, 1));
            soundsMap.put(R.raw.extra_life, soundPool.load(appContext, R.raw.extra_life, 1));
        }

        public void play(int soundKey) {
            if (soundPool == null || !prepared || !isSoundOn) return;
            soundPool.play(soundsMap.get(soundKey), 1.0f, 1.0f, 1, 0, 1.0f);
        }
    }

    public static class MyMusicRunnable implements Runnable, MediaPlayer.OnCompletionListener {
        Context appContext;
        MediaPlayer mPlayer;
        private boolean musicIsPlaying = false;
        private boolean isMainMenu = true;

        public MyMusicRunnable(Context c) {
            // be careful not to leak the activity context.
            // can keep the app context instead.
            appContext = c.getApplicationContext();
        }

        public void stopMusic(boolean isPaused) {
            if(isPaused) mPlayer.pause();
            else mPlayer.stop();
        }

        public void startMusic(boolean isResumed) {
            if(!isResumed) {
                try {
                    mPlayer.prepare();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            mPlayer.start();
        }

        /**
         * MediaPlayer.OnCompletionListener callback
         *
         * @param mp
         */
        @Override
        public void onCompletion(MediaPlayer mp) {
            // loop back - play again
            if (musicIsPlaying && mPlayer != null) {
//                mPlayer.setVolume(volume, volume);
                mPlayer.start();
            }
        }

        /**
         * toggles the music player state
         * called asynchronously every time the play/pause button is pressed
         */
        @Override
        public void run() {

            if (musicIsPlaying) {
                mPlayer.stop();
                musicIsPlaying = false;
            } else {
                if (mPlayer == null) {
                    mPlayer = MediaPlayer.create(appContext, R.raw.welcome_screen);
                    mPlayer.setVolume(volume, volume);
                    mPlayer.start();
                    mPlayer.setOnCompletionListener(this); // MediaPlayer.OnCompletionListener
                } else {
                    try {
                        mPlayer.prepare();
                        mPlayer.setVolume(volume, volume);
                        mPlayer.start();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                musicIsPlaying = true;
            }
        }

        private void switchMusic(boolean isMenu) {
            if(musicIsPlaying) {
                if(isMainMenu == isMenu) return;
                isMainMenu = isMenu;
                int id = isMainMenu? R.raw.welcome_screen : R.raw.game;
                mPlayer.stop();
                mPlayer.release();
                mPlayer = MediaPlayer.create(appContext, id);
                mPlayer.setVolume(volume, volume);
                mPlayer.start();
                mPlayer.setOnCompletionListener(this);
            }
        }
    }
}