package com.example.direct_order.ui.orderhistory;

import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import com.google.firebase.firestore.FirebaseFirestore;
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
    int num=0;
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
        /*docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        HashMap<Object, String> hashMap = new HashMap<>();
                        String s = document.getData().toString();
                        hashMap.put("",s);

                        myList.add(hashMap);
                        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                        mAdapter = new MyListAdapter(myList);

                        mRecyclerView.setAdapter(mAdapter);

                    }
                }
            }
        });
*/

        coRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                               @Override
                                               public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                   if (task.isSuccessful()) {
                                                       for (QueryDocumentSnapshot document : task.getResult()) {

                                                           String keys = document.getData().keySet().toString();
                                                           String values = document.getData().values().toString();
                                                           int size = document.getData().size();

                                                           String key[] =keys.split(",");
                                                           String value[]=values.split(",");
                                                           String s = " ";

                                                           for(int i=0;i<size;i++){
                                                               key[i]=key[i].replace("[","");
                                                               key[i]=key[i].replace("]","");
                                                               if(key[i].trim().equals("shopname"))
                                                                    s = s + key[i]+": "+value[i];
                                                               else if(key[i].trim().equals("day")||key[i].trim().equals("goods"))
                                                                   s = s + "\n" + key[i]+": "+value[i];
                                                               else
                                                                   continue;

                                                               s = s.replace("[","");
                                                               s = s.replace("]","");
                                                           }

                                                           myList.add(s);
                                                           mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                                                           mAdapter = new MyListAdapter(myList);

                                                           mRecyclerView.setAdapter(mAdapter);

                                                       }
                                                   }
                                               }
                                           });

        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mAdapter = new MyListAdapter(myList);

        mRecyclerView.setAdapter(mAdapter);

        return root;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);


    }

}