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
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
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

public class RegisterShopActivity extends AppCompatActivity {
    private EditText et_shopname, et_shopnum, et_instagram, et_shopad, et_shopacc;
    private RadioGroup rg, rg2;
    private RadioButton r1, r2;
    private ImageView marketImage;
    private FirebaseAuth firebaseAuth;
    private List<Address> list = new ArrayList<>();
    private AdrData adrdata = new AdrData();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_shop);

        Button btn_reg = findViewById(R.id.btn_add);

        marketImage = findViewById(R.id.image_shop);
        marketImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
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
                    findAdrData(shopad);
                    showDialog(shopname,shopnum,insta,shopad,shopacc,shopgood,orderedit);
                }
                else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(RegisterShopActivity.this);
                    builder.setMessage("빠짐없이 작성하였는지 다시 확인해주세요:)").setPositiveButton("확인", null);
                    AlertDialog alertDialog = builder.create();
                    alertDialog.show();
                }
            }
        });
    }

    void showDialog(String shopname, String shopnum, String insta, String shopad, String shopacc, String shopgood, Boolean orderedit) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("\""+shopname+"\" 마켓을 등록 하시겠습니까?");
        builder.setNegativeButton("예", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                HashMap<Object, String> hashMap = (HashMap<Object, String>) getIntent().getSerializableExtra("회원가입");
                String uid = hashMap.get("uid");

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
                shophash.put("shoplatitude", Math.round(adrdata.getLatitude()*100) / 100.0);
                shophash.put("shoplongitude", Math.round(adrdata.getLongitude()*100) / 100.0);
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

                StorageReference storageRef = FirebaseStorage.getInstance().getReference();
                StorageReference mountainsRef = storageRef.child(uid);

                marketImage.setDrawingCacheEnabled(true);
                marketImage.buildDrawingCache();
                Bitmap bitmap = ((BitmapDrawable) marketImage.getDrawable()).getBitmap();
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                byte[] data = baos.toByteArray();

                UploadTask uploadTask = mountainsRef.putBytes(data);
                uploadTask.addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {

                    }
                }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                    }
                });

                Toast.makeText(getApplicationContext(), "가입 완료", Toast.LENGTH_SHORT).show();
                finish();
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
                marketImage.setImageBitmap(bm);
                is.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        else if (requestCode == 101 && resultCode == RESULT_CANCELED) {
            Toast.makeText(this, "취소", Toast.LENGTH_SHORT).show();
        }
    }

    private void findAdrData(String shopAddress) {
        final Geocoder geocoder = new Geocoder(this);

        try {
            list = geocoder.getFromLocationName(shopAddress, 10);
        } catch (IOException e) {
            e.printStackTrace();
            Log.e("Geocoder", "입출력 오류 - 서버에서 주소 변환 시 에러 발생");
        }

        if (list != null) {
            if (list.size() == 0) {
                Log.d("Geocoder", "해당되는 주소 정보 없음");
            }
            else {
                adrdata.setLatitude(list.get(0).getLatitude());
                adrdata.setLongitude(list.get(0).getLongitude());
            }
        }
    }
}