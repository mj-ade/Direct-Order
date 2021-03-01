package com.example.direct_order.customermain;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

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
    Spinner spinner;
    CollectionReference customerFavorRef;

    public MarketFragment() {

    }

    public MarketFragment(int position, CollectionReference customerFavorRef) {
        this.position = position;
        this.customerFavorRef = customerFavorRef;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_market, container, false);

        recyclerView = root.findViewById(R.id.recyclerView);
        spinner = root.findViewById(R.id.spinner);
        ArrayAdapter arrayAdapter = ArrayAdapter.createFromResource(getContext(), R.array.market_sort, android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(arrayAdapter);
        spinner.setSelection(0);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int p, long id) {
                if (p == 0) {
                    adapter.updateOptions(new FirestoreRecyclerOptions.Builder<Market>()
                            .setQuery(market.whereEqualTo("shopgoods", goods[position]), Market.class)
                            .build());
                }
                else if (p == 1) {
                    adapter.updateOptions(new FirestoreRecyclerOptions.Builder<Market>()
                            .setQuery(market.whereEqualTo("shopgoods", goods[position])
                                    .whereEqualTo("latitude", CustomerMainFragment.customVariable.getLatitude())
                                    .whereEqualTo("longitude", CustomerMainFragment.customVariable.getLongitude()), Market.class)
                            .build());
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        setupRecyclerView();
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
        spinner.setSelection(0);
    }

    @Override
    public void onStop() {
        super.onStop();
        adapter.stopListening();
    }
}