package com.example.open_library.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.text.Editable;
import android.text.TextWatcher;
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

public class RequestDialog extends DialogFragment {

    Book dialogBook;
    TextView titleTextView;
    TextView detailsTextView;
    TextView authorTextView;
    ImageView imageView;
    Button approveButton;
    Button denyButton;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        dialogBook = ((HomeActivity)getActivity()).dialogBook;
        View view = inflater.inflate(R.layout.request_dialog_layout, null);


        titleTextView = view.findViewById(R.id.titleTextView);
        authorTextView = view.findViewById(R.id.authorTextView);
        detailsTextView  = view.findViewById(R.id.descriptionTextView);
        imageView = view.findViewById(R.id.imageView);
        approveButton = view.findViewById(R.id.approveRequest);
        denyButton = view.findViewById(R.id.denyRequest);

        titleTextView.setText(dialogBook.getBookTitle());
        detailsTextView.setText(dialogBook.getDescription());

        Picasso.get().load(dialogBook.getUrl()).into(imageView);

        approveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                approve();
            }
        });

        denyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        builder.setView(view);
        return builder.create();
    }

    public void approve() {
        // db add state: lent
        dialogBook.setState("lent");

    }
}
