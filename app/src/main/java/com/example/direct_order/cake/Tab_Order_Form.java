package com.example.direct_order.cake;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.direct_order.R;
import com.example.direct_order.ordersheet.GlideApp;
import com.example.direct_order.ordersheet.Option;
import com.example.direct_order.ordersheet.OptionAdapter;
import com.example.direct_order.ordersheet.OrderSheetActivity;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class Tab_Order_Form extends Fragment {
    private final String TAG = "TAB_ORDER_FORM";
    private Button btn;
    private CardView cardView;
    private ViewGroup viewGroup;
    private LinearLayout cover;
    private OptionAdapter adapter;
    private boolean isExist;

    public Tab_Order_Form(boolean isExist) {
        this.isExist = isExist;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_tab__order__form, container, false);
        btn = root.findViewById(R.id.add_form);
        btn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(getActivity(), OrderSheetActivity.class),100);
            }
        });
        cardView = root.findViewById(R.id.cardView);
        viewGroup = root.findViewById(R.id.order_sheet);
        cover = root.findViewById(R.id.cover);
        cover.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(getActivity(), OrderSheetActivity.class),100);
            }
        });

        setupOrderSheetButton();

        DocumentReference myOrderSheet = FirebaseFirestore.getInstance()
                .collection("markets")
                .document(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .collection("OrderSheet")
                .document("sheet");
        CollectionReference optionRef = myOrderSheet.collection("Options");

        setupCardImageView(myOrderSheet);
        setupCardRecyclerView(optionRef);

        return root;
    }

    private void setupOrderSheetButton() {
        if (isExist) {
            btn.setVisibility(View.GONE);
            cardView.setVisibility(View.VISIBLE);
        }
        else {
            btn.setVisibility(View.VISIBLE);
            cardView.setVisibility(View.GONE);
        }
    }

    private void setupCardRecyclerView(CollectionReference optionRef) {
        Query query = optionRef.orderBy("number", Query.Direction.ASCENDING);
        FirestoreRecyclerOptions<Option> options = new FirestoreRecyclerOptions.Builder<Option>()
                .setQuery(query, Option.class)
                .build();
        adapter = new OptionAdapter(options, getContext());
        RecyclerView recyclerView = viewGroup.findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(adapter);
    }

    private void setupCardImageView(DocumentReference myOrderSheet) {
        ImageView imageView = viewGroup.findViewById(R.id.imageView);
        myOrderSheet.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        String imageName = (String) document.get("image");
                        if (imageName.substring(0, 12).equals("option_main/")) {
                            StorageReference ref = FirebaseStorage.getInstance().getReference(imageName);
                            GlideApp.with(getContext()).load(ref).into(imageView);
                        }
                    }
                    else
                        Log.d("CARD_MAIN_IMG", "No such document");
                }
                else {
                    Log.d("CARD_MAIN_IMG", "get failed with ", task.getException());
                }
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == 100 && data != null) {
                Log.d(TAG, "resultCode = RESULT_OK");
                isExist = data.getBooleanExtra("isExist", false);
                setupOrderSheetButton();
            }
        }
        else if (resultCode == Activity.RESULT_CANCELED) {
            Log.d(TAG, "resultCode = RESULT_CANCELED");
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        adapter.stopListening();
    }
}
