package amit_yoav.deep_diving.dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import amit_yoav.deep_diving.MainActivity;
import amit_yoav.deep_diving.R;


public class GameOverDialog extends Dialog implements android.view.View.OnClickListener {

    private Activity gameViewActivity;
    private int scores = 0, bestScores = 0;

    public GameOverDialog(Activity a) {
        super(a);
        this.gameViewActivity = a;
        this.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
    }

    public void setScores(int scores) {
        this.scores = scores;
    }
    public void setBestScore(int scores) {this.bestScores = scores;}

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.gameover_dialog);
        this.setCanceledOnTouchOutside(false);

        Button rstBtn = (Button) findViewById(R.id.restart_game_button);
        Button bckBtn = (Button) findViewById(R.id.back_main_menu_button);
        TextView scoresText = (TextView) findViewById(R.id.end_score);
        TextView bestScoreText = (TextView) findViewById(R.id.best_score);

        scoresText.setText(String.valueOf(scores));
        bestScoreText.setText(String.valueOf(bestScores));

        Typeface face= Typeface.createFromAsset(
                gameViewActivity.getAssets(),"fonts/SF_Slapstick_Comic_Bold_Oblique.ttf");
        scoresText.setTypeface(face);
        bestScoreText.setTypeface(face);

        rstBtn.setOnClickListener(this);
        bckBtn.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.restart_game_button:
                gameViewActivity.recreate();
//                MainActivity.musicPlayer.
                MainActivity.musicPlayer.switchMusic(R.raw.game);
                MainActivity.soundEffectsUtil.play(R.raw.start_bubble);
                break;

            case R.id.back_main_menu_button:
                gameViewActivity.finish();
                MainActivity.soundEffectsUtil.play(R.raw.quit_dialog);
                break;

            default:
                break;
        }
    }

    @Override
    public void onBackPressed() {}
}