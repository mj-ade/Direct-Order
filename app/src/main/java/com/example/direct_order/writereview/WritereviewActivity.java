package com.example.direct_order.writereview;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.direct_order.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;

public class WritereviewActivity extends AppCompatActivity {
    EditText editText;
    Button button;
    RatingBar ratingBar;
    //TextView textView;
    String shopid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_writereview);

        editText = (EditText)findViewById(R.id.editText);
        button = (Button)findViewById(R.id.button);
        ratingBar = findViewById(R.id.ratingBar);
        //textView = findViewById(R.id.editText);

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

        Map<String, Object> review = new HashMap<>();
        button.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                //Log.d("tag","masg");
                review.put("stars",ratingBar.getRating());
                review.put("content", editText.getText().toString().trim());
                //해당마켓 document
                db.collection("markets").document(shopid).collection("ReviewList").document().set(review);

                Toast.makeText(getApplicationContext(),"리뷰가 저장되었습니다", Toast.LENGTH_SHORT).show();

                coRef.document(docId).update("review", true);

                finish();
            }
        });
    }
}