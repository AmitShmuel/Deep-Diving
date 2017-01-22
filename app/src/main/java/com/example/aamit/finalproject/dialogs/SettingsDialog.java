package com.example.aamit.finalproject.dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.widget.CompoundButton;
import android.widget.SeekBar;
import android.widget.Switch;

import com.example.aamit.finalproject.MainActivity;
import com.example.aamit.finalproject.R;
import com.example.aamit.finalproject.utilities.AsyncHandler;

public class SettingsDialog extends Dialog {

    private MainActivity mainActivity;
    private Switch switchSound;
    private SeekBar seekbarMusic;

    private SharedPreferences preferences;
    private final SharedPreferences.Editor editor;

    public SettingsDialog(Activity a) {
        super(a);
        this.mainActivity = (MainActivity) a;

        preferences = PreferenceManager.getDefaultSharedPreferences(mainActivity);
        editor = preferences.edit();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_dialog);
        this.setCanceledOnTouchOutside(false);

        switchSound = (Switch) this.findViewById(R.id.switchSound);
        seekbarMusic = (SeekBar) this.findViewById(R.id.seekbarMusic);

        switchSound.setChecked(preferences.getBoolean("sound",true));
        seekbarMusic.setProgress(preferences.getInt("music", 99));

        switchSound.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView,final boolean isChecked) {
                mainActivity.playToggleSoundEffect(isChecked);
                mainActivity.setIsSoundOn(isChecked);

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

                mainActivity.setVolumeMusic((float)progress/100);
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

    @Override
    public void onBackPressed() {
        mainActivity.closeDialog(null);
    }
}