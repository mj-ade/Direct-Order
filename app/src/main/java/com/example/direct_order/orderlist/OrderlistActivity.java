package com.example.direct_order.orderlist;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.direct_order.R;

import java.io.Serializable;
import java.util.ArrayList;

public class OrderlistActivity extends AppCompatActivity implements Serializable {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_orderlist);

        //주문목록 - 아이템 리스트
        itemArrayList = new ArrayList<>();
        itemArrayList.add(new item("jinju", "basic cake", "2021-01-21", "2021-02-02"));
        itemArrayList.add(new item("yeji", "heart cake", "2021-01-20", "2021-02-03"));
        itemArrayList.add(new item("minjae", "customized cake", "2021-01-22", "2021-02-01"));

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


    //주문목록
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    //주문목록 - 아이템 리스트
    private static ArrayList<item> itemArrayList;
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