package com.example.direct_order.register;

import android.content.DialogInterface;
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
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;

public class JoinCustomerActivity extends AppCompatActivity {
    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private long backKeyPressedTime = 0;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join_customer);

        Button btn_register = (Button) findViewById(R.id.btn_register_customer);
        EditText et_id = findViewById(R.id.et_id2);
        EditText et_pass = findViewById(R.id.et_pass2);
        EditText et_passcheck = findViewById(R.id.et_passcheck2);
        EditText et_name = findViewById(R.id.et_name2);
        EditText et_phone = findViewById(R.id.et_phone2);
        EditText et_add = findViewById(R.id.et_address2);

        btn_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(JoinCustomerActivity.this);
                builder.setTitle("").setMessage("가입하시겠습니까?").setNegativeButton("예", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        final String email = et_id.getText().toString().trim();
                        final String pwd = et_pass.getText().toString().trim();
                        final String pwdcheck = et_passcheck.getText().toString().trim();
                        final String name = et_name.getText().toString().trim().toUpperCase();
                        final String phone = et_phone.getText().toString().trim();
                        final String add = et_add.getText().toString().trim();

                        if (!email.equals("") && !pwd.equals("") && !name.equals("") && !phone.equals("") && !add.equals("")) {
                            if (!pwd.equals(pwdcheck)) {
                                AlertDialog.Builder builder2 = new AlertDialog.Builder(JoinCustomerActivity.this);
                                builder2.setMessage("비밀번호 불일치\n다시 입력해주세요.").setPositiveButton("확인", null).create().show();
                            }
                            else {
                                firebaseAuth.createUserWithEmailAndPassword(email, pwd).addOnCompleteListener(JoinCustomerActivity.this, new OnCompleteListener<AuthResult>() {
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
                                            hashMap.put("address",add);

                                            FirebaseFirestore db = FirebaseFirestore.getInstance();
                                            db.collection("customers").document(uid).set(hashMap)
                                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                        @Override
                                                        public void onSuccess(Void aVoid) {
                                                            Toast.makeText(getApplicationContext(), "회원가입 완료", Toast.LENGTH_LONG).show();
                                                        }
                                                    });
                                            finish();
                                        }
                                        else {
                                            if (pwd.length() < 6) {
                                                AlertDialog.Builder builder = new AlertDialog.Builder(JoinCustomerActivity.this);
                                                builder.setTitle("")
                                                        .setMessage("비밀번호를 6자리 이상 입력해주세요.")
                                                        .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                                                            @Override
                                                            public void onClick(DialogInterface dialogInterface, int i) {

                                                            }
                                                        });
                                                AlertDialog alertDialog = builder.create();
                                                alertDialog.show();
                                            }
                                            //아이디 중복 시
                                            else {
                                                AlertDialog.Builder builder = new AlertDialog.Builder(JoinCustomerActivity.this);
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
                            AlertDialog.Builder builder1 = new AlertDialog.Builder(JoinCustomerActivity.this);
                            builder1.setMessage("빠짐없이 작성하였는지 다시 확인해주세요:)").setPositiveButton("확인", null).create().show();
                        }
                    }
                });
                builder.setPositiveButton("아니오", null);
                builder.show();
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