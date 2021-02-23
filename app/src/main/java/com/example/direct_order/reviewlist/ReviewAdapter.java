package com.example.direct_order.reviewlist;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.request.target.Target;
import com.example.direct_order.R;
import com.example.direct_order.ordersheet.GlideApp;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class ReviewAdapter extends FirestoreRecyclerAdapter<ReviewData, ReviewAdapter.ReviewDataHolder> {
    Context context;

    public ReviewAdapter(@NonNull FirestoreRecyclerOptions<ReviewData> options, Context context) {
        super(options);
        this.context = context;
    }

    @Override
    protected void onBindViewHolder(@NonNull ReviewDataHolder holder, int position, @NonNull ReviewData model) {
        holder.ratingBar.setRating(model.getStar());
        Log.d("why", model.getContent());
        holder.textView.setText(model.getContent());
        StorageReference ref = FirebaseStorage.getInstance().getReference(model.getImage());
        GlideApp.with(context)
                .load(ref)
                .override(Target.SIZE_ORIGINAL)
                .into(holder.imageView);
    }

    @NonNull
    @Override
    public ReviewDataHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View itemView = inflater.inflate(R.layout.review_item, parent, false);
        return new ReviewDataHolder(itemView);
    }

    class ReviewDataHolder extends RecyclerView.ViewHolder {
        private RatingBar ratingBar;
        private ImageView imageView;
        private TextView textView;

        public ReviewDataHolder(View itemView) {
            super(itemView);

            ratingBar = itemView.findViewById(R.id.ratingBar);
            imageView = itemView.findViewById(R.id.imageView);
            textView = itemView.findViewById(R.id.textView);
        }
    }
}