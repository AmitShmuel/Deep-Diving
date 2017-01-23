package amit_yoav.deep_diving.dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;

import amit_yoav.deep_diving.MainActivity;
import amit_yoav.deep_diving.R;

public class InfoDialog extends Dialog {

    private MainActivity mainActivity;

    public InfoDialog(Activity a) {
        super(a);
//         TODO Auto-generated constructor stub
        mainActivity = (MainActivity) a;
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
