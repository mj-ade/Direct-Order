package com.example.direct_order.register;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.example.direct_order.R;
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
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;


public class Register_Shop extends AppCompatActivity {

    EditText et_shopname, et_shopnum, et_instagram, et_shopad, et_shopacc;
    RadioGroup rg;
    RadioButton r1;
    ImageView setmain;
    FirebaseStorage storage = FirebaseStorage.getInstance();
    private FirebaseAuth firebaseAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register__shop);


        Button btn_reg = findViewById(R.id.btn_add);

        //shop image 등록 위한 gallery 접근
        //checkSelfPermission();
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

        //listDataGroup.add(getString(R.string.show_shop));

        et_shopname=findViewById(R.id.txt_shopname);
        et_shopnum=findViewById(R.id.txt_shopnum);
        et_instagram=findViewById(R.id.txt_instagram);
        et_shopad=findViewById(R.id.txt_shop_ad);
        et_shopacc=findViewById(R.id.txt_shop_acc);

        rg=findViewById(R.id.radio_product);

        r1=findViewById(R.id.radioButton_cake);
        r1.setChecked(true);
        /*r2=findViewById(R.id.radioButton_macaron);
        r3=findViewById(R.id.radioButton_case);
        r4=findViewById(R.id.radioButton_acc);
        r5=findViewById(R.id.radioButton_etc);*/

        btn_reg.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {

                //Toast.makeText(getApplicationContext(),(MainForSeller.listDataChild).get(0).toString(),Toast.LENGTH_LONG).show();
                //Toast.makeText(getApplicationContext(),MainForSeller.shopList.toString(),Toast.LENGTH_LONG).show();
                final String shopname = et_shopname.getText().toString().trim();
                final String shopnum = et_shopnum.getText().toString().trim();
                final String insta = et_instagram.getText().toString().trim();
                final String shopad = et_shopad.getText().toString().trim();
                final String shopacc = et_shopacc.getText().toString().trim();
                final RadioButton rb;
                rb = findViewById(rg.getCheckedRadioButtonId());
                final String shopgood = rb.getText().toString();

                if(!shopname.equals("")&&!shopnum.equals("")&&!insta.equals("")&&!shopad.equals("")&&!shopacc.equals("")) {
                    showDialog(shopname,shopnum,insta,shopad,shopacc,shopgood);
                }
                else{
                    android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(Register_Shop.this);
                    builder.setTitle("").setMessage("빠짐없이 작성하였는지 다시 확인해주세요:)").setPositiveButton("확인", new DialogInterface.OnClickListener() {
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

    void showDialog(String shopname, String shopnum, String insta, String shopad, String shopacc, String shopgood){
        Join ja = (Join) Join.activity;
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("\""+shopname+"\" 마켓을 등록 하시겠습니까?");
        builder.setNegativeButton("예", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                ja.finish();
                //Toast.makeText(getApplicationContext(),list.toString(),Toast.LENGTH_LONG).show();
                //MainForSeller.listDataChild.put(listDataGroup.get(0), shopList);

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
                                //Log.d(TAG, "DocumentSnapshot successfully written!");
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                //Log.w(TAG, "Error writing document", e);
                                FirebaseUser user = firebaseAuth.getCurrentUser();
                                user.delete();
                            }
                        });


                HashMap<Object,String> shophash = new HashMap<>();

                shophash.put("shopname",shopname);
                shophash.put("shopnum",shopnum);
                shophash.put("instagram",insta);
                shophash.put("shopaddress",shopad);
                shophash.put("shopaccount",shopacc);
                shophash.put("shopgoods",shopgood);



                db.collection("markets").document(uid).set(shophash)
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
                                FirebaseUser user = firebaseAuth.getCurrentUser();
                                user.delete();
                            }
                        });

// Create a storage reference from our app
                StorageReference storageRef = storage.getReference();

// Create a reference to "mountains.jpg"
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

                /*HomeFragment fragment = new HomeFragment();
                   Bundle bundle = new Bundle();
                   bundle.putString("마켓",str);
                   fragment.setArguments(bundle);

                FragmentManager fm = getSupportFragmentManager();
                FragmentTransaction ft = fm.beginTransaction();
                ft.add(R.id.fragment_test,fragment);
                ft.commit();
                */


                finish();
                startActivity(intent);
            }
        });

        builder.setPositiveButton("아니오", null);
        builder.show();
    }

    /*    //권한에 대한 응답이 있을때 작동하는 함수
        @Override
        public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
            //권한을 허용 했을 경우
            if(requestCode == 1){
                int length = permissions.length;
                for (int i = 0; i < length; i++) {
                    if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                        //동의
                        Log.d("Register_Shop","권한 허용 : + permissions[i]); } } } }


        /*public void checkSelfPermission() {
            String temp = ""; //파일 읽기 권한 확인
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                temp += Manifest.permission.READ_EXTERNAL_STORAGE + " "; }
            //파일 쓰기 권한 확인
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                temp += Manifest.permission.WRITE_EXTERNAL_STORAGE + " "; }
            if (TextUtils.isEmpty(temp) == false) { // 권한 요청
                ActivityCompat.requestPermissions(this, temp.trim().split(" "),1); }
            else { // 모두 허용 상태
                Toast.makeText(this, "권한을 모두 허용", Toast.LENGTH_SHORT).show(); }
        }
    */
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

}