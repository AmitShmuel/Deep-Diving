package amit_yoav.deep_diving.dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.widget.CompoundButton;
import android.widget.SeekBar;
import android.widget.Switch;

import amit_yoav.deep_diving.GameViewActivity;
import amit_yoav.deep_diving.MainActivity;
import amit_yoav.deep_diving.R;
import amit_yoav.deep_diving.utilities.AsyncHandler;

public class PauseSettingsDialog extends Dialog {

    private GameViewActivity gameViewActivity;
    private Switch switchSound;
    private SeekBar seekbarMusic;

    private SharedPreferences preferences;
    private final SharedPreferences.Editor editor;

    public PauseSettingsDialog(Activity a) {
        super(a);
        gameViewActivity = (GameViewActivity) a;
//        mainActivity =  ma;
        preferences = PreferenceManager.getDefaultSharedPreferences(gameViewActivity);
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
//                mainActivity.playToggleSoundEffect(isChecked);
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
//                ((MainActivity) gameViewActivity.getParent()).setVolumeMusic((float)progress/100);
            }
        });
    }

//    public float getVolume() {
//        return (float)(preferences.getInt("music",99))/100;
//    }
//    public boolean getSound() {return preferences.getBoolean("sound",true);}

    @Override
    public void onBackPressed() {
        gameViewActivity.closeDialog(null);
    }
}