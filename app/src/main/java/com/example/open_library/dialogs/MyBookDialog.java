package com.example.open_library.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.open_library.Book;
import com.example.open_library.HomeActivity;
import com.example.open_library.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.squareup.picasso.Picasso;

public class MyBookDialog extends DialogFragment {


    Book dialogBook;
    TextView titleTextView;
    TextView detailsTextView;
    TextView authorTextView;
    ImageView imageView;
    Button changeStateButton;
    Button cancelButton;
    EditText daysEdittext;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        dialogBook = ((HomeActivity)getActivity()).dialogBook;
        View view = inflater.inflate(R.layout.my_book_dialog, null);


        titleTextView = view.findViewById(R.id.titleTextView);
        authorTextView = view.findViewById(R.id.authorTextView);
        detailsTextView  = view.findViewById(R.id.descriptionTextView);
        imageView = view.findViewById(R.id.imageView);
        changeStateButton = view.findViewById(R.id.changeStateButton);
        cancelButton = view.findViewById(R.id.cancelButton);
        daysEdittext = view.findViewById(R.id.daysEditText);

        titleTextView.setText(dialogBook.getBookTitle());
        detailsTextView.setText(dialogBook.getDescription());

        Picasso.get().load(dialogBook.getUrl()).into(imageView);
//        Log.d("TAG", dialogBook.)
        if (dialogBook.getState().equals("None")) {
            daysEdittext.setAlpha(0f);
            changeStateButton.setText("Private");
        } else {
            changeStateButton.setText("Available");
        }

        daysEdittext.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                try {
                    int numOfDays = Integer.parseInt(daysEdittext.getText().toString());
                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                    ((HomeActivity)getActivity()).changeState(dialogBook.getIsbn(), user.getUid(), "available", numOfDays);
                } catch (Exception e) {

                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        changeStateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                listener.onDialogPositiveClick(dialogBook.getIsbn());
                if (dialogBook.getState().equals("None")) {
                    changeStateButton.setText("Available");
                    dialogBook.setState("Available");
                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                    ((HomeActivity)getActivity()).changeState(dialogBook.getIsbn(), user.getUid(), "available", 14);
                    daysEdittext.setAlpha(1f);
                } else {
                    changeStateButton.setText("Private");
                    dialogBook.setState("None");
                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                    ((HomeActivity)getActivity()).changeState(dialogBook.getIsbn(), user.getUid(), "None", 0);
                    daysEdittext.setAlpha(0f);
                }
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });


        builder.setView(view);
        return builder.create();
    }

    /* The activity that creates an instance of this dialog fragment must
     * implement this interface in order to receive event callbacks.
     * Each method passes the DialogFragment in case the host needs to query it. */
//    public interface MyBookDialogListener {
//        void onChangeStateClick(String isbn, String state);
////        void onDialogNegativeClick(int num);
//    }
//
//    // Use this instance of the interface to deliver action events
//    MyBookDialogListener listener;
//
//    // Override the Fragment.onAttach() method to instantiate the NoticeDialogListener
//    @Override
//    public void onAttach(Context context) {
//        super.onAttach(context);
//        // Verify that the host activity implements the callback interface
//        try {
//            // Instantiate the NoticeDialogListener so we can send events to the host
//            listener = (MyBookDialogListener) context;
//        } catch (ClassCastException e) {
//            // The activity doesn't implement the interface, throw exception
//            throw new ClassCastException(getActivity().toString()
//                    + " must implement NoticeDialogListener");
//        }
//    }

}
