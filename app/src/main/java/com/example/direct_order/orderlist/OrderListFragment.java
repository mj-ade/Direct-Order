package com.example.direct_order.orderlist;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.request.target.Target;
import com.example.direct_order.GlideApp;
import com.example.direct_order.R;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class OrderListFragment extends Fragment {
    private final Calendar mCalendar = Calendar.getInstance();
    private String today = new SimpleDateFormat("yyyy/MM/dd").format(System.currentTimeMillis());
    private String selectedDate = today;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
    private DocumentReference market = db.collection("markets").document(uid);
    private CollectionReference orderListRef = market.collection("OrderList");

    private OrderListAdapter mAdapter;
    private RecyclerView recyclerView;
    private Button button;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup viewGroup = (ViewGroup) inflater.inflate(R.layout.fragment_order_list, container, false);
        recyclerView = viewGroup.findViewById(R.id.recyclerView);
        Spinner spinner = viewGroup.findViewById(R.id.array_spinner);
        ArrayAdapter adapter = ArrayAdapter.createFromResource(getContext(), R.array.sort, android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    mAdapter.updateOptions(new FirestoreRecyclerOptions.Builder<Order>()
                            .setQuery(orderListRef.whereEqualTo("date", selectedDate).orderBy("time", Query.Direction.ASCENDING), Order.class)
                            .build());
                }
                else if (position == 1) {
                    mAdapter.updateOptions(new FirestoreRecyclerOptions.Builder<Order>()
                            .setQuery(orderListRef.whereEqualTo("pickup", selectedDate).orderBy("pickupTime", Query.Direction.ASCENDING), Order.class)
                            .build());
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        button = viewGroup.findViewById(R.id.button);
        button.setText(today);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePicker();
                spinner.setSelection(0);
            }
        });

        setupRecyclerView();
        return viewGroup;
    }

    private void setupRecyclerView() {
        Query query = orderListRef.whereEqualTo("date", today).orderBy("time", Query.Direction.ASCENDING);

        FirestoreRecyclerOptions<Order> orders = new FirestoreRecyclerOptions.Builder<Order>()
                .setQuery(query, Order.class)
                .build();

        mAdapter = new OrderListAdapter(orders, getContext());

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(mAdapter);

        mAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(DocumentSnapshot documentSnapshot, int position) {
                // 주문서 보여주기
                LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View myScrollView = inflater.inflate(R.layout.scroll_detail, null, false);

                ImageView iv = (ImageView) myScrollView.findViewById(R.id.orderdetail);

                documentSnapshot.getReference().addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                        if (value != null && value.exists()) {
                            //screenshot 가져오기
                            String screenshot = value.get("screenshot").toString().trim();

                            StorageReference ref = FirebaseStorage.getInstance().getReference(screenshot);
                            GlideApp.with(getContext())
                                    .load(ref)
                                    .override(Target.SIZE_ORIGINAL)
                                    .placeholder(R.drawable.white)
                                    .into(iv);
                        }
                    }
                });

                AlertDialog.Builder builder = new AlertDialog.Builder(getContext())
                        .setView(myScrollView)
                        .setTitle("<주문 상세 보기>")
                        .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });
                builder.create().show();
            }
        });
    }

    private void showDatePicker() {
        int todayYear = mCalendar.get(Calendar.YEAR);
        int todayMonth = mCalendar.get(Calendar.MONTH);
        int todayDate = mCalendar.get(Calendar.DATE);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                getContext(),
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        selectedDate = String.format("%02d/%02d/%02d", year, month+1, dayOfMonth);
                        button.setText(selectedDate);
                        mAdapter.updateOptions(new FirestoreRecyclerOptions.Builder<Order>()
                                .setQuery(orderListRef.whereEqualTo("date", selectedDate).orderBy("time", Query.Direction.ASCENDING), Order.class)
                                .build());
                    }
                },
                todayYear, todayMonth, todayDate);
        datePickerDialog.show();
    }

    @Override
    public void onStart() {
        super.onStart();
        mAdapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        mAdapter.stopListening();
    }
}