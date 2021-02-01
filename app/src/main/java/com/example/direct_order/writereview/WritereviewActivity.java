package com.example.direct_order.writereview;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.direct_order.R;

public class WritereviewActivity extends AppCompatActivity {
    EditText editText;
    Button button;
    RatingBar ratingBar;
    //TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_writereview);

        editText = (EditText)findViewById(R.id.editText);
        button = (Button)findViewById(R.id.button);
        ratingBar = findViewById(R.id.ratingBar);
        //textView = findViewById(R.id.editText);


        button.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                //Log.d("tag","masg");
                Toast.makeText(getApplicationContext(),"리뷰가 저장되었습니다", Toast.LENGTH_SHORT).show();
                //textView.setText(editText.getText()); //리뷰 저장되야함. 리뷰 보기에서 볼 수 있게
            }
        });
    }
}