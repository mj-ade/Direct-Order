package com.example.direct_order.ordersheet;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.request.target.Target;
import com.example.direct_order.GlideApp;
import com.example.direct_order.R;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
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
import com.pedro.library.AutoPermissions;
import com.pedro.library.AutoPermissionsListener;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator;

public class OrderSheetActivity extends ImageCropActivity implements AutoPermissionsListener {
    static RelativeLayout touchPanel;
    static boolean isFocus, isUpdate;
    static int type, numOfOption;
    static StickerView[] stickerPreviews = new StickerView[20];
    static boolean[] numberDup = new boolean[20];
    static Option option;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
    private DocumentReference market = db.collection("markets").document(uid);
    private CollectionReference orderSheetRef = market.collection("OrderSheet");
    private DocumentReference myOrderSheet = orderSheetRef.document("sheet");
    private CollectionReference optionRef = myOrderSheet.collection("Options");
    private CollectionReference previewRef = myOrderSheet.collection("Previews");

    private OptionAdapter adapter;
    private LinearLayout buttonTypeLayout, cover;
    private ViewGroup viewGroup;
    private String imageName = "";
    private int selectedType;
    private Boolean isExist;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ordersheet);

        AutoPermissions.Companion.loadAllPermissions(this, 101);

        viewGroup = findViewById(R.id.included_view);
        touchPanel = viewGroup.findViewById(R.id.imageDesc);
        buttonTypeLayout = findViewById(R.id.button_type_layout);
        cover = findViewById(R.id.cover);
        cover.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                displaySubButtonLayout(false);
            }
        });

        ImageView iv_main = viewGroup.findViewById(R.id.imageView);
        setupMainImage(iv_main); // DB에서 image 가져오기
        setupPreviews();    //DB에서 preview 가져와서 touchPanel에 배치
        iv_main.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isFocus) {
                    setImageView(iv_main);
                    pickFromGallery();
                }
                isFocus = false;
                for (int i = 0; i < touchPanel.getChildCount(); i++) {
                    if (touchPanel.getChildAt(i) instanceof StickerView)
                        ((StickerView) touchPanel.getChildAt(i)).setControlItemsHidden(true);
                }
            }
        });

        Button saveButton = findViewById(R.id.save_button);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveSheet();
            }
        });

        LinearLayout textButtonLayout = findViewById(R.id.text_button_layout);
        textButtonLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isUpdate = false;
                displaySubButtonLayout(false);
                type = OptionType.TEXT;
                numOfOption = 1;
                startActivity(new Intent(OrderSheetActivity.this, TextOptionActivity.class));
            }
        });

        LinearLayout imageButtonLayout = findViewById(R.id.image_button_layout);
        imageButtonLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isUpdate = false;
                displaySubButtonLayout(false);
                type = OptionType.IMAGE;
                numOfOption = 1;
                startActivity(new Intent(OrderSheetActivity.this, ImageOptionActivity.class));
            }
        });

        LinearLayout cbButtonLayout = findViewById(R.id.cb_button_layout);
        cbButtonLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedType = OptionType.CHECKBOX;
                displaySubButtonLayout(true);
            }
        });

        LinearLayout rbButtonLayout = findViewById(R.id.rb_button_layout);
        rbButtonLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedType = OptionType.RADIOBUTTON;
                displaySubButtonLayout(true);
            }
        });

        LinearLayout calendarButtonLayout = findViewById(R.id.calendar_button_layout);
        calendarButtonLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isUpdate = false;
                displaySubButtonLayout(false);
                type = OptionType.CALENDAR;
                numOfOption = 1;
                startActivity(new Intent(OrderSheetActivity.this, CalendarOptionActivity.class));
            }
        });

        ImageButton iv_back = findViewById(R.id.back);
        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                displaySubButtonLayout(false);
            }
        });

        LinearLayout subTextButtonLayout = findViewById(R.id.sub_text_button_layout);
        subTextButtonLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isUpdate = false;
                displaySubButtonLayout(false);
                if (selectedType == OptionType.CHECKBOX)
                    type = OptionType.CHECKBOX_TEXT;
                else
                    type = OptionType.RADIOBUTTON_TEXT;
                displayDialog(type);
            }
        });

        LinearLayout subImgButtonLayout = findViewById(R.id.sub_image_button_layout);
        subImgButtonLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isUpdate = false;
                displaySubButtonLayout(false);
                if (selectedType == OptionType.CHECKBOX)
                    type = OptionType.CHECKBOX_IMAGE;
                else
                    type = OptionType.RADIOBUTTON_IMAGE;
                displayDialog(type);
            }
        });

        setupRecyclerView();
    }

    private void displaySubButtonLayout(boolean flag) {
        if (flag) {
            buttonTypeLayout.setVisibility(View.VISIBLE);
            cover.setVisibility(View.VISIBLE);
        }
        else {
            buttonTypeLayout.setVisibility(View.GONE);
            cover.setVisibility(View.GONE);
        }
    }

    private void setupRecyclerView() {
        Query query = optionRef.orderBy("number", Query.Direction.ASCENDING);

        FirestoreRecyclerOptions<Option> options = new FirestoreRecyclerOptions.Builder<Option>()
                .setQuery(query, Option.class)
                .build();

        adapter = new OptionAdapter(options, this);

        RecyclerView recyclerView = viewGroup.findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(adapter);

        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                if (direction == ItemTouchHelper.LEFT) {
                    adapter.deleteItem(viewHolder.getAdapterPosition());

                    // 아이템 삭제에 따른 num array 갱신
                    int numberIndex = adapter.getItem(viewHolder.getAdapterPosition()).getNumber() - 1;
                    numberDup[numberIndex] = false;

                    // 아이템 삭제하면 preview도 삭제
                    if (stickerPreviews[numberIndex] != null) {
                        touchPanel.removeView(stickerPreviews[numberIndex]);
                        stickerPreviews[numberIndex] = null;
                    }
                    //db에 저장된 내용도 삭제
                    previewRef.document("stickerID" + (numberIndex + 1)).delete();
                }
                else if (direction == ItemTouchHelper.RIGHT) {
                    option = adapter.updateItem(viewHolder.getAdapterPosition());
                    isUpdate = true;

                    if (option.getType() == OptionType.TEXT)
                        startActivity(new Intent(OrderSheetActivity.this, TextOptionActivity.class));
                    else if (option.getType() == OptionType.IMAGE)
                        startActivity(new Intent(OrderSheetActivity.this, ImageOptionActivity.class));
                    else if (option.getType() >= OptionType.CHECKBOX_TEXT && option.getType() <= OptionType.RADIOBUTTON_IMAGE) {
                        if (option.getType() % 2 == 0)
                            startActivity(new Intent(OrderSheetActivity.this, CompoundTextOptionActivity.class));
                        else
                            startActivity(new Intent(OrderSheetActivity.this, CompoundImageOptionActivity.class));
                    }
                    else if (option.getType() == OptionType.CALENDAR)
                        startActivity(new Intent(OrderSheetActivity.this, CalendarOptionActivity.class));
                }
            }

            @Override
            public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
                new RecyclerViewSwipeDecorator.Builder(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                        .addSwipeLeftBackgroundColor(Color.parseColor("#F13A1F"))
                        .addSwipeLeftActionIcon(R.drawable.ic_delete)
                        .addSwipeLeftLabel("Delete")
                        .setSwipeLeftLabelColor(Color.WHITE)
                        .addSwipeRightBackgroundColor(Color.parseColor("#409E44"))
                        .addSwipeRightActionIcon(R.drawable.ic_mode)
                        .addSwipeRightLabel("Edit")
                        .setSwipeRightLabelColor(Color.WHITE)
                        .create()
                        .decorate();
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            }
        }).attachToRecyclerView(recyclerView);
    }

    private void setupMainImage(ImageView imageView) {
        myOrderSheet.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        imageName = (String) document.get("image");
                        if (imageName == null || imageName.trim().isEmpty()) {
                            imageView.setImageResource(R.drawable.c10);  //개발자 기본 제공 이미지
                            imageName = "";
                        }
                        else {
                            StorageReference ref = FirebaseStorage.getInstance().getReference(imageName);
                            GlideApp.with(getApplicationContext())
                                    .load(ref)
                                    .override(Target.SIZE_ORIGINAL)
                                    .into(imageView);
                        }
                    }
                    else {
                        imageName = "";
                        Log.d("MAIN_IMG", "No such document");
                    }
                }
                else {
                    Log.d("MAIN_IMG", "get failed with ", task.getException());
                }
            }
        });
    }

    private void setupPreviews() {  //preview 가져오기
        for (int i = 0; i < 20; i++) {
            int finalI = i;

            previewRef.document("stickerID"+(finalI+1)).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            String shape = (String) document.get("shape");
                            int x = ((Long) document.get("x")).intValue();
                            int y = ((Long) document.get("y")).intValue();
                            int width = ((Long) document.get("width")).intValue();
                            int height = ((Long) document.get("height")).intValue();
                            String desc = (String) document.get("desc");
                            ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(
                                    dpToPx(getApplicationContext(), width),
                                    dpToPx(getApplicationContext(), height));

                            if (shape.equals("text")) {
                                stickerPreviews[finalI] = new StickerTextView(getApplicationContext());
                                ((StickerTextView) stickerPreviews[finalI]).setText(desc);
                            }
                            else if (shape.equals("image")) {
                                stickerPreviews[finalI] = new StickerImageView(getApplicationContext());
                                ((StickerImageView) stickerPreviews[finalI]).getIv_main().setScaleType(ImageView.ScaleType.FIT_XY);
                                if (desc.equals("square"))
                                    ((StickerImageView) stickerPreviews[finalI]).setImageResource(R.drawable.sticker_preview_square);
                                else if (desc.equals("circle"))
                                    ((StickerImageView) stickerPreviews[finalI]).setImageResource(R.drawable.sticker_preview_circle);
                                else {  // 사용자 지정 이미지
                                    StorageReference ref = FirebaseStorage.getInstance().getReference(desc);
                                    GlideApp.with(getApplicationContext()).load(ref).into(((StickerImageView) stickerPreviews[finalI]).getIv_main());
                                }
                                ((StickerImageView) stickerPreviews[finalI]).getIv_main().setTag(desc);
                            }

                            if (!shape.equals("none")) {
                                stickerPreviews[finalI].setLayoutParams(layoutParams);
                                stickerPreviews[finalI].setX(dpToPx(getApplicationContext(), x));
                                stickerPreviews[finalI].setY(dpToPx(getApplicationContext(), y));
                            }
                        }
                        else {
                            Log.d("PREVIEW_TAG", "No such document");
                        }
                        new OnPreviewSetupListener() {
                            @Override
                            public void onPreviewSetup(StickerView[] views) {
                                for (int i = 0; i < 20; i++) {
                                    if (views[i] != null) {
                                        if (views[i].getParent() != null)
                                            ((ViewGroup) views[i].getParent()).removeView(views[i]);
                                        touchPanel.addView(views[i]);
                                    }
                                }
                            }
                        }.onPreviewSetup(stickerPreviews);
                    }
                    else {
                        Log.d("PREVIEW_TAG", "get failed with ", task.getException());
                    }
                }
            });
        }
    }

    private void saveSheet() {
        Uri uri = getResultUri();
        if (imageName.trim().isEmpty() && uri == null) {
            Toast.makeText(this, "이미지를 넣어주세요", Toast.LENGTH_SHORT).show();
            return;
        }
        else {
            if (uri != null) {
                setUploadCompleteListener(new OnUploadCompleteListener() {
                    @Override
                    public void onUploadComplete() {

                    }
                });
                imageName = uploadImage(uri, "option_main/");
            }
        }
        Map<String, String> data = new HashMap<>();
        data.put("image", imageName);
        myOrderSheet.set(data);

        for (int i = 0; i < stickerPreviews.length; i++) {
            String shape = "";
            String desc = "";
            Sticker s = null;

            if (stickerPreviews[i] == null) {
                shape = "none";
                s = new Sticker(i + 1, shape);
            }
            else {
                if (stickerPreviews[i] instanceof StickerTextView) {
                    shape = "text";
                    desc = ((StickerTextView) stickerPreviews[i]).getText();
                }
                else if (stickerPreviews[i] instanceof StickerImageView) {
                    shape = "image";
                    desc = (String) ((StickerImageView) stickerPreviews[i]).getIv_main().getTag();
                }

                if (stickerPreviews[i].isDeleted())
                    s = new Sticker(i + 1, "none");
                else {
                    s = new Sticker(i + 1,
                            shape,
                            pxToDp(getApplicationContext(), (int) stickerPreviews[i].getX()),
                            pxToDp(getApplicationContext(), (int) stickerPreviews[i].getY()),
                            pxToSp(getApplicationContext(), (int) stickerPreviews[i].getWidth()),
                            pxToDp(getApplicationContext(), (int) stickerPreviews[i].getHeight()),
                            desc);
                }
            }
            previewRef.document("stickerID" + (i + 1)).set(s);
        }

        optionRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    if (task.getResult().size() > 0) {
                        isExist = true;
                        Toast.makeText(getApplicationContext(), "주문서가 저장되었습니다", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        isExist = false;
                        Toast.makeText(getApplicationContext(), "저장된 주문서가 없습니다", Toast.LENGTH_SHORT).show();
                    }
                }
                else
                    Log.d("ORDER_SHEET", "Error getting documents: ", task.getException());

                Intent intent = new Intent();
                intent.putExtra("isExist", isExist);
                setResult(RESULT_OK, intent);
                finish();
            }
        });
    }

    private void displayDialog(int type) {
        LinearLayout container = new LinearLayout(this);
        EditText editText = new EditText(this);
        LinearLayout.LayoutParams params = new  LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.topMargin = dpToPx(getApplicationContext(), 12);
        params.leftMargin = dpToPx(getApplicationContext(), 24);
        params.rightMargin = dpToPx(getApplicationContext(), 24);
        editText.setLayoutParams(params);
        editText.setInputType(InputType.TYPE_CLASS_NUMBER);
        container.addView(editText);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("옵션 개수를 입력해주세요(1~10)");
        builder.setView(container);
        builder.setPositiveButton("확인", null);

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
        alertDialog.getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String inputString = editText.getText().toString();
                if (inputString.isEmpty()) {
                    Toast.makeText(getApplicationContext(),"값을 입력하세요",Toast.LENGTH_SHORT).show();
                }
                else {
                    int inputValue = Integer.parseInt(inputString);
                    if (inputValue == 0 || inputValue > 10)
                        Toast.makeText(getApplicationContext(), "유효한 값을 입력해주세요(1~10)", Toast.LENGTH_SHORT).show();
                    else {
                        numOfOption = inputValue;
                        alertDialog.dismiss();
                        //텍스트인지 이미지인지에 따라 갈 곳이 달라짐
                        if (type % 2 == 0)
                            startActivity(new Intent(OrderSheetActivity.this, CompoundTextOptionActivity.class));
                        else
                            startActivity(new Intent(OrderSheetActivity.this, CompoundImageOptionActivity.class));
                    }
                }
            }
        });
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

    public static int dpToPx(Context context, int dp) {
        float density = context.getResources().getDisplayMetrics().density; // density = densityDpi / DisplayMetrics.DENSITY_DEFAULT
        return (int) (dp * density);
    }

    public static int pxToDp(Context context, int px) {
        float density = context.getResources().getDisplayMetrics().density; // density = densityDpi / DisplayMetrics.DENSITY_DEFAULT
        return (int) (px / density);
    }

    public static int pxToSp(Context context, int px) {
        float density = context.getResources().getDisplayMetrics().scaledDensity; // density = densityDpi / DisplayMetrics.DENSITY_DEFAULT
        return (int) (px / density);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        AutoPermissions.Companion.parsePermissions(this, requestCode, permissions, this);
    }

    @Override
    public void onGranted(int i, @NotNull String[] strings) {
        //Toast.makeText(this, "permissions granted", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDenied(int i, @NotNull String[] strings) {
        //Toast.makeText(this, "permissions denied", Toast.LENGTH_SHORT).show();
    }
}
