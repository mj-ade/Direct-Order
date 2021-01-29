package com.example.direct_order.orderlist;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class OrderlistAdapter extends RecyclerView.Adapter<OrderlistAdapter.ViewHolder> {

    private ArrayList<OrderlistActivity.item> mDataset;
    int[] count;

    public class ViewHolder extends RecyclerView.ViewHolder {
        // 사용될 항목들 선언
        public ImageButton message;
        public TextView name;
        public TextView item_name;
        public TextView date;
        public TextView pickup;
        public Button button_change;

        public ViewHolder(View v) {
            super(v);

            message = (ImageButton) v.findViewById(R.id.imageButton);
            name = (TextView) v.findViewById(R.id.textView);
            item_name = (TextView) v.findViewById(R.id.textView2);
            date = (TextView) v.findViewById(R.id.textView3);
            pickup = (TextView) v.findViewById(R.id.textView4);
            button_change = (Button) v.findViewById(R.id.button2);
        }
    }

    // 생성자 - 넘어 오는 데이터파입에 유의해야 한다.
    public OrderlistAdapter(ArrayList<OrderlistActivity.item> myDataset) {
        mDataset = myDataset;
        count = new int[mDataset.size()];
    }

    @NonNull
    @Override
    public com.example.orderlist.OrderlistAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.orderlist_item, parent,false);

        ViewHolder holder = new ViewHolder(v);
        return holder;
    }

    //int[] count={0,0,0}; //배열 똑바로하기


    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.name.setText("Name : "+mDataset.get(position).getName());
        holder.item_name.setText("Item : "+mDataset.get(position).getItemname());
        holder.date.setText("Date : "+mDataset.get(position).getDate());
        holder.pickup.setText("Pickup : "+mDataset.get(position).getPickup());

        //onItemClick listener here
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Context context = v.getContext();
                //버튼 클릭 이벤트 처리 여기서//
                Intent intent = new Intent(v.getContext(), OrderDetailActivity.class);
                intent.putExtra("name", mDataset.get(position).getName());
                intent.putExtra("item_name", mDataset.get(position).getItemname());
                intent.putExtra("date", mDataset.get(position).getDate());
                intent.putExtra("pickup", mDataset.get(position).getPickup());

                context.startActivity(intent);

            }
        });
        holder.message.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Context context = v.getContext();
                Intent intent = new Intent(v.getContext(), MessageActivity.class);
                intent.putExtra("name", mDataset.get(position).getName());
                context.startActivity(intent);
            }
        });
        holder.button_change.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ++count[position];
                if(count[position]==1){
                    holder.button_change.setText("입금 확인");
                }else if(count[position]==2){
                    holder.button_change.setText("승인된 주문");
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }

}
