package com.example.direct_order;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.direct_order.ui.login.Login_sell1;

public class Join extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join);

        Button btn_register = (Button)findViewById(R.id.btn_register);

        btn_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(),"회원가입 완료",Toast.LENGTH_LONG).show();
                Intent intent = new Intent(getApplicationContext(), Login_sell1.class);
                startActivity(intent);
            }
        });
    }
}