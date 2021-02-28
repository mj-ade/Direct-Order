package com.example.direct_order.productorder;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.request.target.Target;
import com.example.direct_order.R;
import com.example.direct_order.GlideApp;
import com.example.direct_order.ordersheet.OnPreviewSetupListener;
import com.example.direct_order.ordersheet.Option;
import com.example.direct_order.ordersheet.OptionAdapter;
import com.example.direct_order.ordersheet.OptionType;
import com.example.direct_order.ordersheet.StickerImageView;
import com.example.direct_order.ordersheet.StickerTextView;
import com.example.direct_order.ordersheet.StickerView;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

import static com.example.direct_order.ordersheet.OrderSheetActivity.dpToPx;

public class ProductOrderActivity extends AppCompatActivity {
    private final String TAG = "PRODUCTORDER_ACTIVITY";
    private final int IMAGE_REQUEST_CODE = 101;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
    private DocumentReference customerRef = db.collection("customers").document(uid);
    private CollectionReference customerOrderRef = customerRef.collection("orders");

    private DocumentReference marketRef;

    public static boolean isCustomer;
    public static String pickupDate, pickupTime;
    public static StickerView[] stickerViews = new StickerView[20];

    //OptionType.IMAGE
    public static ImageView[] imageViews = new ImageView[20];
    public static boolean[] clicked = new boolean[20];

    //OptionType.RADIOBUTTON_TEXT
    public static MyVariable myVar = new MyVariable();

    private NestedScrollView scrollView;
    private ViewGroup viewGroup;
    private OptionAdapter adapter;
    private RelativeLayout touchPanel;
    private RecyclerView recyclerView;
    private boolean[] filled = new boolean[20];
    private int[][] colors = new int[20][10];
    private Uri resultUri;
    private float move_orgX, move_orgY;
    private int number;
    private int newId;
    private boolean orderEdit;
    private String customerName;
    private String shopuid;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_productorder);
        isCustomer = true;

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_back);

        orderEdit = getIntent().getBooleanExtra("orderEdit", false);
        shopuid = getIntent().getStringExtra("shopuid");
        marketRef = db.collection("markets").document(shopuid);
        DocumentReference marketOrderSheet = marketRef.collection("OrderSheet").document("sheet");
        CollectionReference optionRef = marketOrderSheet.collection("Options");

        scrollView = findViewById(R.id.nested_scrollview);
        viewGroup = findViewById(R.id.included_view);
        touchPanel = viewGroup.findViewById(R.id.imageDesc);
        recyclerView = viewGroup.findViewById(R.id.recyclerView);

        setCustomerName();
        checkNumOfOrder();
        setupImageView(marketOrderSheet);
        setupPreviews(marketOrderSheet);
        setupRecyclerView(optionRef);
        retrieveFunction(optionRef);

        myVar.setOnValueChangeListener(new MyVariable.OnValueChangeListener() {
            @Override
            public void onValueChange(int num, int newVal) {
                if (stickerViews[num - 1] instanceof StickerImageView)
                    ((StickerImageView) stickerViews[num - 1]).getIv_main().setColorFilter(colors[num - 1][newVal], PorterDuff.Mode.SRC_IN);
                else if (stickerViews[num - 1] instanceof StickerTextView)
                    ((StickerTextView) stickerViews[num - 1]).getTv_main().setTextColor(colors[num - 1][newVal]);
            }

            @Override
            public void onStrChange(int num, String newStr) {
                String imgTag = (String) ((StickerImageView) stickerViews[num-1]).getIv_main().getTag();
                if (imgTag.substring(0, 1).equals("o")) {
                    if (stickerViews[num - 1] instanceof StickerImageView) {
                        StorageReference ref = FirebaseStorage.getInstance().getReference(newStr);
                        GlideApp.with(getApplicationContext())
                                .load(ref)
                                .override(Target.SIZE_ORIGINAL)
                                .into(((StickerImageView) stickerViews[num - 1]).getIv_main());
                    }
                }
            }
        });
    }

    private void setCustomerName() {
        customerRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists())
                        customerName = (String) document.get("image");
                    else {
                        customerName = "";
                        Log.d("CUSTOMER_INFO", "No such document");
                    }
                }
                else {
                    Log.d("CUSTOMER_INFO", "get failed with ", task.getException());
                }
            }
        });
    }

    private void checkNumOfOrder() {
        customerOrderRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    newId = task.getResult().size();
                }
                else
                    Log.d(TAG, "Error getting documents: ", task.getException());
            }
        });
    }

    private void setupImageView(DocumentReference marketOrderSheet) {
        ImageView imageView = viewGroup.findViewById(R.id.imageView);
        marketOrderSheet.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        String imageName = (String) document.get("image");
                        if (imageName.substring(0, 12).equals("option_main/")) {
                            StorageReference ref = FirebaseStorage.getInstance().getReference(imageName);
                            GlideApp.with(getApplicationContext())
                                    .load(ref)
                                    .override(Target.SIZE_ORIGINAL)
                                    .into(imageView);
                        }
                    }
                    else
                        Log.d("SHEET_MAIN_IMG", "No such document");
                }
                else {
                    Log.d("SHEET_MAIN_IMG", "get failed with ", task.getException());
                }
            }
        });
    }

    private void setupPreviews(DocumentReference marketOrderSheet) {
        CollectionReference previewRef = marketOrderSheet.collection("Previews");
        for (int i = 0; i < 20; i++) {
            int finalI = i;
            previewRef.document("stickerID"+(i+1)).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            Log.d("TAG", "DocumentSnapshot data: " + document.getData());

                            String shape = (String) document.get("shape");
                            int x = ((Long) document.get("x")).intValue();
                            int y = ((Long) document.get("y")).intValue();
                            int width = ((Long) document.get("width")).intValue();
                            int height = ((Long) document.get("height")).intValue();
                            String desc = (String) document.get("desc");
                            ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(
                                    dpToPx(getApplicationContext(), width),
                                    dpToPx(getApplicationContext(), height));

                            if (stickerViews[finalI] != null)   // 중복 생성 방지
                                touchPanel.removeView(stickerViews[finalI]);

                            if (shape.equals("text")) {
                                stickerViews[finalI] = new StickerTextView(getApplicationContext());
                                ((StickerTextView) stickerViews[finalI]).setText(desc);
                            }
                            else if (shape.equals("image")) {
                                stickerViews[finalI] = new StickerImageView(getApplicationContext());
                                ((StickerImageView) stickerViews[finalI]).getIv_main().setScaleType(ImageView.ScaleType.FIT_XY);
                                if (desc.equals("square"))
                                    ((StickerImageView) stickerViews[finalI]).setImageResource(R.drawable.sticker_preview_square);
                                else if (desc.equals("circle"))
                                    ((StickerImageView) stickerViews[finalI]).setImageResource(R.drawable.sticker_preview_circle);
                                else {  // 사용자 지정 이미지이면
                                    StorageReference ref = FirebaseStorage.getInstance().getReference(desc);
                                    GlideApp.with(getApplicationContext())
                                            .load(ref)
                                            .override(Target.SIZE_ORIGINAL)
                                            .into(((StickerImageView) stickerViews[finalI]).getIv_main());
                                }
                                ((StickerImageView) stickerViews[finalI]).getIv_main().setTag(desc);
                            }

                            if (!shape.equals("none")) {
                                stickerViews[finalI].setLayoutParams(layoutParams);
                                stickerViews[finalI].setX(dpToPx(getApplicationContext(), x));
                                stickerViews[finalI].setY(dpToPx(getApplicationContext(), y));
                                stickerViews[finalI].setControlItemsHidden(true);
                                if (orderEdit) {
                                    stickerViews[finalI].setOnTouchListener(new View.OnTouchListener() {
                                        @Override
                                        public boolean onTouch(View view, MotionEvent event) {
                                            switch (event.getAction()) {
                                                case MotionEvent.ACTION_DOWN:
                                                    move_orgX = event.getRawX();
                                                    move_orgY = event.getRawY();
                                                    scrollView.requestDisallowInterceptTouchEvent(true);
                                                    break;
                                                case MotionEvent.ACTION_MOVE:
                                                    float offsetX = event.getRawX() - move_orgX;
                                                    float offsetY = event.getRawY() - move_orgY;
                                                    stickerViews[finalI].setX(stickerViews[finalI].getX() + offsetX);
                                                    stickerViews[finalI].setY(stickerViews[finalI].getY() + offsetY);
                                                    move_orgX = event.getRawX();
                                                    move_orgY = event.getRawY();
                                                    break;
                                                case MotionEvent.ACTION_UP:
                                                    scrollView.requestDisallowInterceptTouchEvent(false);
                                                    break;
                                            }
                                            return true;
                                        }
                                    });
                                }
                                else
                                    stickerViews[finalI].setOnTouchListener(null);
                            }
                        }
                        else {
                            Log.d("TAG", "No such document" + finalI);
                        }
                        new OnPreviewSetupListener() {
                            @Override
                            public void onPreviewSetup(StickerView[] views) {
                                for (int i = 0; i < 20; i++) {
                                    if (views[i] != null && views[i] instanceof StickerImageView) {
                                        if (views[i].getParent() != null)
                                            ((ViewGroup) views[i].getParent()).removeView(views[i]);
                                        touchPanel.addView(views[i]);
                                    }
                                }
                                for (int i = 0; i < 20; i++) {
                                    if (views[i] != null && views[i] instanceof StickerTextView) {
                                        if (views[i].getParent() != null)
                                            ((ViewGroup) views[i].getParent()).removeView(views[i]);
                                        touchPanel.addView(views[i]);
                                    }
                                }
                            }
                        }.onPreviewSetup(stickerViews);
                    }
                    else {
                        Log.d("TAG", "get failed with ", task.getException());
                    }
                }
            });
        }
    }

    private void setupRecyclerView(CollectionReference optionRef) {
        Query query = optionRef.orderBy("number", Query.Direction.ASCENDING);
        FirestoreRecyclerOptions<Option> options = new FirestoreRecyclerOptions.Builder<Option>()
                .setQuery(query, Option.class)
                .build();
        adapter = new OptionAdapter(options, this);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(adapter);
    }

    private void retrieveFunction(CollectionReference optionRef) {
        optionRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (DocumentSnapshot document : task.getResult()) {
                        long type = (Long) document.get("type");
                        if (type != OptionType.RADIOBUTTON_TEXT)
                            continue;

                        String function = (String) document.get("func");
                        if (function.charAt(4) == '1')   // 기능 없음이면 저장 안함
                            continue;

                        int index = ((Long) document.get("number")).intValue() - 1;
                        int parentIndex = ((Long) document.get("parentNumber")).intValue() - 1;
                        int pos = (index == parentIndex) ? index : parentIndex;

                        if (function.equals("func3"))
                            filled[pos] = true;
                        if (filled[pos]) {
                            String imgTag = (String) ((StickerImageView) stickerViews[pos]).getIv_main().getTag();
                            if (imgTag.equals("circle"))
                                ((StickerImageView) stickerViews[pos]).setImageResource(R.drawable.sticker_preview_circle_filled);
                            else if (imgTag.equals("square"))
                                ((StickerImageView) stickerViews[pos]).setImageResource(R.drawable.sticker_preview_square_filled);
                        }

                        int count = 0;
                        StringTokenizer st = new StringTokenizer((String) document.get("content"), "&");

                        while (st.hasMoreTokens()) {
                            String s = st.nextToken();
                            colors[pos][count++] = Color.parseColor(s.substring(s.indexOf("#"), s.length()));
                        }
                    }
                }
                else
                    Log.d("TAG", "Error getting documents: ", task.getException());
            }
        });
    }

    private void saveNote() {
        long currentTime =  System.currentTimeMillis();
        CollectionReference orderRef = marketRef.collection("OrderList");
        DocumentReference documentRef = orderRef.document();
        String filePath = captureView(scrollView, scrollView.getChildAt(0).getHeight(), scrollView.getChildAt(0).getWidth());

        Toast.makeText(getApplicationContext(), "주문이 완료되었습니다", Toast.LENGTH_SHORT).show();

        Map<String, Object> data = new HashMap<>();
        data.put("screenshot", filePath);
        data.put("name", customerName);
        data.put("date", new SimpleDateFormat("yyyy/MM/dd").format(currentTime));
        data.put("time", new SimpleDateFormat("HH:mm").format(currentTime));
        data.put("pickup", pickupDate);
        data.put("pickupTime", pickupTime);
        data.put("price", 0);
        data.put("process", 0);
        documentRef.set(data);

        Map<String, Object> customerData = new HashMap<>();
        customerData.put("review", false);
        customerData.put("screenshot", filePath);
        customerData.put("date", new SimpleDateFormat("yyyy/MM/dd").format(currentTime));
        customerData.put("pickup", pickupDate);
        customerData.put("pickupTime", pickupTime);
        customerData.put("shopuid", shopuid);
        customerData.put("docid", documentRef.getId());
        customerOrderRef.document(String.valueOf(newId)).set(customerData);

        isCustomer = false;
        finish();
    }

    private String captureView(View view, int height, int width) {
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        Drawable bgDrawable = view.getBackground();
        if (bgDrawable != null)
            bgDrawable.draw(canvas);
        else
            canvas.drawColor(Color.WHITE);
        view.draw(canvas);

        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmssSSS");
        String filename = sdf.format(System.currentTimeMillis());
        String filePath = "folder/" + filename + ".png";
        StorageReference ref = FirebaseStorage.getInstance().getReference(filePath);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] data = baos.toByteArray();

        UploadTask uploadTask = ref.putBytes(data);
        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Toast.makeText(getApplicationContext(), "Image Uploaded!!", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(), "Failed " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
        return filePath;
    }

    public void openGallery(int number) {
        this.number = number;
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, IMAGE_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == IMAGE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                resultUri = data.getData();
                try {
                    imageViews[number-1].setImageURI(resultUri);
                    clicked[number-1] = true;
                    ((StickerImageView) stickerViews[number-1]).setImageUri(resultUri);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(this, "사진 선택 취소", Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.new_option_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.save_note:
                saveNote();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("주문서 작성을 취소하시겠습니까?");
        builder.setPositiveButton("예", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                isCustomer = false;
                finish();
            }
        });
        builder.setNegativeButton("아니오", null);
        builder.show();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
