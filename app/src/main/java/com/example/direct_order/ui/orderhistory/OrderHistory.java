package com.example.direct_order.ui.orderhistory;

import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.direct_order.R;
import com.google.android.gms.tasks.OnCompleteListener;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class OrderHistory extends Fragment {

    public static OrderHistory newInstance() {
        return new OrderHistory();
    }
    List<String> myList = new ArrayList<>();
    private MyListAdapter mAdapter;
    RecyclerView mRecyclerView;
    String s;
    int size;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.order_history_fragment, container, false);
        mRecyclerView = root.findViewById(R.id.rv2);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(root.getContext(), 1));
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String uid = user.getUid();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        //DocumentReference docRef = db.collection("customers").document(uid);
        CollectionReference coRef = db.collection("customers").document(uid).collection("orders");


        coRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                size = task.getResult().size();

                for (int i = 0; i < size; i++) {
                    coRef.document(String.valueOf(i)).addSnapshotListener(new EventListener<DocumentSnapshot>() {
                        @Override
                        public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                            if (value != null && value.exists()) {

                                String shopuid = value.get("shopuid").toString().trim();
                                String pickup;
                                if (!value.get("pickup").toString().trim().equals(""))
                                    pickup = value.get("pickup").toString().trim() + " " + value.get("pickupTime").toString().trim();
                                else
                                    pickup = value.get("date").toString().trim();

                                s = "";

                                db.collection("markets").document(shopuid).addSnapshotListener(new EventListener<DocumentSnapshot>() {
                                    @Override
                                    public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                                        if(value!=null&&value.exists()){

                                            String shopname = value.get("shopname").toString().trim();
                                            String shopgood = value.get("shopgoods").toString().trim();
                                            String shopnum = value.get("shopnum").toString().trim();

                                            s = "마켓명: " + shopname + "\n주문 상품: " + shopgood + "\n마켓 전화번호: " + shopnum + "\n픽업일: " + pickup;

                                            Log.d("test: ",s);
                                            myList.add(s);
                                            mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                                            mAdapter = new MyListAdapter(myList);

                                            mRecyclerView.setAdapter(mAdapter);
                                        }
                                    }
                                });

                                    }
                                }

                            });
                        }
                    }
                });




        return root;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);


    }

}