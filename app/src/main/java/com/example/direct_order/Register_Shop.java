package com.example.direct_order;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.direct_order.R;
import com.example.direct_order.ui.home.HomeFragment;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;



public class Register_Shop extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register__shop);

        Button btn_reg = findViewById(R.id.btn_add);

        //listDataGroup.add(getString(R.string.show_shop));


        btn_reg.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                EditText name = findViewById(R.id.txt_shopname);
                String str = name.getText().toString();
                //Toast.makeText(getApplicationContext(),(MainForSeller.listDataChild).get(0).toString(),Toast.LENGTH_LONG).show();
                //Toast.makeText(getApplicationContext(),MainForSeller.shopList.toString(),Toast.LENGTH_LONG).show();
                showDialog(str);

            }
        });

    }

    void showDialog(String str){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("마켓을 등록 하시겠습니까?");
        builder.setNegativeButton("예(Yes)", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();

                //Toast.makeText(getApplicationContext(),list.toString(),Toast.LENGTH_LONG).show();
                //MainForSeller.listDataChild.put(listDataGroup.get(0), shopList);

                // notify the adapter

                //Toast.makeText(Register_Shop.this, "마켓 등록 완료", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getApplicationContext(), MainForSeller.class);
                intent.putExtra("마켓명",str);

                HomeFragment fragment = new HomeFragment();
                   Bundle bundle = new Bundle();
                   bundle.putString("마켓",str);
                   fragment.setArguments(bundle);

                FragmentManager fm = getSupportFragmentManager();
                FragmentTransaction ft = fm.beginTransaction();
                ft.add(R.id.fragment_test,fragment);
                ft.commit();


                startActivity(intent);
            }
        });

        builder.setPositiveButton("아니오(No)", null);
        builder.show();
    }


}