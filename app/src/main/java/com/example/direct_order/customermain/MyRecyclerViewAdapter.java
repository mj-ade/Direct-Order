package com.example.direct_order.customermain;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.direct_order.R;

import java.util.ArrayList;

public class MyRecyclerViewAdapter extends RecyclerView.Adapter<MyRecyclerViewAdapter.ViewHolder> {

    private ArrayList<CustomerMainData> mData = new ArrayList<>();


    public class ViewHolder extends RecyclerView.ViewHolder {
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
            likeBtn = itemView.findViewById(R.id.likeBtn);
            orderBtn = itemView.findViewById(R.id.orderBtn);
            reviewBtn = itemView.findViewById(R.id.reviewBtn);
            messageBtn = itemView.findViewById(R.id.messageBtn);
        }
    }

    @NonNull
    @Override
    public MyRecyclerViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.customermain_grid_item, parent, false);

        return new ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull MyRecyclerViewAdapter.ViewHolder holder, int position) {
        holder.market_img.setImageResource(mData.get(position).getImgId());
        holder.market_name.setText(mData.get(position).getMarket_name());

        //onItemClick listener here
        holder.likeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mData.get(position).isLike()==false){
                    holder.likeBtn.setImageResource(R.drawable.heart);//채워진하트로
                    mData.get(position).setLike(true);
                }else if(mData.get(position).isLike()==true){
                    holder.likeBtn.setImageResource(R.drawable.empty);//빈하트로
                    mData.get(position).setLike(false);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public void addData (CustomerMainData ndata){
        mData.add(ndata);
    }
}
