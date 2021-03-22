package com.example.thuantran.wego.Adapter;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.thuantran.wego.Object.ReviewContext;
import com.example.thuantran.wego.R;
import com.squareup.picasso.Picasso;

import java.util.List;

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.RecyclerViewHolder> {


    private List<ReviewContext> listReview;

    public ReviewAdapter(List<ReviewContext> listReview){
        this.listReview = listReview;
    }




    @NonNull
    @Override
    public RecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.layout_review, parent, false);
        return new RecyclerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerViewHolder holder, int position) {


        ReviewContext review = listReview.get(position);

        holder.name.setText(review.getName());
        holder.time.setText(review.getTimestamp());
        holder.smsrw.setText(review.getContext());
        holder.ratingBar.setRating(Float.valueOf(review.getRate()));

        Picasso.get().load(review.getAvatar())
                .resize(250,250)
                .centerCrop()
                .into(holder.avatar);

    }

    @Override
    public int getItemCount() {
        return listReview == null ? 0 : listReview.size();
    }




    class RecyclerViewHolder extends RecyclerView.ViewHolder {
        private ImageView avatar;
        private RatingBar ratingBar;
        private TextView name, smsrw, time;

        RecyclerViewHolder(View itemView) {
            super(itemView);
            avatar       = itemView.findViewById(R.id.avatar);
            name         = itemView.findViewById(R.id.name);
            ratingBar    = itemView.findViewById(R.id.pdanhgia);
            smsrw        = itemView.findViewById(R.id.context);
            time         = itemView.findViewById(R.id.daterw);
        }
    }




}
