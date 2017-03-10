package amit_yoav.deep_diving.dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;

import amit_yoav.deep_diving.MainActivity;
import amit_yoav.deep_diving.R;

public class HowToPlayDialog extends Dialog {

//    private MainActivity mainActivity;

    public HowToPlayDialog(Activity a) {
        super(a, R.style.full_screen_dialog);
//        mainActivity = (MainActivity) a;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.howtoplay_dialog);
        this.setCanceledOnTouchOutside(false);

        getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);


        findViewById(R.id.quit_how_to_play_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.soundEffectsUtil.play(R.raw.quit_dialog);
                HowToPlayDialog.this.dismiss();
            }
        });
    }

    @Override
    public void onBackPressed() {
        MainActivity.soundEffectsUtil.play(R.raw.quit_dialog);
        dismiss();
    }
/*
    @Override
    public void onDismiss(DialogInterface dialog) {
        if(mainActivity.getIsHowToPlayShown()) {
            mainActivity.startGame(null);
        }
    }
    */
}