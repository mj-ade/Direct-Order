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

public class OrderSheetFragment extends Fragment {
    private ArrayList<OptionForm> optionFormArrayList = new ArrayList<>();

    ViewGroup viewGroup;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        viewGroup = (ViewGroup) inflater.inflate(R.layout.fragment_optionsheet, container, false);

        Button button = viewGroup.findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OptionForm optionFormLayout = new OptionForm(getContext(), optionFormArrayList);
                LinearLayout container = viewGroup.findViewById(R.id.container);
                container.addView(optionFormLayout);
                optionFormArrayList.add(optionFormLayout);
            }
        });

        return viewGroup;
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
