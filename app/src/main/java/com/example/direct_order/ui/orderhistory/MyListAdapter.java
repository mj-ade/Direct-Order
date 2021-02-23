package com.example.direct_order.ui.orderhistory;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.direct_order.R;
import com.example.direct_order.writereview.WritereviewActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.appbar.AppBarLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.Source;

import java.util.HashMap;
import java.util.List;

public class MyListAdapter extends RecyclerView.Adapter<MyListAdapter.ViewHolder> {


    private List<String> myList;


    public MyListAdapter(List<String> myList) {
        this.myList = myList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.myorderlist, parent, false);

        return new ViewHolder(v);
    }

    @Override

    public void onBindViewHolder(final ViewHolder holder, int position) {

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String uid = user.getUid();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String x = String.valueOf(position);
        String my = myList.get(position);
        holder.mText.setText(my);

        //CollectionReference coRef = db.collection("customers").document(uid).collection("orders");
        DocumentReference doRef = db.collection("customers").document(uid).collection("orders").document(x);
        doRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                if (value != null && value.exists()) {
                    if(value.get("deposit").toString().trim().equals("true"))
                        holder.mImage.setImageResource(R.drawable.progress2);
                    else
                        holder.mImage.setImageResource(R.drawable.progress1);
                }
            }
        });

        holder.mContent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //메세지 보내기
                Context context = v.getContext();
                //Toast.makeText(context,position+": "+ my+" click!",Toast.LENGTH_LONG).show();
                if(position==0) {
                    /*((AppCompatActivity) context).getSupportFragmentManager().beginTransaction()
                            .replace(R.id.nav_host_fragment, new Cake_shop())
                            .commit();*/
                }
            }

        });
        holder.mContent2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //리뷰 쓰기
                Context context = v.getContext();
                //Toast.makeText(context,position+": "+ my+" click!",Toast.LENGTH_LONG).show();
                Intent intent = new Intent((AppCompatActivity) context, WritereviewActivity.class);
                intent.putExtra("position", position);
                context.startActivity(intent);
            }

        });

        holder.mContent3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //주문 상세 보기
                Context context = v.getContext();
                //Toast.makeText(context,position+": "+ my+" click!",Toast.LENGTH_LONG).show();
                AlertDialog.Builder builder = new AlertDialog.Builder((AppCompatActivity) context).setNegativeButton("확인",null);
                builder.setTitle("<주문 상세 보기>");

                TextView tv = new TextView(context);
                tv.setTextSize(TypedValue.COMPLEX_UNIT_SP,17);
                tv.setGravity(View.TEXT_ALIGNMENT_GRAVITY);
                //CollectionReference coRef = db.collection("customers").document(uid).collection("orders");
                doRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                       @Override
                                                       public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                           if (task.isSuccessful()) {
                                                               DocumentSnapshot document = task.getResult();
                                                               String keys = document.getData().keySet().toString();
                                                               String values = document.getData().values().toString();
                                                               int size = document.getData().size();

                                                               String key[] =keys.split(",");
                                                               String value[]=values.split(",");
                                                               String s = " ";

                                                               for(int i=0;i<size;i++){
                                                                   if(key[i].contains("deposit"))
                                                                       i++;

                                                                   s = s + key[i]+": "+value[i];
                                                                   if(i<size-1)
                                                                       s = s + "\n";
                                                                   s = s.replace("[","");
                                                                   s = s.replace("]","");
                                                               }
                                                               tv.setText(s);
                                                           }
                                                       }
                                                   });


                builder.setView(tv);
                builder.create().show();

            }

        });

        holder.mContent4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //입금 완료 버튼
                Context context = v.getContext();

                doRef.update("deposit",true);


               // holder.mImage.setImageResource(R.drawable.progress2);
                //Toast.makeText(context,position+": "+ my+" click!",Toast.LENGTH_LONG).show();

            }

        });
    }

    @Override
    public int getItemCount() {
        return myList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public final View mView;
        public final TextView mText;
        public final Button mContent, mContent2, mContent3, mContent4;
        public final ImageView mImage;
        public ViewHolder(View view) {

            super(view);

            mView = view;
            mText=view.findViewById(R.id.orders);
            mContent = view.findViewById(R.id.send);
            mContent2 = view.findViewById(R.id.write);
            mContent3 = view.findViewById(R.id.detail);
            mContent4 = view.findViewById(R.id.complete);
            mImage=view.findViewById(R.id.progress);
        }

    }
}
