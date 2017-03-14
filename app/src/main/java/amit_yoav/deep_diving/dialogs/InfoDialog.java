package amit_yoav.deep_diving.dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Window;

import amit_yoav.deep_diving.MainActivity;
import amit_yoav.deep_diving.R;

public class InfoDialog extends Dialog {

    private MainActivity mainActivity;

    public InfoDialog(Activity a) {
        super(a);
        mainActivity = (MainActivity) a;
        this.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        this.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.info_dialog);
        this.setCanceledOnTouchOutside(false);
    }

    @Override
    public void onBackPressed() {
        mainActivity.closeDialog(null);
    }
}
