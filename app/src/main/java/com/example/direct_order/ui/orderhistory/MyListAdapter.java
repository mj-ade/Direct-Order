package com.example.direct_order.ui.orderhistory;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
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

import com.bumptech.glide.Glide;
import com.example.direct_order.R;
import com.example.direct_order.write_review.WriteReviewActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

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

                    String screenshot = value.get("screenshot").toString().trim();
                    String file = screenshot.substring(screenshot.lastIndexOf("/"));

                    if(value.get("review").toString().trim().equals("true"))
                        ;//리뷰보기 버튼 안눌리게



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


              //  iv.setImageResource(R.drawable.white);
                doRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                        if (value != null && value.exists()) {
                            //screenshot 가져오기
                            String screenshot = value.get("screenshot").toString().trim();
                            String file = screenshot.substring(screenshot.lastIndexOf("/"));

                            FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
                            StorageReference rootRef = firebaseStorage.getReferenceFromUrl(screenshot.replace(file,"").trim());
                            StorageReference childRef = rootRef.child(file.trim());
                            childRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    Glide.with(v.getContext()).load(uri).placeholder(R.drawable.white).into(iv);                            //네트워크 이미지는 Glide로 해결한다.
                                }                                                                                   //Glide를 쓰지 않으면 Thread + URL을 써야한다.
                            });
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
