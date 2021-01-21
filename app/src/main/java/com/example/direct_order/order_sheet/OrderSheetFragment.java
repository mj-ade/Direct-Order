package com.example.direct_order.order_sheet;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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

    ViewGroup viewGroup;
    LinearLayout buttonTypeLayout;
    int selectedType;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        viewGroup = (ViewGroup) inflater.inflate(R.layout.fragment_optionsheet, container, false);

        buttonTypeLayout = viewGroup.findViewById(R.id.sub_button_layout); //sub button panel

        Button txtButton = viewGroup.findViewById(R.id.text_button);
        txtButton.setOnClickListener(new View.OnClickListener() {
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
                //버튼 누르면 갤러리 창으로 이동하고 바로 이미지 추가
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


    /*@Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 0) {
            if (resultCode == RESULT_OK) {
                try {
                    // 선택한 이미지에서 비트맵 생성
                    InputStream inputStream = getContentResolver().openInputStream(data.getData());
                    Bitmap image = BitmapFactory.decodeStream(inputStream);
                    inputStream.close();
                    imageView.setImageBitmap(image);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(this, "사진 선택 취소", Toast.LENGTH_LONG).show();
            }
        }
    }*/
}
