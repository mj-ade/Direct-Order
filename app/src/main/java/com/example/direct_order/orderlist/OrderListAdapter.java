package com.example.direct_order.orderlist;

import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.direct_order.R;
import com.example.direct_order.ordersheet.Option;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

public class OrderListAdapter extends FirestoreRecyclerAdapter<Order, OrderListAdapter.ViewHolder> {
    private OnItemClickListener listener;
    private Context context;
    private String[] btnTexts = {"가격/계좌 전송", "입금 확인", "입금 완료"};

    public OrderListAdapter(@NonNull FirestoreRecyclerOptions<Order> options, Context context) {
        super(options);
        this.context = context;
    }

    @Override
    protected void onBindViewHolder(@NonNull ViewHolder holder, int position, @NonNull Order model) {
        holder.name.setText(model.getName());
        holder.date.setText(model.getDate() + " " + model.getTime());
        holder.pickup.setText(model.getPickup() + " " + model.getPickupTime());
        if (model.getPrice() != 0)
            holder.price.setText(model.getPrice() + "");
        else
            holder.price.setText("");

        holder.button_change.setText(btnTexts[model.getProcess()]);
        if (model.getProcess() != 0) {
            holder.price.setKeyListener(null);
            if (model.getProcess() == 2) {
                holder.button_change.setEnabled(false);
                holder.button_change.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#FFFFFF")));
            }
        }

        holder.button_change.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (model.getProcess() == 0) {
                    if (!holder.price.getText().toString().equals("")) {
                        getSnapshots().getSnapshot(position).getReference()
                                .update("price", Integer.parseInt(holder.price.getText().toString()), "process", model.getProcess() + 1)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Log.d("TAG", "DocumentSnapshot successfully updated!");
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.w("TAG", "Error updating document", e);
                                    }
                                });
                    }
                    else {
                        Toast.makeText(context, "가격을 입력해주세요", Toast.LENGTH_SHORT).show();
                    }
                }
                else if (model.getProcess() == 1) {
                    getSnapshots().getSnapshot(position).getReference()
                            .update("process", model.getProcess() + 1)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Log.d("TAG", "DocumentSnapshot successfully updated!");
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.w("TAG", "Error updating document", e);
                                }
                            });
                }
            }
        });
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.orderlist_item, parent,false);
        ViewHolder holder = new ViewHolder(v);
        return holder;
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        public TextView name;
        public TextView date;
        public TextView pickup;
        public EditText price;
        public Button button_change;

        public ViewHolder(View v) {
            super(v);

            name = (TextView) v.findViewById(R.id.textView);
            date = (TextView) v.findViewById(R.id.textView2);
            pickup = (TextView) v.findViewById(R.id.textView3);
            price = (EditText) v.findViewById(R.id.price);
            button_change = (Button) v.findViewById(R.id.send_button);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION && listener != null) {
                        listener.onItemClick(getSnapshots().getSnapshot(position), position);
                    }
                }
            });
        }
    }
}
