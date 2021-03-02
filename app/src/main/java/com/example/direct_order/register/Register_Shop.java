package com.example.direct_order.register;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.direct_order.R;
import com.example.direct_order.customermain.AdrData;
import com.example.direct_order.ui.login.Login_sell;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Register_Shop extends AppCompatActivity {
    EditText et_shopname, et_shopnum, et_instagram, et_shopad, et_shopacc;
    RadioGroup rg, rg2;
    RadioButton r1, r2;
    ImageView setmain;
    FirebaseStorage storage = FirebaseStorage.getInstance();
    private FirebaseAuth firebaseAuth;

    private List<Address> list = new ArrayList<>();
    private AdrData adrdata = new AdrData();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register__shop);

        Button btn_reg = findViewById(R.id.btn_add);

        setmain = findViewById(R.id.image_shop);
        setmain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(); //기기 기본 갤러리 접근
                intent.setType(MediaStore.Images.Media.CONTENT_TYPE); //구글 갤러리 접근
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent,101);
            }
        });

        et_shopname=findViewById(R.id.txt_shopname);
        et_shopnum=findViewById(R.id.txt_shopnum);
        et_instagram=findViewById(R.id.txt_instagram);
        et_shopad=findViewById(R.id.txt_shop_ad);
        et_shopacc=findViewById(R.id.txt_shop_acc);

        rg=findViewById(R.id.radio_product);
        rg2=findViewById(R.id.editable);

        r1=findViewById(R.id.radioButton_cake);
        r1.setChecked(true);

        r2=findViewById(R.id.possible);
        r2.setChecked(true);

        btn_reg.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                final String shopname = et_shopname.getText().toString().trim();
                final String shopnum = et_shopnum.getText().toString().trim();
                final String insta = et_instagram.getText().toString().trim();
                final String shopad = et_shopad.getText().toString().trim();
                final String shopacc = et_shopacc.getText().toString().trim();
                final RadioButton rb, rb2;
                rb = findViewById(rg.getCheckedRadioButtonId());
                rb2 = findViewById(rg2.getCheckedRadioButtonId());

                final String shopgood = rb.getText().toString();
                final boolean orderedit;
                if (rb2.getText().toString().equals("가능"))
                    orderedit=true;
                else
                    orderedit=false;

                if (!shopname.equals("") && !shopnum.equals("") && !insta.equals("") && !shopad.equals("") && !shopacc.equals("")) {
                    findAdrdata(shopad);
                    showDialog(shopname,shopnum,insta,shopad,shopacc,shopgood,orderedit);
                }
                else {
                    android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(Register_Shop.this);
                    builder.setTitle("").setMessage("빠짐없이 작성하였는지 다시 확인해주세요:)")
                            .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {

                                }
                            });
                    android.app.AlertDialog alertDialog = builder.create();
                    alertDialog.show();
                }
            }
        });
    }

    void showDialog(String shopname, String shopnum, String insta, String shopad, String shopacc, String shopgood, Boolean orderedit) {
        Join ja = (Join) Join.activity;
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("\""+shopname+"\" 마켓을 등록 하시겠습니까?");
        builder.setNegativeButton("예", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                ja.finish();

                // 회원가입정보 join.class에서 받아와서 저장
                FirebaseDatabase database = FirebaseDatabase.getInstance();
                DatabaseReference reference = database.getReference("sellers");
                HashMap<Object, String> hashMap = (HashMap<Object, String>) getIntent().getSerializableExtra("회원가입");
                String uid = hashMap.get("uid");

                reference.child(uid).setValue(hashMap);

                FirebaseFirestore db = FirebaseFirestore.getInstance();

                db.collection("sellers").document(uid).set(hashMap)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {

                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                FirebaseUser user = firebaseAuth.getCurrentUser();
                                user.delete();
                            }
                        });

                HashMap<Object, Object> shophash = new HashMap<>();

                shophash.put("shopname",shopname);
                shophash.put("shopnum",shopnum);
                shophash.put("instagram",insta);
                shophash.put("shopaddress",shopad);
                shophash.put("shoplatitude", adrdata.getLatitude());
                shophash.put("shoplongitude", adrdata.getLongitude());
                shophash.put("shopaccount",shopacc);
                shophash.put("shopgoods",shopgood);
                shophash.put("shopuid", uid);

                db.collection("markets").document(uid).set(shophash)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {

                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                FirebaseUser user = firebaseAuth.getCurrentUser();
                                user.delete();
                            }
                        });

                db.collection("markets").document(uid).update("orderedit",orderedit);

                StorageReference storageRef = storage.getReference();
                StorageReference mountainsRef = storageRef.child(uid);

                setmain.setDrawingCacheEnabled(true);
                setmain.buildDrawingCache();
                Bitmap bitmap = ((BitmapDrawable) setmain.getDrawable()).getBitmap();
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

                Toast.makeText(Register_Shop.this, "가입 완료", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getApplicationContext(), Login_sell.class);
                intent.putExtra("마켓명",shopname);

                finish();
                startActivity(intent);
            }
        });

        builder.setPositiveButton("아니오", null);
        builder.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 101 && resultCode == RESULT_OK) {
            try {
                InputStream is = getContentResolver().openInputStream(data.getData());
                Bitmap bm = BitmapFactory.decodeStream(is);
                is.close();
                setmain.setImageBitmap(bm);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (requestCode == 101 && resultCode == RESULT_CANCELED) {
            Toast.makeText(this, "취소", Toast.LENGTH_SHORT).show();
        }
    }

    private void findAdrdata(String shopad){
        final Geocoder geocoder = new Geocoder(this);

        try {
            list = geocoder.getFromLocationName(
                    shopad, // 지역 이름
                    10); // 읽을 개수
        } catch (IOException e) {
            e.printStackTrace();
            Log.e("geocoder","입출력 오류 - 서버에서 주소 변환 시 에러발생");
        }

        if (list != null) {
            if (list.size() == 0) {
                Log.d("geocoder","해당되는 주소 정보는 없습니다");
            }
            else {
                adrdata.setLatitude(list.get(0).getLatitude());
                adrdata.setLongitude(list.get(0).getLongitude());
            }
        }
    }
}