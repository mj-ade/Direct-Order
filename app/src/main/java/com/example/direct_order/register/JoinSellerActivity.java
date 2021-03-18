package com.example.direct_order.register;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.direct_order.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.HashMap;

public class JoinSellerActivity extends AppCompatActivity {
    private FirebaseAuth firebaseAuth =  FirebaseAuth.getInstance();
    private long backKeyPressedTime = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join_seller);

        Button btn_register = findViewById(R.id.btn_register_market);
        EditText et_id = findViewById(R.id.et_id);
        EditText et_pass = findViewById(R.id.et_pass);
        EditText et_passcheck = findViewById(R.id.et_passcheck);
        EditText et_name = findViewById(R.id.et_name);
        EditText et_phone = findViewById(R.id.et_phone);

        btn_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String email = et_id.getText().toString().trim();
                final String pwd = et_pass.getText().toString().trim();
                final String pwdcheck = et_passcheck.getText().toString().trim();
                final String name = et_name.getText().toString().trim().toUpperCase();
                final String phone = et_phone.getText().toString().trim();

                if (!email.equals("") && !pwd.equals("")  && !phone.equals("") && !name.equals("")) {
                    if (!pwd.equals(pwdcheck)) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(JoinSellerActivity.this);
                        builder.setMessage("비밀번호 불일치\n다시 입력해주세요.").setPositiveButton("확인", null);
                        AlertDialog alertDialog = builder.create();
                        alertDialog.show();
                    }
                    else {
                        firebaseAuth.createUserWithEmailAndPassword(email, pwd).addOnCompleteListener(JoinSellerActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    FirebaseUser user = firebaseAuth.getCurrentUser();
                                    String email = user.getEmail();
                                    String uid = user.getUid();

                                    HashMap<Object,String> hashMap = new HashMap<>();
                                    hashMap.put("uid",uid);
                                    hashMap.put("email",email);
                                    hashMap.put("name",name);
                                    hashMap.put("phone",phone);

                                    Intent intent = new Intent(getApplicationContext(), RegisterShopActivity.class);
                                    intent.putExtra("회원가입",hashMap);
                                    startActivity(intent);
                                    finish();
                                }
                                else {
                                    if (pwd.length() < 6) {
                                        AlertDialog.Builder builder = new AlertDialog.Builder(JoinSellerActivity.this);
                                        builder.setMessage("비밀번호를 6자리 이상 입력해주세요.").setPositiveButton("확인", null);
                                        AlertDialog alertDialog = builder.create();
                                        alertDialog.show();
                                    }
                                    else {
                                        AlertDialog.Builder builder = new AlertDialog.Builder(JoinSellerActivity.this);
                                        builder.setMessage("사용할 수 없는 아이디입니다.").setPositiveButton("확인", null);
                                        AlertDialog alertDialog = builder.create();
                                        alertDialog.show();
                                    }
                                    return;
                                }
                            }
                        });
                    }
                }
                else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(JoinSellerActivity.this);
                    builder.setMessage("빠짐없이 작성하였는지 다시 확인해주세요:)").setPositiveButton("확인", null);
                    AlertDialog alertDialog = builder.create();
                    alertDialog.show();
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (System.currentTimeMillis() > backKeyPressedTime + 2500) {
            backKeyPressedTime = System.currentTimeMillis();
            Toast.makeText(this, "뒤로 가기 버튼을 한 번 더 누르면 로그인 창으로 이동합니다.", Toast.LENGTH_SHORT).show();
            return;
        }
        else {
            FirebaseUser user = firebaseAuth.getCurrentUser();
            user.delete();
            Toast.makeText(this,"회원가입 취소",Toast.LENGTH_SHORT).show();
            finish();
        }
    }
}