package amit_yoav.deep_diving.dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.widget.SwitchCompat;
import android.view.View;
import android.view.Window;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.Switch;

import amit_yoav.deep_diving.GameViewActivity;
import amit_yoav.deep_diving.MainActivity;
import amit_yoav.deep_diving.R;
import amit_yoav.deep_diving.utilities.AsyncHandler;

public class PauseSettingsDialog extends Dialog {

    private GameViewActivity gameViewActivity;
    private SwitchCompat switchSound;
    private SeekBar seekbarMusic;
    private ImageButton howToPlay;

    private SharedPreferences preferences;
    private final SharedPreferences.Editor editor;
    private HowToPlayDialog howToPlayDialog;

    public PauseSettingsDialog(Activity a) {
        super(a);
        gameViewActivity = (GameViewActivity) a;
        preferences = PreferenceManager.getDefaultSharedPreferences(gameViewActivity);
        editor = preferences.edit();

        howToPlayDialog = new HowToPlayDialog(gameViewActivity);
        this.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        this.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_dialog);
        this.setCanceledOnTouchOutside(false);

        switchSound = (SwitchCompat) this.findViewById(R.id.switchSound);
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
                MainActivity.soundEffectsUtil.play(R.raw.open_dialog);
                howToPlayDialog.show();
            }
        });
    }

    @Override
    public void onBackPressed() {
        gameViewActivity.closeDialog(null);
    }
}