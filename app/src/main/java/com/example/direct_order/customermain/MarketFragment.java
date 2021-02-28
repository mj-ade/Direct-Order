package com.example.direct_order.customermain;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.direct_order.R;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class MarketFragment extends Fragment {
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference market = db.collection("markets");
    private String[] goods = {"케이크", "케이스", "악세사리", "ETC"};

    private int position;
    private MarketAdapter adapter;
    private RecyclerView recyclerView;
    private double latitude, longitude;

    CollectionReference customerFavorRef;

    public MarketFragment() {

    }

    public MarketFragment(int position, CollectionReference customerFavorRef, double latitude, double longitude) {
        this.position = position;
        this.customerFavorRef = customerFavorRef;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_market, container, false);

        recyclerView = root.findViewById(R.id.recyclerView);



        setupRecyclerView();
        CustomerMainFragment.customVariable.setOnValueChangeListener(new CustomVariable.OnValueChangeListener() {
            @Override
            public void onValueChange(double latitude, double longitude) {
                Toast.makeText(getContext(), "update"+latitude+" "+longitude, Toast.LENGTH_SHORT).show();
                Query query = market//.whereEqualTo("shopgoods", goods[position])
                        .whereEqualTo("latitude", latitude);
                       // .whereEqualTo("longitude", longitude);
                adapter.updateOptions(new FirestoreRecyclerOptions.Builder<Market>()
                        .setQuery(query, Market.class)
                        .build());
            }
        });
        return root;
    }

    private void setupRecyclerView() {
        Query query = market.whereEqualTo("shopgoods", goods[position]);
        FirestoreRecyclerOptions<Market> datas = new FirestoreRecyclerOptions.Builder<Market>()
                .setQuery(query, Market.class)
                .build();

        adapter = new MarketAdapter(datas, customerFavorRef, getContext());

        recyclerView.setHasFixedSize(true);
        GridLayoutManager myLayoutManager = new GridLayoutManager(getContext(), 2);
        recyclerView.setLayoutManager(myLayoutManager);
        recyclerView.setAdapter(adapter);
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