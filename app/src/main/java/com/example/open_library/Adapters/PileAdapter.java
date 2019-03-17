package com.example.open_library.Adapters;


import android.content.Context;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.open_library.Book;
import com.example.open_library.HomeActivity;
import com.example.open_library.R;
import com.example.open_library.dialogs.MyBookDialog;
import com.example.open_library.dialogs.ViewBookDialog;
import com.google.android.gms.maps.model.LatLng;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class PileAdapter extends RecyclerView.Adapter<PileAdapter.ViewHolder> {

    private FragmentManager fragmentManager;
    private Context context;

    public ArrayList<Book> books = new ArrayList<>();

    public PileAdapter(Context _context, FragmentManager _fragmentManager) {
        this.context = _context;
        this.fragmentManager = _fragmentManager;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.card_layout, viewGroup, false); //CardView inflated as RecyclerView list item
        ViewHolder viewHolder = new ViewHolder(v);


        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {

        int index = position;


        final int idx1 = getIndex(index, 0);
        final int idx2 = getIndex(index, 1);
        final int idx3 = getIndex(index, 2);

        if (idx1 != -1) {
            final Book book1 = books.get(idx1);

            viewHolder.titleTextView.setText(book1.getBookTitle());
            viewHolder.authorTextView.setText(book1.getAuthor());
            Picasso.get().load(book1.getUrl()).into(viewHolder.thumbImageView);
            viewHolder.thumbImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ((HomeActivity)context).dialogBook = new Book(book1.getIsbn(), book1.getBookTitle(), book1.getAuthor(), book1.getUrl(), book1.getDescription(), new LatLng(-37.814,144.96332));
                    ((HomeActivity)context).dialogBook.setBookId(book1.getBookId());
                    ViewBookDialog dialog = new ViewBookDialog();
                    dialog.show(fragmentManager, "book_dialog");
                }
            });
        } else {
            viewHolder.titleTextView.setText("");
            viewHolder.authorTextView.setText("");
            viewHolder.thumbImageView.setBackgroundResource(R.drawable.grey_backgroud);
        }

        if (idx2 != -1) {
            final Book book2 = books.get(idx2);
            viewHolder.titleTextView2.setText(book2.getBookTitle());
            viewHolder.authorTextView2.setText(book2.getAuthor());
            Picasso.get().load(book2.getUrl()).into(viewHolder.thumbImageView2);
            viewHolder.thumbImageView2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ((HomeActivity)context).dialogBook = new Book(book2.getIsbn(), book2.getBookTitle(), book2.getAuthor(), book2.getUrl(), book2.getDescription(), new LatLng(-37.814,144.96332));
                    ((HomeActivity)context).dialogBook.setBookId(book2.getBookId());
                    ViewBookDialog dialog = new ViewBookDialog();
                    dialog.show(fragmentManager, "book_dialog");
                }
            });
        } else {
            viewHolder.titleTextView2.setText("");
            viewHolder.authorTextView2.setText("");
            viewHolder.thumbImageView2.setBackgroundResource(R.drawable.grey_backgroud);
        }

        if (idx3 != -1) {
            final Book book3 = books.get(idx1);
            viewHolder.titleTextView3.setText(book3.getBookTitle());
            viewHolder.authorTextView3.setText(book3.getAuthor());
            Picasso.get().load(book3.getUrl()).into(viewHolder.thumbImageView3);
            viewHolder.thumbImageView3.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ((HomeActivity)context).dialogBook = new Book(book3.getIsbn(), book3.getBookTitle(), book3.getAuthor(), book3.getUrl(), book3.getDescription(), new LatLng(-37.814,144.96332));
                    ((HomeActivity)context).dialogBook.setBookId(book3.getBookId());
                    ViewBookDialog dialog = new ViewBookDialog();
                    dialog.show(fragmentManager, "book_dialog");
                }
            });
        } else {
            viewHolder.titleTextView3.setText("");
            viewHolder.authorTextView3.setText("");
            viewHolder.thumbImageView3.setBackgroundResource(R.drawable.grey_backgroud);
        }

//        if (index+1 > Math.ceil(books.size()/3.0)) {
//            viewHolder.thumbImageView.setScaleX(0);
//            viewHolder.thumbImageView.setScaleY(0);
//            viewHolder.thumbImageView2.setScaleX(0);
//            viewHolder.thumbImageView2.setScaleY(0);
//            viewHolder.thumbImageView3.setScaleX(0);
//            viewHolder.thumbImageView3.setScaleY(0);
//
//            viewHolder.titleTextView.setScaleX(0);
//            viewHolder.titleTextView.setScaleY(0);
//            viewHolder.titleTextView2.setScaleX(0);
//            viewHolder.titleTextView2.setScaleY(0);
//            viewHolder.titleTextView3.setScaleX(0);
//            viewHolder.titleTextView3.setScaleY(0);
//
//            viewHolder.authorTextView.setScaleX(0);
//            viewHolder.authorTextView.setScaleY(0);
//            viewHolder.authorTextView2.setScaleX(0);
//            viewHolder.authorTextView2.setScaleY(0);
//            viewHolder.authorTextView3.setScaleX(0);
//            viewHolder.authorTextView3.setScaleY(0);
//
//            viewHolder.cardView.setScaleX(0);
//            viewHolder.cardView.setScaleY(0);
//            viewHolder.cardView2.setScaleX(0);
//            viewHolder.cardView2.setScaleY(0);
//            viewHolder.cardView3.setScaleX(0);
//            viewHolder.cardView3.setScaleY(0);
//
//            viewHolder.constraintLayout.setScaleX(0);
//            viewHolder.constraintLayout.setScaleY(0);
//        }



    }

    public int getIndex(int position, int num) {
        int index = 3*position + num;
        if (index >= books.size()) {
            index = -1;
        }

        return index;
    }

    @Override
    public int getItemCount() {
        return books.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        private View itemView;
        private TextView titleTextView;
        private TextView authorTextView;
        private ImageView thumbImageView;
        private CardView cardView;

        private TextView titleTextView2;
        private TextView authorTextView2;
        private ImageView thumbImageView2;
        private CardView cardView2;

        private TextView titleTextView3;
        private TextView authorTextView3;
        private ImageView thumbImageView3;
        private CardView cardView3;

        private ConstraintLayout constraintLayout;

        private ViewHolder(View view) {
            super(view);

            this.itemView = view;
            titleTextView = view.findViewById(R.id.titleTextView);
            authorTextView = view.findViewById(R.id.authorTextView);
            thumbImageView = view.findViewById(R.id.thumbImageView);
            cardView = view.findViewById(R.id.cardView);

            titleTextView2 = view.findViewById(R.id.titleTextView2);
            authorTextView2 = view.findViewById(R.id.authorTextView2);
            thumbImageView2 = view.findViewById(R.id.thumbImageView2);
            cardView2 = view.findViewById(R.id.cardView2);

            titleTextView3 = view.findViewById(R.id.titleTextView3);
            authorTextView3 = view.findViewById(R.id.authorTextView3);
            thumbImageView3 = view.findViewById(R.id.thumbImageView3);
            cardView3 = view.findViewById(R.id.cardView3);

            constraintLayout = view.findViewById(R.id.constraintLayout);
        }
    }




}
