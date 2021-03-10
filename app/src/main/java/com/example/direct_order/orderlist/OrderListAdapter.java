package com.example.direct_order.orderlist;

import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.direct_order.R;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class OrderListAdapter extends FirestoreRecyclerAdapter<Order, OrderListAdapter.ViewHolder> {
    private OnItemClickListener listener;
    private Context context;
    private String[] btnTexts = {"가격/계좌 전송", "입금 확인", "입금 완료"};
    private String shopName, shopAccount;

    public OrderListAdapter(@NonNull FirestoreRecyclerOptions<Order> options, Context context) {
        super(options);
        this.context = context;
        setupShopInfo();
    }

    @Override
    protected void onBindViewHolder(@NonNull ViewHolder holder, int position, @NonNull Order model) {
        holder.name.setText(model.getName());
        holder.date.setText(model.getDate() + " " + model.getTime());
        if (model.getPickup().equals(""))
            holder.pickup.setText(model.getDate());
        else
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
                        sendSmsIntent(model.getName(), model.getPhone(), model.getDate(), model.getTime(), holder.price.getText().toString());

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

    public void sendSmsIntent(String name, String number, String date, String time, String price) {
        String message = "[Direct Order 알림 메시지]\n안녕하세요 " + name + " 고객님.\n"
                + "[" + shopName + "] 입니다.\n\n"
                + "고객님께서 " + date + " " + time + " 에 주문하신 상품의 가격은 " + price + "원 입니다.\n"
                + shopAccount + "로 입금 부탁드립니다:)\n\n"
                + "추가 문의사항이 있으시면 연락주세요.";
        try{
            Uri smsUri = Uri.parse("sms:" + number);
            Intent sendIntent = new Intent(Intent.ACTION_SENDTO, smsUri);
            sendIntent.putExtra("sms_body", message);
            context.startActivity(sendIntent);
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    private void setupShopInfo() {
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DocumentReference marketRef = FirebaseFirestore.getInstance().collection("markets").document(uid);

        marketRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        shopName = (String) document.get("shopname");
                        shopAccount = (String) document.get("shopaccount");
                    }
                    else
                        Log.d("OrderListAdapter", "No such document");
                }
                else {
                    Log.d("OrderListAdapter", "get failed with ", task.getException());
                }
            }
        });
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView name;
        TextView date;
        TextView pickup;
        EditText price;
        Button button_change;

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
