package com.example.direct_order.ui.settings;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.method.KeyListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.direct_order.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;

public class MarketSettingFragment extends Fragment {
    FirebaseStorage storage = FirebaseStorage.getInstance();
    private EditText tv1, tv2, tv3, tv4, tv5;
    private TextView tv6, tv7;
    private Button btn_edit;
    private KeyListener k1, k2, k3, k4, k5;
    private ImageView main;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_market_setting, container, false);

        main = root.findViewById(R.id.shop_main);

        tv1 = root.findViewById(R.id.shop_name);
        tv2 = root.findViewById(R.id.shop_num);
        tv3 = root.findViewById(R.id.shop_insta);
        tv4 = root.findViewById(R.id.shop_acc);
        tv5 = root.findViewById(R.id.shop_add);
        tv6 = root.findViewById(R.id.shop_good);
        tv7=root.findViewById(R.id.whether);

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
                        String possible = document.get("orderedit").toString().trim();


                        tv1.setText(name);
                        tv2.setText(num);
                        tv3.setText(insta);
                        tv4.setText(acc);
                        tv5.setText(add);
                        tv6.setText(good);
                        if(possible.equals("true"))
                            tv7.setText("가능");
                        else
                            tv7.setText("불가능");

                    }
                }
            }
        });

        FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
        StorageReference rootRef = firebaseStorage.getReference();
        StorageReference imgRef = rootRef.child(uid);

        if (imgRef != null) {
            imgRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() { //imgRef 자체가 객체.
                @Override
                public void onSuccess(Uri uri) {
                    Glide.with(getActivity()).load(uri).into(main);                            //네트워크 이미지는 Glide로 해결한다.
                }                                                                                   //Glide를 쓰지 않으면 Thread + URL을 써야한다.
            });
        }
            btn_edit = root.findViewById(R.id.edit_shop);
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
                    } else {
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

                                docRef.update("shopname", name);
                                docRef.update("shopnum", num);
                                docRef.update("instagram", insta);
                                docRef.update("shopaccount", acc);
                                docRef.update("shopaddress", add);


                                // Create a storage reference from our app
                                StorageReference storageRef = storage.getReference();
                                StorageReference mountainsRef = storageRef.child(uid);

                                main.setDrawingCacheEnabled(true);
                                main.buildDrawingCache();
                                Bitmap bitmap = ((BitmapDrawable) main.getDrawable()).getBitmap();
                                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                                byte[] data = baos.toByteArray();

                                UploadTask uploadTask = mountainsRef.putBytes(data);
                                uploadTask.addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception exception) {
                                        // Handle unsuccessful uploads
                                    }
                                }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                    @Override
                                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                        // taskSnapshot.getMetadata() contains file metadata such as size, content-type, etc.
                                        // ...
                                    }
                                });

                                btn_edit.setText("수정");
                            }
                        });
                        builder.setPositiveButton("아니오", null);
                        builder.show();


                        btn_edit.setText("수정");
                    }
                }
            });

            tv6.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (btn_edit.getText().toString().equals("완료"))
                        Toast.makeText(getActivity(), "판매 상품 종류는 변경이 불가능합니다.", Toast.LENGTH_LONG).show();
                }
            });
        tv7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (btn_edit.getText().toString().equals("완료"))
                    Toast.makeText(getActivity(), "지정 가능 여부는 변경이 불가능합니다.", Toast.LENGTH_LONG).show();
            }
        });
            main.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (btn_edit.getText().toString().equals("완료")) {
                        //마켓 대표 이미지 변경
                        //갤러리 연동
                        Intent intent = new Intent(); //기기 기본 갤러리 접근
                        intent.setType(MediaStore.Images.Media.CONTENT_TYPE); //구글 갤러리 접근
                        intent.setType("image/*");
                        intent.setAction(Intent.ACTION_GET_CONTENT);
                        startActivityForResult(intent, 101);


                    }
                }
            });
            return root;
        }


    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == 101 && resultCode == RESULT_OK) {
            try {
                InputStream is = getActivity().getContentResolver().openInputStream(data.getData());
                Bitmap bm = BitmapFactory.decodeStream(is);
                is.close();
                main.setImageBitmap(bm);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (requestCode == 101 && resultCode == RESULT_CANCELED) {
            Toast.makeText(getContext(), "취소", Toast.LENGTH_SHORT).show();
        }
    }
}