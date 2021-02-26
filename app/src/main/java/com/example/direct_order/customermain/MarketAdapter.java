package com.example.direct_order.customermain;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.Target;
import com.example.direct_order.R;
import com.example.direct_order.ordersheet.GlideApp;
import com.example.direct_order.productorder.ProductOrderActivity;
import com.example.direct_order.reviewlist.ReviewListActivity;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class MarketAdapter extends FirestoreRecyclerAdapter<Market, MarketAdapter.ViewHolder> {
    Context context;

    public MarketAdapter(@NonNull FirestoreRecyclerOptions<Market> options, Context context) {
        super(options);
        this.context = context;
    }

    @Override
    protected void onBindViewHolder(@NonNull MarketAdapter.ViewHolder holder, int position, @NonNull Market model) {
        holder.market_name.setText(model.getShopname());
        if (model.getShopuid() != null) {
            Log.d("hi", model.getShopname() + model.getShopuid());
            StorageReference ref = FirebaseStorage.getInstance().getReference(model.getShopuid());//.child();

            GlideApp.with(context)
                    .load(ref)
                    .override(Target.SIZE_ORIGINAL)
                    .into(holder.market_img);
        }
        /**/
        /*FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();
        storageRef.child(shopid).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                //이미지 로드 성공시
                Glide.with(context)
                        .load(uri)
                        .into(holder.market_img);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                //이미지 로드 실패시
                Toast.makeText(context, "실패", Toast.LENGTH_SHORT).show();
            }
        });*/
        holder.market_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse("https://instagram.com/" + model.getInstagram() + "/"));
                context.startActivity(intent);
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
                if(model.getShopuid()==null)
                    Log.d("ghkr", "null이에요");
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
            likeBtn = itemView.findViewById(R.id.likeBtn);
            orderBtn = itemView.findViewById(R.id.orderBtn);
            reviewBtn = itemView.findViewById(R.id.reviewBtn);
            messageBtn = itemView.findViewById(R.id.messageBtn);
        }
    }


/*

    List<String> listShopid = new ArrayList<>();
    List<String> listMarketname = new ArrayList<>();
    String myinsta;

    @Override
    public void onBindViewHolder(@NonNull MarketAdapter.ViewHolder holder, int position) {
        //holder.market_img.setImageResource(mData.get(position).getImgId());
        holder.market_name.setText(mData.get(position).getMarket_name());
        String shopid = mData.get(position).getShopid();

        FirebaseStorage storage = FirebaseStorage.getInstance("gs://direct-order-59b4e.appspot.com/");
        StorageReference storageRef = storage.getReference();
        storageRef.child(shopid).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                //이미지 로드 성공시
                Glide.with(context)
                        .load(uri)
                        .into(holder.market_img);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                //이미지 로드 실패시
                Toast.makeText(context, "실패", Toast.LENGTH_SHORT).show();
            }
        });

        //가게사진 누르면 인스타로 이동
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference docRef = db.collection("markets").document(shopid);
        docRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                if (value != null && value.exists()) {
                    myinsta = "https://instagram.com/" + value.get("instagram").toString().trim() + "/";
                }
            }
        });

        holder.market_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Context context = view.getContext();
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(myinsta));
                context.startActivity(intent);
            }
        });

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
        holder.reviewBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Context context = view.getContext();
                //리뷰리스트 액티비티 필요--!
                //Intent intent = new Intent(리뷰리스트액티비티);
                //context.startActivity(intent);
            }
        });
        holder.messageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //메시지기능연결
            }
        });
        holder.orderBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //주문서 작성 페이지 연결
            }
        });
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public void addData (Market ndata){
        mData.add(ndata);
    }*/


}
