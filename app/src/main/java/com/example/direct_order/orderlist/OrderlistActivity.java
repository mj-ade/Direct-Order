package com.example.direct_order.orderlist;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.direct_order.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class OrderlistActivity extends AppCompatActivity implements Serializable {

    public int[] count;

    //주문목록
    private RecyclerView mRecyclerView;
    private OrderlistAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    //주문목록 - 아이템 리스트
    private static ArrayList<item> itemArrayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_orderlist);


        //Access a Cloud Firestore instance from Activity
        final FirebaseFirestore db = FirebaseFirestore.getInstance();

        /*Map<String, Integer> sellers = new HashMap<>();
        sellers.put("process", 1);
        db.collection("sellers").add(sellers).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(DocumentReference documentReference) {
                //데이터가 성공적으로 추가되었을때
                Log.d("tag", "DocumentSnapshot added wit ID: " + documentReference.getId());
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                //에러발생시
                Log.w("tag", "Error",e);
            }
        });*/



        //주문목록 - 아이템 리스트
        itemArrayList = new ArrayList<>();
        itemArrayList.add(new item("jinju", "basic cake", "2021-01-21", "2021-02-02"));
        itemArrayList.add(new item("yeji", "heart cake", "2021-01-20", "2021-02-03"));
        itemArrayList.add(new item("minjae", "customized cake", "2021-01-22", "2021-02-01"));

        count = new int[itemArrayList.size()];

        Map<String, Object> order = new HashMap<>();


        for (int i=0; i<itemArrayList.size(); i++){
            order.put("name", itemArrayList.get(i).name);
            order.put("item", itemArrayList.get(i).itemname);
            order.put("date", itemArrayList.get(i).date);
            order.put("pikup", itemArrayList.get(i).pickup);
            order.put("process", 0);

            db.collection("markets").document("Qu3tzy23GfMcBsBQjI5pw7x3AM12").collection("orderlist").document(String.valueOf(i)).set(order).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Log.d("OrderlistActivity", "DocumentSnapshot successfully written!");
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.w("OrderlistActivity", "Error writing document",e);
                }
            });
        }


        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        //mRecyclerView.setHasFixedSize(true);//옵션
        //Linear layout manager 사용
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        //어답터 세팅
        mAdapter = new OrderlistAdapter(itemArrayList); //스트링 배열 데이터 인자로
        mRecyclerView.setAdapter(mAdapter);



        //스피너 주문순픽업순 정렬
        Spinner spinner = (Spinner)findViewById(R.id.array_spinner);
        ArrayAdapter adapter = ArrayAdapter.createFromResource(this,R.array.sort, android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(getApplicationContext(), Integer.toString(position), Toast.LENGTH_SHORT).show();
                if(position==0){
                    //주문날짜순으로 정렬

                }else if(position==1){
                    //픽업날짜순으로 정렬
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) { }
        });
    }


    //캘린더에서 날짜 고르기
    public void showDatePicker(View view) {
        DialogFragment newFragment = new DatePickerFragment();
        newFragment.show(getSupportFragmentManager(),"datePicker");
    }
    public void processDatePickerResult(int year, int month, int day){
        String month_string = Integer.toString(month+1);
        String day_string = Integer.toString(day);
        String year_string = Integer.toString(year);

        //year, month, day에 해당하는 주문data 보여주기
    }


    //주문목록 - 아이템 클라스
    public static class item{
        String name;
        String itemname;
        String date;
        String pickup;

        public item(String name, String itemname, String date, String pickup) {
            this.name = name;
            this.itemname = itemname;
            this.date = date;
            this.pickup = pickup;
        }

        public String getName() {
            return name;
        }

        public String getItemname() {
            return itemname;
        }

        public String getDate() {
            return date;
        }

        public String getPickup() {
            return pickup;
        }

    }

}