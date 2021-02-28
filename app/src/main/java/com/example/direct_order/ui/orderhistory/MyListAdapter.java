package com.example.direct_order.ui.orderhistory;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.direct_order.R;
import com.example.direct_order.write_review.WriteReviewActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MyListAdapter extends RecyclerView.Adapter<MyListAdapter.ViewHolder> {


    private List<String> myList;
    String s;
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

        long now = System.currentTimeMillis();
        Date mDate = new Date(now);
        SimpleDateFormat simpleDate = new SimpleDateFormat("yyyy/MM/dd");
        String getTime = simpleDate.format(mDate);

        DocumentReference doRef = db.collection("customers").document(uid).collection("orders").document(x);

        doRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                if (value != null && value.exists()) {

                    DocumentReference selldoc = db.collection("markets").document(value.get("shopuid").toString().trim()).collection("OrderList").document(value.get("docid").toString().trim());

                    selldoc.addSnapshotListener(new EventListener<DocumentSnapshot>() {
                        @Override
                        public void onEvent(@Nullable DocumentSnapshot value1, @Nullable FirebaseFirestoreException error1) {
                            if(value1!=null&&value1.exists()){
                                int compare = getTime.compareTo(value.get("pickup").toString().trim());
                                if(value1.get("process").toString().trim().equals("1"))
                                    holder.mImage.setImageResource(R.drawable.progress2);


                                else if(value1.get("process").toString().trim().equals("2")){
                                    if(compare>=0)
                                        holder.mImage.setImageResource(R.drawable.progress4);
                                    else
                                        holder.mImage.setImageResource(R.drawable.progress3);
                                }

                                else
                                    holder.mImage.setImageResource(R.drawable.progress1);

                            }

                        }
                    });

                    //리뷰보기 버튼 안눌리게
                     if(value.get("review").toString().trim().equals("true"))
                        holder.mContent2.setBackgroundColor(holder.itemView.getResources().getColor(R.color.white));


                }
            }
        });


        holder.mContent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //메세지 보내기
                //인스타연결?

                Context context = v.getContext();

            }

        });
        holder.mContent2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //리뷰 쓰기
                Context context = v.getContext();
                //Toast.makeText(context,position+": "+ my+" click!",Toast.LENGTH_LONG).show();
                doRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                        if (value != null && value.exists()) {
                            if(value.get("review").toString().trim().equals("true")) {
                                Toast.makeText(v.getContext(),"리뷰는 한 번만 작성할 수 있습니다.",Toast.LENGTH_LONG).show();
                            }
                            else{
                                Intent intent = new Intent((AppCompatActivity) context, WriteReviewActivity.class);
                                intent.putExtra("position", position);
                                context.startActivity(intent);
                            }
                        }
                    }
                });


            }

        });

        holder.mContent3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //주문 상세 보기
                Context context = v.getContext();

                LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View myScrollView = inflater.inflate(R.layout.scroll_detail, null, false);

                ImageView iv = (ImageView) myScrollView.findViewById(R.id.orderdetail);


               doRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                        if (value != null && value.exists()) {
                            //screenshot 가져오기
                            String screenshot = value.get("screenshot").toString().trim();
                            //String file = screenshot.substring(screenshot.lastIndexOf("/"));

                            StorageReference rootRef = FirebaseStorage.getInstance().getReference(screenshot);
                            Glide.with(v.getContext()).load(rootRef).placeholder(R.drawable.white).into(iv);

                        }
                    }
                });


                AlertDialog.Builder builder = new AlertDialog.Builder((AppCompatActivity) v.getContext()).setView(myScrollView).setTitle("<주문 상세 보기>").setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

                //Toast.makeText(holder.itemView.getContext(), "Options was clicked", Toast.LENGTH_LONG).show();
                builder.create().show();


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
        public final Button mContent, mContent2, mContent3;
        public final ImageView mImage;
        public ViewHolder(View view) {

            super(view);

            mView = view;
            mText=view.findViewById(R.id.orders);
            mContent = view.findViewById(R.id.send);
            mContent2 = view.findViewById(R.id.write);
            mContent3 = view.findViewById(R.id.detail);
            mImage=view.findViewById(R.id.progress);
        }

    }
}
