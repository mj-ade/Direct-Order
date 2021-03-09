package com.example.direct_order.register;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.direct_order.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.HashMap;

public class Join extends AppCompatActivity {
    public static Activity activity;
    private FirebaseAuth firebaseAuth;
    private long backKeyPressedTime = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join);
        activity=Join.this;
        Button btn_register = (Button)findViewById(R.id.btn_register_sell);
        EditText et_id = findViewById(R.id.et_id);
        EditText et_pass = findViewById(R.id.et_pass);
        EditText et_passcheck = findViewById(R.id.et_passcheck);
        EditText et_name = findViewById(R.id.et_name);
        EditText et_phone = findViewById(R.id.et_phone);
        //Button validateButton = findViewById(R.id.validateButton);

        //파이어베이스 접근 설정
        // user = firebaseAuth.getCurrentUser();
        firebaseAuth =  FirebaseAuth.getInstance();
        //firebaseDatabase = FirebaseDatabase.getInstance().getReference()

        /*validateButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                //id 중복확인
                final String email = et_id.getText().toString().trim();
                final String pwd = et_pass.getText().toString().trim();



            }
        });
*/

        btn_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(getApplicationContext(),"회원가입 완료",Toast.LENGTH_LONG).show();
                final String email = et_id.getText().toString().trim();
                final String pwd = et_pass.getText().toString().trim();
                final String pwdcheck = et_passcheck.getText().toString().trim();
                final String name = et_name.getText().toString().trim().toUpperCase();
                final String phone = et_phone.getText().toString().trim();


                if (!email.equals("")&& !pwd.equals("")  && !phone.equals("") && !name.equals("")) {

                    if (!pwd.equals(pwdcheck)) {
                        //final ProgressDialog mDialog = new ProgressDialog(Join.this);
                        AlertDialog.Builder builder = new AlertDialog.Builder(Join.this);
                        builder.setTitle("").setMessage("비밀번호 불일치\n다시 입력해주세요.").setPositiveButton("확인", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                            }
                        });
                        AlertDialog alertDialog = builder.create();
                        alertDialog.show();
                    }
                    else {
                        firebaseAuth.createUserWithEmailAndPassword(email, pwd).addOnCompleteListener(Join.this, new OnCompleteListener<AuthResult>() {
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

                                    Intent intent = new Intent(getApplicationContext(), RegisterShopActivity.class);
                                    intent.putExtra("회원가입",hashMap);



                                    /*FirebaseDatabase database = FirebaseDatabase.getInstance();
                                    DatabaseReference reference = database.getReference("Users");
                                    reference.child(uid).setValue(hashMap);*/


                                    //가입이 이루어져을시 가입 화면을 빠져나감.

                                    startActivity(intent);

                                    //finish();
                                    //Toast.makeText(Join.this, "회원가입에 성공하셨습니다.", Toast.LENGTH_SHORT).show();

                                } else {
                                    //패스워드가 5자리 이하일때
                                    if(pwd.length()<6){
                                        AlertDialog.Builder builder = new AlertDialog.Builder(Join.this);
                                        builder.setTitle("").setMessage("비밀번호를 6자리 이상 입력해주세요.").setPositiveButton("확인", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {

                                            }
                                        });
                                        AlertDialog alertDialog = builder.create();
                                        alertDialog.show();

                                    }
                                    //아이디 중복 시
                                    else {
                                        AlertDialog.Builder builder = new AlertDialog.Builder(Join.this);
                                        builder.setTitle("").setMessage("사용할 수 없는 아이디입니다.").setPositiveButton("확인", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {

                                            }
                                        });
                                        AlertDialog alertDialog = builder.create();
                                        alertDialog.show();
                                    }
                                    return;  //해당 메소드 진행을 멈추고 빠져나감.

                                }

                            }
                        });

                    }
                }

                else{
                    AlertDialog.Builder builder = new AlertDialog.Builder(Join.this);
                    builder.setTitle("").setMessage("빠짐없이 작성하였는지 다시 확인해주세요:)").setPositiveButton("확인", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                        }
                    });
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
            Toast.makeText(this, "뒤로 가기 버튼을 한 번 더 누르시면 종료됩니다.", Toast.LENGTH_LONG).show();

            return;
        }
        if (System.currentTimeMillis() <= backKeyPressedTime + 2500) {
            FirebaseUser user = firebaseAuth.getCurrentUser();
            user.delete();
            finish();
            Toast.makeText(this,"회원가입 취소",Toast.LENGTH_LONG).show();

        }
    }
}