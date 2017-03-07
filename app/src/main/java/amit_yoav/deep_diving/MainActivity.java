package amit_yoav.deep_diving;

import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.SparseIntArray;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;

import com.crashlytics.android.Crashlytics;
import com.crashlytics.android.ndk.CrashlyticsNdk;

import amit_yoav.deep_diving.compat.Compat;
import amit_yoav.deep_diving.dialogs.InfoDialog;
import amit_yoav.deep_diving.dialogs.QuitDialog;
import amit_yoav.deep_diving.dialogs.SettingsDialog;
import amit_yoav.deep_diving.utilities.AsyncHandler;

import io.fabric.sdk.android.Fabric;
import java.io.IOException;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.games.Games;
import com.google.example.games.basegameutils.BaseGameUtils;


public class MainActivity extends AppCompatActivity implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        View.OnClickListener {

    public InfoDialog infoDialog;
    public static SettingsDialog settingsDialog;
    public QuitDialog quitDialog;

    public static MyMusicRunnable musicPlayer;
    public static MySFxRunnable soundEffectsUtil;

    private static float volume = 0;
    private static boolean isSoundOn, gameStarted, isFinished;

    private int[] divers = {
      R.drawable.background_black, R.drawable.background_magenta, R.drawable.background_pink
    };
    private int diverPointer = 0;
    View mainActivityLayout = null;
    ImageButton leftArrow = null;


    //GOOGLE PLAY SERVICES RELATED TYPES
    public GoogleApiClient mGoogleApiClient;
    private static final int REQUEST_ACHIEVEMENTS = 5001, REQUEST_LEADERBOARD = 5001;

    private static int RC_SIGN_IN = 9001;
    private boolean mResolvingConnectionFailure = false;
    private boolean mAutoStartSignInflow = true;
    private boolean mSignInClicked = false;

    boolean mExplicitSignOut = false;
    boolean mInSignInFlow = false;

    public void setDiver(View v) {
        soundEffectsUtil.play(R.raw.open_dialog);
        if(leftArrow == v) diverPointer = --diverPointer == -1 ? 2 : diverPointer;
        else diverPointer = (++diverPointer) % 3;
        mainActivityLayout.setBackgroundResource(divers[diverPointer]);
        settingsDialog.setMainCharacter(diverPointer);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics(), new CrashlyticsNdk());

        setContentView(R.layout.activity_main);

        // Create the Google Api Client with access to the Play Games services
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(Games.API).addScope(Games.SCOPE_GAMES)
//                 add other APIs and scopes here as needed
//                .addApi(Drive.API).addScope(Drive.SCOPE_APPFOLDER) // Drive API
                .build();

        findViewById(R.id.sign_in_button).setOnClickListener(this);
        findViewById(R.id.sign_out_button).setOnClickListener(this);

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
        mainActivityLayout = this.findViewById(R.id.activity_main);
        leftArrow = (ImageButton) (this.findViewById(R.id.leftArrow));
        diverPointer = settingsDialog.getMainCharacter();
        mainActivityLayout.setBackgroundResource(divers[diverPointer]);
    }

    public void startGame(View view) {
        soundEffectsUtil.play(R.raw.start_bubble);
        gameStarted = true;
        musicPlayer.switchMusic(R.raw.game);
        Intent intent = new Intent(this, GameViewActivity.class);
        startActivity(intent);

        // Following the documentation, right after starting the activity
        // we override the transition
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);

    }

    public void quitGame(View view) {
        soundEffectsUtil.play(R.raw.quit_dialog);
        quitDialog.dismiss();
        isFinished = true;
        finish();
    }

    public void openDialog(View view) {
        soundEffectsUtil.play(R.raw.open_dialog);
        if(findViewById(R.id.infoButton) == view) infoDialog.show();
        else settingsDialog.show();
        //Games.Leaderboards.submitScore(mGoogleApiClient, LEADERBOARD_ID, UPDATED_SCORE);
    }

    public void closeDialog(View view) {
        soundEffectsUtil.play(R.raw.quit_dialog);
        if(infoDialog.isShowing()) infoDialog.dismiss();
        else if(settingsDialog.isShowing()) settingsDialog.dismiss();
        else quitDialog.dismiss();
    }

    public static void playToggleSoundEffect(boolean on) {
        boolean tmp = isSoundOn;
        isSoundOn = true;
        if(on) soundEffectsUtil.play(R.raw.toggle_on);
        else soundEffectsUtil.play(R.raw.toggle_off);
        isSoundOn = tmp;
    }

    public static void setVolumeMusic(float newVolume) {
        musicPlayer.mPlayer.setVolume((volume = newVolume), newVolume);
    }

    public static void setIsSoundOn(boolean sound) {
        isSoundOn = sound;
    }


    @Override
    protected void onStart() {
        super.onStart();
        if (!mInSignInFlow && !mExplicitSignOut) {
//             auto sign in
            mGoogleApiClient.connect();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if(isFinished) android.os.Process.killProcess(android.os.Process.myPid());
        gameStarted = false;
        musicPlayer.switchMusic(R.raw.welcome_screen);
        if(musicPlayer.mPlayer != null)
            musicPlayer.startMusic(true);
        settingsDialog = new SettingsDialog(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mGoogleApiClient.isConnected()) mGoogleApiClient.disconnect();
        if(!gameStarted) musicPlayer.stopMusic(true);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }

    @Override
    public void onBackPressed() {
        soundEffectsUtil.play(R.raw.open_dialog);
        quitDialog.show();
    }

    public void openAchievements(View view) {
        if(isSignedIn()) {
            startActivityForResult(Games.Achievements.getAchievementsIntent(mGoogleApiClient),
                    REQUEST_ACHIEVEMENTS);
        }
        else {
            BaseGameUtils.makeSimpleDialog(this, getString(R.string.achievements_not_available)).show();
        }
    }

    public void openLeaderboard(View view) {
        if(isSignedIn()) {
            startActivityForResult(Games.Leaderboards.getLeaderboardIntent(mGoogleApiClient,
                    getString(R.string.leaderboard_top_divers)),
                    REQUEST_LEADERBOARD);
        }
        else {
            BaseGameUtils.makeSimpleDialog(this, getString(R.string.leaderboard_not_available)).show();
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        // The player is signed in. Hide the sign-in button and allow the
        // player to proceed.
        // show sign-out button, hide the sign-in button
        findViewById(R.id.sign_in_button).setVisibility(View.GONE);
        findViewById(R.id.sign_out_button).setVisibility(View.VISIBLE);

        // (your code here: update UI, enable functionality that depends on sign in, etc)
    }

    @Override
    public void onConnectionSuspended(int i) {
        // Attempt to reconnect
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        if (mResolvingConnectionFailure) {
            // already resolving
            return;
        }
        // if the sign-in button was clicked or if auto sign-in is enabled,
        // launch the sign-in flow
        if (mSignInClicked || mAutoStartSignInflow) {
            mAutoStartSignInflow = false;
            mSignInClicked = false;
            mResolvingConnectionFailure = true;

            // Attempt to resolve the connection failure using BaseGameUtils.
            // The R.string.signin_other_error value should reference a generic
            // error string in your strings.xml file, such as "There was
            // an issue with sign-in, please try again later."
            if (!BaseGameUtils.resolveConnectionFailure(this,
                    mGoogleApiClient, connectionResult,
                    RC_SIGN_IN, "There was an issue with sign-in, please try again later.")) {
                mResolvingConnectionFailure = false;
            }
        }

        // Put code here to display the sign-in button
    }

    @Override
    protected void onActivityResult(final int requestCode,final int resultCode,final Intent intent) {
        if (requestCode == RC_SIGN_IN) {
            mSignInClicked = mResolvingConnectionFailure = false;
            if (resultCode == RESULT_OK) {
                mGoogleApiClient.connect();
            } else {
                // Bring up an error dialog to alert the user that sign-in
                // failed. The R.string.signin_failure should reference an error
                // string in your strings.xml file that tells the user they
                // could not be signed in, such as "Unable to sign in."
                BaseGameUtils.showActivityResultError(this, requestCode, resultCode,
                        R.string.signin_failure);
            }
        }
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.sign_in_button) {
            // start the asynchronous sign in flow
            mSignInClicked = true;
            mGoogleApiClient.connect();
        }
        else if (view.getId() == R.id.sign_out_button) {
            // sign out.
            mSignInClicked = false;
            Games.signOut(mGoogleApiClient);

            // show sign-in button, hide the sign-out button
            findViewById(R.id.sign_in_button).setVisibility(View.VISIBLE);
            findViewById(R.id.sign_out_button).setVisibility(View.GONE);

            // user explicitly signed out, so turn off auto sign in
            mExplicitSignOut = true;
            if (isSignedIn()) {
                Games.signOut(mGoogleApiClient);
                mGoogleApiClient.disconnect();
            }
        }
    }

    public boolean isSignedIn() {
        return (mGoogleApiClient != null && mGoogleApiClient.isConnected());
    }

    public static class MySFxRunnable implements Runnable {
        Context appContext;
        SoundPool soundPool;
        /**
         * like a hash map, but more efficient
         */
        SparseIntArray soundsMap = new SparseIntArray();
        private boolean prepared = false;

        MySFxRunnable(Context c) {
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

            /*
             * a callback when prepared -- we need to prevent playing before prepared
             */
            soundPool.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {
                @Override
                public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {
                    prepared = true;
                }
            });

            /*
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
            soundsMap.put(R.raw.gun_collect, soundPool.load(appContext, R.raw.gun_collect, 1));
            soundsMap.put(R.raw.shield, soundPool.load(appContext, R.raw.shield, 1));
            soundsMap.put(R.raw.shoot, soundPool.load(appContext, R.raw.shoot, 1));
            soundsMap.put(R.raw.killed, soundPool.load(appContext, R.raw.killed, 1));
            soundsMap.put(R.raw.drop_inks, soundPool.load(appContext, R.raw.drop_inks, 1));
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
//        private boolean isMainMenu = true;
        private int currentMusicPlaying = R.raw.welcome_screen;

        MyMusicRunnable(Context c) {
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
         * @param mp - this is the MediaPlayer
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

        void switchMusic(int id) {
            if(musicIsPlaying) {
                if(currentMusicPlaying == id) return;
                currentMusicPlaying = id;
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