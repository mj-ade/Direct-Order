package com.example.direct_order.ui.settings;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.method.KeyListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.direct_order.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class MarketSettingFragment extends Fragment {

    private EditText tv1, tv2, tv3, tv4, tv5;
    private TextView tv6;
    private Button btn_edit;
    private KeyListener k1, k2, k3, k4, k5;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_market_setting, container, false);


        tv1=root.findViewById(R.id.shop_name);
        tv2=root.findViewById(R.id.shop_num);
        tv3=root.findViewById(R.id.shop_insta);
        tv4=root.findViewById(R.id.shop_acc);
        tv5=root.findViewById(R.id.shop_add);
        tv6=root.findViewById(R.id.shop_good);

        k1 = tv1.getKeyListener();
        k2 = tv2.getKeyListener();
        k3 = tv3.getKeyListener();
        k4 = tv4.getKeyListener();
        k5 = tv5.getKeyListener();
        //판매 상품 종류는 변경 불가

        tv1.setKeyListener(null);
        tv2.setKeyListener(null);
        tv3.setKeyListener(null);
        tv4.setKeyListener(null);
        tv5.setKeyListener(null);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String uid = user.getUid();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference docRef = db.collection("markets").document(uid);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        String name = document.get("shopname").toString();
                        String num = document.get("shopnum").toString();
                        String insta = document.get("instagram").toString();
                        String acc = document.get("shopaccount").toString();
                        String add = document.get("shopaddress").toString();
                        String good = document.get("shopgoods").toString();

                        tv1.setText(name);
                        tv2.setText(num);
                        tv3.setText(insta);
                        tv4.setText(acc);
                        tv5.setText(add);
                        tv6.setText(good);


                    }
                }
            }
        });

        btn_edit=root.findViewById(R.id.edit_shop);
        btn_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //마켓 정보 수정
                //상품 종류는 변경 불가
                if (btn_edit.getText().toString().equals("수정")) {
                    tv1.setKeyListener(k1);
                    tv1.onEditorAction(EditorInfo.IME_ACTION_DONE);
                    tv2.setKeyListener(k2);
                    tv2.onEditorAction(EditorInfo.IME_ACTION_DONE);
                    tv3.setKeyListener(k3);
                    tv3.onEditorAction(EditorInfo.IME_ACTION_DONE);
                    tv4.setKeyListener(k4);
                    tv4.onEditorAction(EditorInfo.IME_ACTION_DONE);
                    tv5.setKeyListener(k5);
                    tv5.onEditorAction(EditorInfo.IME_ACTION_DONE);


                    btn_edit.setText("완료");
                }
                else{
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setTitle("마켓 정보 변경").setMessage("변경하시겠습니까?").setNegativeButton("예", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    String name = tv1.getText().toString();
                                    String num = tv2.getText().toString();
                                    String insta = tv3.getText().toString();
                                    String acc = tv4.getText().toString();
                                    String add = tv5.getText().toString();

                                    tv1.setKeyListener(null);
                                    tv1.onEditorAction(EditorInfo.IME_ACTION_DONE);
                                    tv2.setKeyListener(null);
                                    tv2.onEditorAction(EditorInfo.IME_ACTION_DONE);
                                    tv3.setKeyListener(null);
                                    tv3.onEditorAction(EditorInfo.IME_ACTION_DONE);
                                    tv4.setKeyListener(null);
                                    tv4.onEditorAction(EditorInfo.IME_ACTION_DONE);
                                    tv5.setKeyListener(null);
                                    tv5.onEditorAction(EditorInfo.IME_ACTION_DONE);

                                    docRef.update("shopname",name);
                                    docRef.update("shopnum",num);
                                    docRef.update("instagram",insta);
                                    docRef.update("shopaccount",acc);
                                    docRef.update("shopaddress",add);
                                    
                                    btn_edit.setText("수정");
                                }
                            });
                    builder.setPositiveButton("아니오",null);
                    builder.show();

                    
                    btn_edit.setText("수정");
                }
            }
        });

        tv6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(btn_edit.getText().toString().equals("완료"))
                    Toast.makeText(getActivity(),"판매 상품 종류는 변경이 불가능합니다.",Toast.LENGTH_LONG).show();
            }
        });

        return root;
    }
}