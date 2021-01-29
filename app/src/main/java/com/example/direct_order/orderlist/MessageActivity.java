package com.example.direct_order.orderlist;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.direct_order.R;

public class MessageActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.input_message);

        TextView toname = findViewById(R.id.toname);


        Intent messageIntent = getIntent();
        String mname = messageIntent.getStringExtra("name");

        toname.setText("받는 사람 : "+mname);
    }

    public void Message(View v){

        //메시지 전송기능 넣기//
        Toast.makeText(getApplicationContext(), "메시지가 전송되었습니다.", Toast.LENGTH_SHORT).show();
        finish();
    }
}