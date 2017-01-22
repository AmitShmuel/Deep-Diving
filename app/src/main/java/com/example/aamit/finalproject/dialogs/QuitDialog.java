package com.example.aamit.finalproject.dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;

import com.example.aamit.finalproject.R;

public class QuitDialog extends Dialog {

    private Activity mainActivity;

    public QuitDialog(Activity a) {
        super(a);
        mainActivity = a;
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