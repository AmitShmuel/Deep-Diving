package amit_yoav.deep_diving.dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.Switch;

import amit_yoav.deep_diving.MainActivity;
import amit_yoav.deep_diving.R;
import amit_yoav.deep_diving.utilities.AsyncHandler;

public class SettingsDialog extends Dialog {

    private MainActivity mainActivity;
    private Switch switchSound;
    private SeekBar seekbarMusic;
    private ImageButton howToPlay;
//    private static boolean isStarted = true;

    private SharedPreferences preferences;
    private final SharedPreferences.Editor editor;

    private HowToPlayDialog howToPlayDialog;
    public boolean showOnStart;

    public SettingsDialog(Activity a) {
        super(a);
        this.mainActivity = (MainActivity) a;

        preferences = PreferenceManager.getDefaultSharedPreferences(mainActivity);
        editor = preferences.edit();


        howToPlayDialog = new HowToPlayDialog(mainActivity);
        howToPlayDialog.setOnDismissListener(new OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                if(showOnStart && !getHowToPlayUsed()) {
                    showOnStart = false;
                    setHowToPlayUsed(true);
                    mainActivity.startGame(null);
                }
            }
        });

        //Use these lines to see How to play again
//        editor.putBoolean("how_to_play", false);
//        editor.commit();

        //Use these lines to reset the Highest score (local)
//        editor.putInt("best_score", 1);
//        editor.commit();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_dialog);
        this.setCanceledOnTouchOutside(false);

        switchSound = (Switch) this.findViewById(R.id.switchSound);
        seekbarMusic = (SeekBar) this.findViewById(R.id.seekbarMusic);
        howToPlay = (ImageButton) this.findViewById(R.id.how_to_play_button);

        switchSound.setChecked(preferences.getBoolean("sound",true));
        seekbarMusic.setProgress(preferences.getInt("music", 99));

        switchSound.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView,final boolean isChecked) {
                MainActivity.playToggleSoundEffect(isChecked);
                MainActivity.setIsSoundOn(isChecked);

                AsyncHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        editor.putBoolean("sound", isChecked);
                        editor.commit();
                    }
                });
            }
        });

        seekbarMusic.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onStopTrackingTouch(final SeekBar seekBar) {
                AsyncHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        editor.putInt("music", seekBar.getProgress());
                        editor.commit();
                    }
                });
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress,boolean fromUser) {

                MainActivity.setVolumeMusic((float)progress/100);
            }
        });

        howToPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showHowToPlayDialog();
            }
        });
    }

    public float getVolume() {
        return (float)(preferences.getInt("music",99))/100;
    }
    public boolean getSound() {return preferences.getBoolean("sound",true);}

    public int getBestScore() {return preferences.getInt("best_score",3);}
    public void setBestScore(final int bestScore) {
        AsyncHandler.post(new Runnable() {
            @Override
            public void run() {
                editor.putInt("best_score", bestScore);
                editor.commit();
            }
        });
    }

    public int getMainCharacter() {return preferences.getInt("main_character", 0);}
    public void setMainCharacter(final int mainCharacter) {
        AsyncHandler.post(new Runnable() {
            @Override
            public void run() {
                editor.putInt("main_character", mainCharacter);
                editor.commit();
            }
        });
    }

    public boolean getHowToPlayUsed() {return preferences.getBoolean("how_to_play", false);}
    public void setHowToPlayUsed(final boolean howToPlayUsed) {
        AsyncHandler.post(new Runnable() {
            @Override
            public void run() {
                editor.putBoolean("how_to_play", howToPlayUsed);
                editor.commit();
            }
        });
    }

    @Override
    public void onBackPressed() {
        mainActivity.closeDialog(null);
    }

    public void showHowToPlayDialog() {
        MainActivity.soundEffectsUtil.play(R.raw.open_dialog);
        howToPlayDialog.show();
    }
}