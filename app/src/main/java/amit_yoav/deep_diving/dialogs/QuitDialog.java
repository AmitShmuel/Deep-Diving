package amit_yoav.deep_diving.dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;

import amit_yoav.deep_diving.R;

public class QuitDialog extends Dialog {

    public QuitDialog(Activity a) {
        super(a);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.quit_dialog);

        this.setCanceledOnTouchOutside(false);
    }

    @Override
    public void onBackPressed() {}
}