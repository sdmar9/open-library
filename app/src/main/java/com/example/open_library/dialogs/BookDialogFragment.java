package com.example.open_library.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.open_library.Book;
import com.example.open_library.HomeActivity;
import com.example.open_library.MainActivity;
import com.example.open_library.R;
import com.squareup.picasso.Picasso;

public class BookDialogFragment extends DialogFragment {

    Book dialogBook;
    TextView titleTextView;
    TextView detailsTextView;
    TextView authorTextView;
    ImageView imageView;
    Button requestButton;
    Button cancelButton;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();

        View view = inflater.inflate(R.layout.book_dialog, null);
        dialogBook = ((HomeActivity)getActivity()).dialogBook;

        titleTextView = view.findViewById(R.id.titleTextView);
        authorTextView = view.findViewById(R.id.authorTextView);
        detailsTextView  = view.findViewById(R.id.descriptionTextView);
        imageView = view.findViewById(R.id.imageView);
        requestButton = view.findViewById(R.id.requestButton);
        cancelButton = view.findViewById(R.id.cancelButton);

        titleTextView.setText(dialogBook.getBookTitle());
        detailsTextView.setText(dialogBook.getDescription());
        Picasso.get().load(dialogBook.getUrl()).into(imageView);


        requestButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onDialogPositiveClick(dialogBook.getIsbn());
                dismiss();
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
    public interface NoticeDialogListener {
        void onDialogPositiveClick(String string);
//        void onDialogNegativeClick(int num);
    }

    // Use this instance of the interface to deliver action events
    NoticeDialogListener listener;

    // Override the Fragment.onAttach() method to instantiate the NoticeDialogListener
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the NoticeDialogListener so we can send events to the host
            listener = (NoticeDialogListener) context;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(getActivity().toString()
                    + " must implement NoticeDialogListener");
        }
    }
}
