package com.example.direct_order.order_sheet;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.direct_order.R;

import java.io.InputStream;
import java.util.ArrayList;

public class OrderSheetFragment extends Fragment implements com.example.direct_order.onBackPressedListener{
    private ArrayList<OptionForm> optionFormArrayList = new ArrayList<>();
    static int position;

    ViewGroup viewGroup;
    LinearLayout buttonTypeLayout;
    ImageView imageView;
    int selectedType;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        viewGroup = (ViewGroup) inflater.inflate(R.layout.fragment_optionsheet, container, false);

        buttonTypeLayout = viewGroup.findViewById(R.id.sub_button_layout); //sub button panel

        Button textButton = viewGroup.findViewById(R.id.text_button);
        textButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addOptionFrom(OptionType.TEXT);
                buttonTypeLayout.setVisibility(View.GONE);
            }
        });

        Button imgButton=viewGroup.findViewById(R.id.image_button);
        imgButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addOptionFrom(OptionType.IMAGE);
                buttonTypeLayout.setVisibility(View.GONE);
            }
        });

        Button cbButton = viewGroup.findViewById(R.id.cb_button);
        cbButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedType = OptionType.CHECKBOX;
                buttonTypeLayout.setVisibility(View.VISIBLE);
            }
        });

        Button rbButton = viewGroup.findViewById(R.id.rb_button);
        rbButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedType = OptionType.RADIOBUTTON;
                buttonTypeLayout.setVisibility(View.VISIBLE);
            }
        });

        Button calendarButton = viewGroup.findViewById(R.id.calendar_button);
        calendarButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addOptionFrom(OptionType.CALENDAR);
                buttonTypeLayout.setVisibility(View.GONE);
            }
        });

        Button subTextButton = viewGroup.findViewById(R.id.sub_button_text);
        subTextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(selectedType == OptionType.CHECKBOX)
                    addOptionFrom(OptionType.CHECKBOX_TEXT);
                else
                    addOptionFrom(OptionType.RADIOBUTTON_TEXT);

                buttonTypeLayout.setVisibility(View.GONE);
            }
        });

        Button subImageButton = viewGroup.findViewById(R.id.sub_button_image);
        subImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(selectedType == OptionType.CHECKBOX)
                    addOptionFrom(OptionType.CHECKBOX_IMAGE);
                else
                    addOptionFrom(OptionType.RADIOBUTTON_IMAGE);

                buttonTypeLayout.setVisibility(View.GONE);
            }
        });

        imageView = viewGroup.findViewById(R.id.imageView);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGallery(R.id.imageView);
            }
        });
        return viewGroup;
    }

    private void addOptionFrom(int type) {
        OptionForm optionForm = new OptionForm(getContext(), type);
        optionFormArrayList.add(optionForm);
        optionForm.setOptionFormArrayList(optionFormArrayList);
        LinearLayout container = viewGroup.findViewById(R.id.container);
        container.addView(optionForm);
    }

    @Override
    public void onBackPressed() {
        buttonTypeLayout.setVisibility(View.GONE);
    }

    public void openGallery(int imgId) {
        position = imgId;
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, 101);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 0) {
            if (resultCode == Activity.RESULT_OK) {
                try {
                    // 선택한 이미지에서 비트맵 생성
                    InputStream inputStream = getActivity().getContentResolver().openInputStream(data.getData());
                    Bitmap image = BitmapFactory.decodeStream(inputStream);
                    if(position == R.id.imageView)
                        imageView = viewGroup.findViewById(R.id.imageView);
                    else
                        imageView = (ImageView) viewGroup.findViewById(position);   //viewGroup 맞는지 실행 확인 필요
                    imageView.setImageBitmap(image);
                    inputStream.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            else if (resultCode == Activity.RESULT_CANCELED) {
                Toast.makeText(getContext(), "사진 선택 취소", Toast.LENGTH_LONG).show();
            }
        }
    }
}
