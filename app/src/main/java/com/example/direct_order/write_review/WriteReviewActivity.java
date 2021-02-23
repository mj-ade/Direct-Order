package com.example.direct_order.write_review;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.direct_order.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

public class WriteReviewActivity extends AppCompatActivity {
    ImageView imageView;
    EditText editText;
    Button button;
    RatingBar ratingBar;
    String shopid;
    Boolean isImgClicked;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write_review);

        editText = (EditText)findViewById(R.id.editText);
        button = (Button)findViewById(R.id.button);
        ratingBar = findViewById(R.id.ratingBar);
        imageView = findViewById(R.id.imageView);

        String docId = String.valueOf(getIntent().getIntExtra("position", 1));
        final FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String uid = user.getUid();
        CollectionReference coRef = db.collection("customers").document(uid).collection("orders");
        //documnet로 가져온다
        coRef.document(docId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        shopid = (String) document.get("shopuid");
                        Log.d("WriteReview", "DocumentSnapshot data: " + document.getData());
                    } else {
                        Log.d("WriteReview", "No such document");
                    }
                }else {
                    Log.d("WriteReviewActivity", "get failed with ", task.getException());
                }
            }
        });

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGallery();
                isImgClicked = true;
            }
        });

        Map<String, Object> review = new HashMap<>();
        button.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                //Log.d("tag","masg");
                review.put("stars", ratingBar.getRating());
                review.put("content", editText.getText().toString().trim());
                //해당마켓 document
                db.collection("markets").document(shopid).collection("ReviewList").document().set(review);
                imageUpload();
                coRef.document(docId).update("review", true);
                Toast.makeText(getApplicationContext(),"리뷰가 저장되었습니다", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }

    private void imageUpload() {
        Bitmap bitmap = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmssSSS");
        String filename = sdf.format(System.currentTimeMillis());
        StorageReference ref = FirebaseStorage.getInstance().getReference("review_image/" + filename + ".jpg");
        UploadTask uploadTask = ref.putBytes(data);
        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // taskSnapshot.getMetadata() contains file metadata such as size, content-type, etc.
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Toast.makeText(getApplicationContext(), "실패", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void openGallery() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, 101);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 101) {
            if (resultCode == RESULT_OK && data != null && data.getData() != null) {
                try {
                    InputStream in = getContentResolver().openInputStream(data.getData());
                    Bitmap bitmap = BitmapFactory.decodeStream(in);
                    imageView.setImageBitmap(bitmap);
                    in.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(this, "사진 선택 취소", Toast.LENGTH_LONG).show();
                isImgClicked = false;
            }
        }
    }
}