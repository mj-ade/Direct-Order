package com.example.direct_order.setting;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.text.method.KeyListener;
import android.text.method.PasswordTransformationMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.example.direct_order.MainActivity;
import com.example.direct_order.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

public class SettingSellerFragment extends Fragment {
    private EditText tv1, tv3;
    private TextView tv2;
    private Button btn_edit, btn_sec;
    private KeyListener variable1, variable3;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_setting_seller, container, false);

        tv1 = root.findViewById(R.id.user_name);
        tv2 = root.findViewById(R.id.user_email);
        tv3 = root.findViewById(R.id.user_phone);

        variable1 = tv1.getKeyListener();
        variable3 = tv3.getKeyListener();

        tv1.setKeyListener(null);
        tv3.setKeyListener(null);

        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String uid = user.getUid();

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference docRef = db.collection("sellers").document(uid);
        docRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                if (value != null && value.exists()) {
                    String name = value.get("name").toString().trim();
                    String email = value.get("email").toString().trim();
                    String phone = value.get("phone").toString().trim();
                    tv1.setText(name);
                    tv2.setText(email);
                    tv3.setText(phone);
                }
            }
        });

        btn_edit = root.findViewById(R.id.edit);
        btn_edit.setText("수정");
        btn_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (btn_edit.getText().toString().equals("수정")) {
                    //사용자 계정 정보 수정
                    //비밀번호 확인 기능
                    EditText checkpass = new EditText(getActivity());
                    checkpass.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    checkpass.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    AlertDialog.Builder check = new AlertDialog.Builder(getActivity());
                    check.setTitle("비밀번호 확인").setMessage("비밀번호를 입력하세요.");
                    check.setView(checkpass);
                    check.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    if (task.isSuccessful()) {
                                        DocumentSnapshot document = task.getResult();
                                        if (document.exists()) {
                                            String email = document.get("email").toString();
                                            String pwd = checkpass.getText().toString().trim();
                                            if (pwd.equals(""))
                                                Toast.makeText(getActivity(), "비밀번호가 일치하지 않습니다.", Toast.LENGTH_SHORT).show();
                                            else {
                                                firebaseAuth.signInWithEmailAndPassword(email, pwd)
                                                        .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<AuthResult> task) {
                                                                if (task.isSuccessful()) {
                                                                    //비밀번호 인증 성공 시
                                                                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                                                                    String uid = user.getUid();
                                                                    FirebaseFirestore db = FirebaseFirestore.getInstance();
                                                                    DocumentReference docRef = db.collection("sellers").document(uid);
                                                                    docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                                        @Override
                                                                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                                            if (task.isSuccessful()) {
                                                                                DocumentSnapshot document = task.getResult();
                                                                                if (document.exists()) {
                                                                                    //이메일은 변경 불가
                                                                                    tv1.setKeyListener(variable1);
                                                                                    tv3.setKeyListener(variable3);
                                                                                    tv1.onEditorAction(EditorInfo.IME_ACTION_DONE);
                                                                                    tv3.onEditorAction(EditorInfo.IME_ACTION_DONE);
                                                                                    btn_edit.setText("완료");
                                                                                }
                                                                            }
                                                                        }
                                                                    });
                                                                }
                                                                else
                                                                    Toast.makeText(getActivity(), "비밀번호가 일치하지 않습니다.", Toast.LENGTH_SHORT).show(); //비밀번호 다를 때
                                                            }
                                                        });
                                            }
                                        }
                                    }
                                }
                            });
                        }
                    });
                    check.create().show();
                }
                else if (btn_edit.getText().toString().equals("완료")) {
                    android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(getActivity());
                    builder.setTitle("계정 정보 변경").setMessage("변경하시겠습니까?").setNegativeButton("예", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            String name = tv1.getText().toString().toUpperCase();
                            String phone = tv3.getText().toString();
                            //edit 불가하게 변경
                            tv1.setKeyListener(null);
                            tv3.setKeyListener(null);
                            //keyboard 사라짐
                            tv1.onEditorAction(EditorInfo.IME_ACTION_DONE);
                            tv3.onEditorAction(EditorInfo.IME_ACTION_DONE);
                            //db에 정보 update
                            docRef.update("name", name);
                            docRef.update("phone", phone);
                            //button text 변경
                            btn_edit.setText("수정");
                        }
                    }).setPositiveButton("아니오", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            //변경 전 정보로 다시 돌아감
                            tv1.setKeyListener(null);
                            tv3.setKeyListener(null);
                            tv1.onEditorAction(EditorInfo.IME_ACTION_DONE);
                            tv3.onEditorAction(EditorInfo.IME_ACTION_DONE);
                            docRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
                                @Override
                                public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                                    if (value != null && value.exists()) {
                                        String name = value.get("name").toString().trim();
                                        String phone = value.get("phone").toString().trim();
                                        tv1.setText(name);
                                        tv3.setText(phone);
                                    }
                                }
                            });
                        }
                    }).show();
                }
            }
        });

        tv2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (btn_edit.getText().toString().equals("완료"))
                    Toast.makeText(getActivity(), "이메일은 변경이 불가능합니다.", Toast.LENGTH_SHORT).show();
            }
        });


        btn_sec = root.findViewById(R.id.secession);
        btn_sec.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder1 = new AlertDialog.Builder(getActivity());
                builder1.setTitle("탈퇴하기").setMessage("탈퇴하시겠습니까?").setNegativeButton("예", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //비밀번호 인증
                        EditText checkpass = new EditText(getActivity());
                        checkpass.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                        checkpass.setTransformationMethod(PasswordTransformationMethod.getInstance());
                        AlertDialog.Builder check = new AlertDialog.Builder(getActivity());
                        check.setTitle("비밀번호 확인").setMessage("비밀번호를 입력하세요.");
                        check.setView(checkpass);
                        check.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                        if (task.isSuccessful()) {
                                            DocumentSnapshot document = task.getResult();
                                            if (document.exists()) {
                                                String email = document.get("email").toString();
                                                String pwd = checkpass.getText().toString().trim();
                                                if (pwd.equals(""))
                                                    Toast.makeText(getActivity(), "비밀번호가 일치하지 않습니다.", Toast.LENGTH_SHORT).show();
                                                else {
                                                    firebaseAuth.signInWithEmailAndPassword(email, pwd)
                                                            .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                                                                @Override
                                                                public void onComplete(@NonNull Task<AuthResult> task) {
                                                                    if (task.isSuccessful()) {
                                                                        //비밀번호 인증 성공 시
                                                                        Toast.makeText(getActivity(), "탈퇴가 완료되었습니다.\n곧 시작 화면으로 돌아갑니다.", Toast.LENGTH_SHORT).show();
                                                                        DocumentReference docRef2 = db.collection("markets").document(uid);
                                                                        user.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                            @Override
                                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                                if (task.isSuccessful()) {
                                                                                    docRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                                        @Override
                                                                                        public void onSuccess(Void aVoid) {
                                                                                            docRef2.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                                                @Override
                                                                                                public void onSuccess(Void aVoid) {
                                                                                                    FirebaseAuth.getInstance().signOut();    //로그아웃처리
                                                                                                    //사용자 로그인 선택 화면으로 이동
                                                                                                    Intent intent = new Intent(getActivity(), MainActivity.class);
                                                                                                    startActivity(intent);
                                                                                                    //현 fragment 종료
                                                                                                    FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                                                                                                    fragmentManager.beginTransaction().remove(SettingSellerFragment.this).commit();
                                                                                                }
                                                                                            });
                                                                                        }
                                                                                    });
                                                                                }
                                                                            }
                                                                        });
                                                                    }
                                                                    else
                                                                        Toast.makeText(getActivity(), "비밀번호가 일치하지않습니다.", Toast.LENGTH_SHORT).show(); //비밀번호 다를 때
                                                                }
                                                            });
                                                }
                                            }
                                        }
                                    }
                                });
                            }
                        }).create().show();
                    }
                }).setPositiveButton("아니요", null).create().show();
            }
        });

        return root;
    }
}
