package com.example.test;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.test.ui.login.Login_sell;


public class Join extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join);

        Button btn_register = (Button)findViewById(R.id.btn_register_sell);

        btn_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                Toast.makeText(getApplicationContext(),"회원가입 완료",Toast.LENGTH_LONG).show();
                Intent intent = new Intent(getApplicationContext(), Login_sell.class);
                startActivity(intent);

            }
        });
    }
}