package com.example.direct_order.review_list;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.direct_order.R;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class ReviewListActivity extends AppCompatActivity {
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private DocumentReference market;
    private CollectionReference reviewRef;

    private ReviewAdapter adapter;
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_review_list);
        market = db.collection("markets").document(getIntent().getStringExtra("shopuid"));
        reviewRef = market.collection("ReviewList");
        recyclerView = findViewById(R.id.recyclerView);
        setupRecyclerView();
    }

    private void setupRecyclerView() {
        Query query = reviewRef.orderBy("image", Query.Direction.DESCENDING);

        FirestoreRecyclerOptions<ReviewData> reviews = new FirestoreRecyclerOptions.Builder<ReviewData>()
                .setQuery(query, ReviewData.class)
                .build();

        adapter = new ReviewAdapter(reviews, this);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
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