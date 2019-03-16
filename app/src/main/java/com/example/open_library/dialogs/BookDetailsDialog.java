package com.example.open_library.dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;

import com.example.open_library.R;

public class BookDetailsDialog extends Dialog implements
        android.view.View.OnClickListener{

    private Activity mActivity;
    public Dialog d;
    public Button yes, no;

    public BookDetailsDialog(Activity activity) {
        super(activity);
        mActivity = activity;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_book_details);

    }

    @Override
    public void onClick(View v) {

    }
}
