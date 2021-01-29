package com.example.direct_order.orderlist;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.direct_order.R;

public class OrderDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.order_detail);

        TextView printname = findViewById(R.id.ordername);
        TextView printitem = findViewById(R.id.orderitem);
        TextView printdate = findViewById(R.id.orderdate);
        TextView printpickup = findViewById(R.id.orderpickup);

        Intent detailIntent = getIntent();
        String dname = detailIntent.getStringExtra("name");
        String ditem = detailIntent.getStringExtra("item_name");
        String ddate = detailIntent.getStringExtra("date");
        String dpickup = detailIntent.getStringExtra("pickup");

        printname.setText("이름 : "+dname);
        printitem.setText("주문 상품 : "+ditem+ "\n옵션 : ~~~");
        printdate.setText("주문날짜 : "+ddate);
        printpickup.setText("픽업일시 : "+dpickup);
    }

}