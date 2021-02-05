package com.example.direct_order.register;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.direct_order.R;
import com.example.direct_order.ui.login.Login_buy;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;


public class Join_buy extends AppCompatActivity {
    private FirebaseAuth firebaseAuth;
    private EditText et_id, et_pass, et_passcheck, et_name, et_phone, et_add;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join_buy);
        firebaseAuth = FirebaseAuth.getInstance();
        Button btn_register = (Button) findViewById(R.id.btn_register_sell2);
        et_id = findViewById(R.id.et_id2);
        et_pass = findViewById(R.id.et_pass2);
        et_passcheck = findViewById(R.id.et_passcheck2);
        et_name = findViewById(R.id.et_name2);
        et_phone = findViewById(R.id.et_phone2);
        et_add = findViewById(R.id.et_address2);

        btn_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(Join_buy.this);
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
                                //final ProgressDialog mDialog = new ProgressDialog(Join.this);
                                AlertDialog.Builder builder2 = new AlertDialog.Builder(Join_buy.this);
                                builder2.setTitle("").setMessage("비밀번호 불일치\n다시 입력해주세요.").setPositiveButton("확인", null).create().show();
                            }
                            else{
                                firebaseAuth.createUserWithEmailAndPassword(email, pwd).addOnCompleteListener(Join_buy.this, new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {

                                        //가입 성공시
                                        if (task.isSuccessful()) {

                                            FirebaseUser user = firebaseAuth.getCurrentUser();
                                            String email = user.getEmail();
                                            String uid = user.getUid();

                                            //해쉬맵 테이블을 파이어베이스 데이터베이스에 저장
                                            HashMap<Object,String> hashMap = new HashMap<>();

                                            hashMap.put("uid",uid);
                                            hashMap.put("email",email);
                                            hashMap.put("name",name);
                                            hashMap.put("phone",phone);
                                            hashMap.put("address",add);

                                            FirebaseDatabase database = FirebaseDatabase.getInstance();
                                            DatabaseReference reference = database.getReference("customers");
                                            reference.child(uid).setValue(hashMap);
                                            FirebaseFirestore db = FirebaseFirestore.getInstance();

                                            db.collection("customers").document(uid).set(hashMap)
                                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                        @Override
                                                        public void onSuccess(Void aVoid) {
                                                            //Log.d(TAG, "DocumentSnapshot successfully written!");
                                                        }
                                                    })
                                                    .addOnFailureListener(new OnFailureListener() {
                                                        @Override
                                                        public void onFailure(@NonNull Exception e) {
                                                            //Log.w(TAG, "Error writing document", e);
                                                        }
                                                    });

                                            finish();
                                            Toast.makeText(getApplicationContext(), "회원가입 완료", Toast.LENGTH_LONG).show();
                                            Intent intent = new Intent(getApplicationContext(), Login_buy.class);
                                            startActivity(intent);


                                            //가입이 이루어져을시 가입 화면을 빠져나감.

                                            startActivity(intent);

                                            //finish();
                                            //Toast.makeText(Join.this, "회원가입에 성공하셨습니다.", Toast.LENGTH_SHORT).show();

                                        } else {
                                            //아이디 중복 시
                                            android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(Join_buy.this);
                                            builder.setTitle("").setMessage("사용할 수 없는 아이디입니다.").setPositiveButton("확인", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialogInterface, int i) {

                                                }
                                            });
                                            android.app.AlertDialog alertDialog = builder.create();
                                            alertDialog.show();

                                            return;  //해당 메소드 진행을 멈추고 빠져나감.

                                        }

                                    }
                                });

                            }
                        } else {
                            AlertDialog.Builder builder1 = new AlertDialog.Builder(Join_buy.this);
                            builder1.setTitle("").setMessage("빠짐없이 작성하였는지 다시 확인해주세요:)").setPositiveButton("확인", null).create().show();
                        }


                    }
                });
                builder.setPositiveButton("아니오", null);
                builder.show();
            }
        });
    }
}