package com.example.aamit.finalproject.dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;

import com.example.aamit.finalproject.MainActivity;
import com.example.aamit.finalproject.R;

import static com.example.aamit.finalproject.GameViewActivity.gamePaused;

public class PauseDialog extends Dialog implements
        android.view.View.OnClickListener {

    private Activity gameViewActivity;
//    private ImageButton resumeBtn, quitBtn, settingsBtn;

    public PauseDialog(Activity a) {
        super(a);
        this.gameViewActivity = a;
        this.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.pause_dialog);
        this.setCanceledOnTouchOutside(false);

        Button resumeBtn = (Button) findViewById(R.id.resume_button);
        Button quitBtn = (Button) findViewById(R.id.quit_button);

        resumeBtn.setOnClickListener(this);
        quitBtn.setOnClickListener(this);
    }



    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.quit_button:

                MainActivity.soundEffectsUtil.play(R.raw.quit_dialog);
//                dismiss();
                gameViewActivity.finish();

                MainActivity.musicPlayer.stopMusic(false);
                gamePaused = false;
                break;

            case R.id.resume_button:
                MainActivity.soundEffectsUtil.play(R.raw.quit_dialog);
                dismiss();
                gamePaused = false;
                MainActivity.musicPlayer.startMusic(true);
                break;

            default:
                break;
        }
    }

    @Override
    public void onBackPressed() {}
}