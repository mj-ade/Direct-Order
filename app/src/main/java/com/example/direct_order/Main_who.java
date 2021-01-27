package com.example.direct_order;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.direct_order.ui.login.Login_buy;
import com.example.direct_order.ui.login.Login_sell;

public class Main_who extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_who);

        Button btn1 = (Button)findViewById(R.id.Seller_login);
        Button btn2 = (Button)findViewById(R.id.Customer_login);

        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), Login_sell.class);
                startActivity(intent);
                finish();
            }
        });

        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), Login_buy.class);
                startActivity(intent);
                finish();
            }
        });
    }
}