package com.example.direct_order.ordersheet;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.direct_order.R;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class OrderSheetActivity extends AppCompatActivity implements View.OnTouchListener {
    OptionAdapter adapter;
    ImageView imageView;
    LinearLayout buttonTypeLayout;
    static RelativeLayout touchPanel;
    int selectedType;
    static int position;
    List<Option> allOptionList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_optionsheet);

        touchPanel = findViewById(R.id.imageDesc);
        buttonTypeLayout = findViewById(R.id.button_type_layout);

        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

        adapter = new OptionAdapter(this, allOptionList);

        Button saveButton = findViewById(R.id.save_button);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buttonTypeLayout.setVisibility(View.GONE);
                if (recyclerView.getChildCount() == 0)
                    Toast.makeText(getApplicationContext(), "추가한 옵션이 없습니다", Toast.LENGTH_SHORT).show();
                else {
                    // DB에 저장
                    Toast.makeText(getApplicationContext(), "저장되었습니다", Toast.LENGTH_SHORT).show();
                }
            }
        });

        Button txtButton = findViewById(R.id.txt_button);
        txtButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addRecyclerViewItem(OptionType.TEXT);
                buttonTypeLayout.setVisibility(View.GONE);
            }
        });

        Button imgButton = findViewById(R.id.img_button);
        imgButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addRecyclerViewItem(OptionType.IMAGE);
                buttonTypeLayout.setVisibility(View.GONE);
            }
        });

        Button cbButton = findViewById(R.id.cb_button);
        cbButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedType = OptionType.CHECKBOX;
                buttonTypeLayout.setVisibility(View.VISIBLE);
            }
        });

        Button rdButton = findViewById(R.id.rd_button);
        rdButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedType = OptionType.RADIOBUTTON;
                buttonTypeLayout.setVisibility(View.VISIBLE);
            }
        });

        Button calButton = findViewById(R.id.c_button);
        calButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addRecyclerViewItem(OptionType.CALENDAR);
                buttonTypeLayout.setVisibility(View.GONE);
            }
        });

        Button subTextButton = findViewById(R.id.sub_button_text);
        subTextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(selectedType == OptionType.CHECKBOX) {
                    addRecyclerViewItem(OptionType.CHECKBOX_TEXT);
                }
                else {
                    addRecyclerViewItem(OptionType.RADIOBUTTON_TEXT);
                }
                buttonTypeLayout.setVisibility(View.GONE);
            }
        });

        Button subImageButton = findViewById(R.id.sub_button_image);
        subImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(selectedType == OptionType.CHECKBOX) {
                    addRecyclerViewItem(OptionType.CHECKBOX_IMAGE);
                }
                else {
                    addRecyclerViewItem(OptionType.RADIOBUTTON_IMAGE);
                }
                buttonTypeLayout.setVisibility(View.GONE);
            }
        });

        imageView = findViewById(R.id.imageView);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cropImage(R.id.imageView);
            }
        });

        recyclerView.setAdapter(adapter);
    }

    public void openGallery(int a) {
        position = a;
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, 101);
    }

    public void cropImage(int a) {
        position = a;
        CropImage.activity()
                .setGuidelines(CropImageView.Guidelines.ON)
                .start(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                Uri resultUri = result.getUri();
                try {
                    // 선택한 이미지에서 비트맵 생성
                    InputStream inputStream = getContentResolver().openInputStream(resultUri);
                    Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                    if(position == R.id.imageView)
                        imageView = findViewById(R.id.imageView);
                    else
                        imageView = (ImageView) findViewById(position);
                    imageView.setImageBitmap(bitmap);
                    inputStream.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(this, "사진 선택 취소", Toast.LENGTH_LONG).show();
            }
            else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }

    public void addRecyclerViewItem(int type) {
        allOptionList.add(new Option(allOptionList.size()+1, "010-1111-1000", new OptionForm(this, type)));
        //adapter.addItem(new Option(adapter.items.size(), , ));
        adapter.notifyDataSetChanged();
        //adapter.notifyItemInserted(0);
        /*if (type != OptionType.CALENDAR) { // 그림판에 추가되는 텍스트
            TextView textView = new TextView(this);
            textView.setText("option" + allOptionList.size());
            textView.setOnTouchListener(this);
            textPanel.addView(textView);
        }*/
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        int parentWidth = ((ViewGroup)v.getParent()).getWidth();
        int parentHeight = ((ViewGroup)v.getParent()).getHeight();

        if(event.getAction() == MotionEvent.ACTION_DOWN){
            //oldX = event.getX();// View 내부에서 터치한 지점의 상대 좌표값
            //oldY = event.getY();
        }
        else if(event.getAction() == MotionEvent.ACTION_MOVE){
            v.setX(v.getX() + event.getX() - v.getWidth()/2);
            v.setY(v.getY() + event.getY() - v.getHeight()/2);
        }
        else if(event.getAction() == MotionEvent.ACTION_UP){
            if (v.getX() < 0){
                v.setX(0);
            }
            else if ((v.getX() + v.getWidth()) > parentWidth){
                v.setX(parentWidth - v.getWidth());
            }

            if (v.getY() < 0){
                v.setY(0);
            }
            else if ((v.getY() + v.getHeight()) > parentHeight){
                v.setY(parentHeight - v.getHeight());
            }
        }
        return true;
    }
}
