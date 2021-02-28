package com.example.direct_order.customermain;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.request.target.Target;
import com.example.direct_order.GlideApp;
import com.example.direct_order.R;
import com.example.direct_order.productorder.ProductOrderActivity;
import com.example.direct_order.reviewlist.ReviewListActivity;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;
import java.util.Map;

public class MarketAdapter extends FirestoreRecyclerAdapter<Market, MarketAdapter.ViewHolder> {
    Context context;
    CollectionReference customerFavorRef;

    public MarketAdapter(@NonNull FirestoreRecyclerOptions<Market> options, CollectionReference customerFavorRef, Context context) {
        super(options);
        this.context = context;
        this.customerFavorRef = customerFavorRef;
    }

    @Override
    protected void onBindViewHolder(@NonNull MarketAdapter.ViewHolder holder, int position, @NonNull Market model) {
        for(String s : CustomerMainFragment.favoriteMarket) {
            if (model.getShopuid().equals(s)) {
                holder.likeBtn.setImageResource(R.drawable.ic_like2);
                holder.likeBtn.setTag("true");
                break;
            }
        }

        holder.market_name.setText(model.getShopname());
        if (model.getShopuid() != null) {
            StorageReference ref = FirebaseStorage.getInstance().getReference(model.getShopuid());
            GlideApp.with(context)
                    .load(ref)
                    .override(Target.SIZE_ORIGINAL)
                    .into(holder.market_img);
        }

        holder.market_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse("https://instagram.com/" + model.getInstagram() + "/"));
                context.startActivity(intent);
            }
        });

        holder.likeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (holder.likeBtn.getTag().equals("false")) {
                    Map<String, Boolean> map = new HashMap<>();
                    map.put("like", true);
                    customerFavorRef.document(model.getShopuid()).set(map);
                    CustomerMainFragment.favoriteMarket.add(model.getShopuid());
                    holder.likeBtn.setImageResource(R.drawable.ic_like2);
                    holder.likeBtn.setTag("true");
                }
                else {
                    customerFavorRef.document(model.getShopuid()).delete();
                    CustomerMainFragment.favoriteMarket.remove(model.getShopuid());
                    holder.likeBtn.setImageResource(R.drawable.ic_like1);
                    holder.likeBtn.setTag("false");
                }
            }
        });

        holder.orderBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ProductOrderActivity.class);
                intent.putExtra("orderEdit", model.isOrderedit());
                intent.putExtra("shopuid", model.getShopuid());
                context.startActivity(intent);
            }
        });

        holder.reviewBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ReviewListActivity.class);
                intent.putExtra("shopuid", model.getShopuid());
                context.startActivity(intent);
            }
        });

        holder.messageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri smsUri = Uri.parse("sms:" + model.getShopnum());
                Intent sendIntent = new Intent(Intent.ACTION_SENDTO, smsUri);
                context.startActivity(sendIntent);
            }
        });
    }

    @NonNull
    @Override
    public MarketAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View itemView = inflater.inflate(R.layout.customermain_grid_item, parent, false);
        return new ViewHolder(itemView);
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private TextView market_name;
        private ImageView market_img;
        private ImageButton likeBtn;
        private Button orderBtn;
        private Button reviewBtn;
        private Button messageBtn;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            market_name = itemView.findViewById(R.id.market_name);
            market_img = itemView.findViewById(R.id.market_img);
            likeBtn = itemView.findViewById(R.id.like_btn);
            orderBtn = itemView.findViewById(R.id.orderBtn);
            reviewBtn = itemView.findViewById(R.id.reviewBtn);
            messageBtn = itemView.findViewById(R.id.messageBtn);
        }
    }
}
