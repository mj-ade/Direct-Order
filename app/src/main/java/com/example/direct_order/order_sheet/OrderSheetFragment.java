package com.example.direct_order.order_sheet;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.direct_order.R;

import java.io.InputStream;
import java.util.ArrayList;

public class OrderSheetFragment extends Fragment implements com.example.direct_order.onBackPressedListener, View.OnTouchListener {
    private ArrayList<OptionForm> optionFormArrayList = new ArrayList<>();
    static int position;

    ViewGroup viewGroup;
    LinearLayout container, buttonTypeLayout;
    RelativeLayout touchPanel;
    ImageView imageView;
    int selectedType;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup con, @Nullable Bundle savedInstanceState) {
        viewGroup = (ViewGroup) inflater.inflate(R.layout.fragment_optionsheet, con, false);

        container = viewGroup.findViewById(R.id.container);
        buttonTypeLayout = viewGroup.findViewById(R.id.sub_button_layout); //sub button panel
        touchPanel = viewGroup.findViewById(R.id.touch_panel);

        Button saveButton = viewGroup.findViewById(R.id.save_button);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (con.getChildCount() == 1){
                    Toast.makeText(getContext(), "추가한 옵션이 없습니다", Toast.LENGTH_SHORT).show();
                }
                else {
                    // DB에 저장
                    Toast.makeText(getContext(), "저장되었습니다", Toast.LENGTH_SHORT).show();
                }
            }
        });

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
        container.addView(optionForm);

        if (type != OptionType.CALENDAR) {
            addOptionPositionDescription();
        }
    }

    private void addOptionPositionDescription() {
        LinearLayout linearLayout = new LinearLayout(getContext());
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        linearLayout.setLayoutParams(layoutParams);

        Button button = new Button(getContext());
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(dpToPx(getContext(), 28), dpToPx(getContext(), 28));
        button.setLayoutParams(lp);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                touchPanel.removeView(linearLayout);
            }
        });

        TextView textView = new TextView(getContext());
        textView.setText("option" + optionFormArrayList.size());
        textView.setTextColor(Color.BLACK);
        textView.setTextSize(18);
        textView.setLayoutParams(layoutParams);
        textView.setPadding(dpToPx(getContext(), 4), dpToPx(getContext(), 4), dpToPx(getContext(), 4), dpToPx(getContext(), 4));

        linearLayout.addView(button);
        linearLayout.addView(textView);

        linearLayout.setOnTouchListener(this);
        touchPanel.addView(linearLayout);
    }

    public int dpToPx(Context context, int dp) {
        DisplayMetrics dm = context.getResources().getDisplayMetrics();
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, dm);
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


    @Override
    public boolean onTouch(View v, MotionEvent event) {
        int parentWidth = ((ViewGroup) v.getParent()).getWidth();
        int parentHeight = ((ViewGroup) v.getParent()).getHeight();

        if(event.getAction() == MotionEvent.ACTION_DOWN){

        }
        else if(event.getAction() == MotionEvent.ACTION_MOVE){
            v.setX(v.getX() + event.getX() - v.getWidth()/2);
            v.setY(v.getY() + event.getY() - v.getHeight()/2);
        }
        else if(event.getAction() == MotionEvent.ACTION_UP){
            if (v.getX() < 0)
                v.setX(0);
            else if ((v.getX() + v.getWidth()) > parentWidth)
                v.setX(parentWidth - v.getWidth());

            if (v.getY() < 0)
                v.setY(0);
            else if ((v.getY() + v.getHeight()) > parentHeight)
                v.setY(parentHeight - v.getHeight());
        }
        return true;
    }
}
