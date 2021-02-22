package com.example.direct_order.ordersheet;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.direct_order.R;

import java.util.ArrayList;
import java.util.StringTokenizer;

public class CompoundTextOptionActivity extends NewOptionActivity {
    private ArrayList<EditText> contentsList = new ArrayList<>();
    private StickerView stickerView;
    private StringTokenizer st;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void setOption() {
        super.setOption();

        getContentsLayout().setVisibility(View.VISIBLE);
    }

    @Override
    protected void retrievePreviewDesc() {
        super.retrievePreviewDesc();
        st = new StringTokenizer(OrderSheetActivity.option.getContent(), "&");
    }

    @Override
    protected void addContents() {
        if (getOptionType() == OptionType.RADIOBUTTON_TEXT) {
            getRadio03().setVisibility(View.VISIBLE);
            getRadio04().setVisibility(View.VISIBLE);
            TextView textView = new TextView(this);
            textView.setText("기능 추가 시 아래의 형식에 맞춰 입력해 주세요.\nex) 색상명:#FFFFFF");
            getContentsContainer().addView(textView);
        }
        for (int i = 0; i < getNumOfOption(); i++) {
            EditText editText = new EditText(this);
            if (OrderSheetActivity.isUpdate)
                editText.setText(st.nextToken());
            else
                editText.setHint("옵션"+ (i + 1));
            getContentsContainer().addView(editText);
            contentsList.add(editText);
        }
    }

    @Override
    protected String setOptionFunction() {
        String function = "";
        if (getOptionType() >= OptionType.RADIOBUTTON_TEXT && !OrderSheetActivity.isUpdate) {
            for (int i = 0; i < getFunctionRadioGroup().getChildCount(); i++) {
                RadioButton r = (RadioButton) getFunctionRadioGroup().getChildAt(i);
                if (r.isChecked()) {
                    function = (String) r.getTag();
                    break;
                }
            }
            if (function.trim().isEmpty()) {
                Toast.makeText(this, "옵션 기능을 선택하세요.", Toast.LENGTH_LONG).show();
                return null;
            }
        }
        return function;
    }

    @Override
    protected String setContents() {
        String contents = "";
        for (int i = 0; i < getNumOfOption(); i++) {
            String content = contentsList.get(i).getText().toString();
            if (content.trim().isEmpty()) {
                Toast.makeText(this, "입력하지 않은 항목이 있습니다", Toast.LENGTH_SHORT).show();
                return null;
            }
            contents += content + "&";
        }
        return contents;
    }

    @Override
    protected void addStickerView(int index, String previewDesc) {
        if (getRadio02().isChecked()) {
            stickerView = new StickerTextView(getApplicationContext());
            ((StickerTextView) stickerView).setText(previewDesc);
        }
        else if (getRadio03().isChecked()) {
            stickerView = new StickerImageView(getApplicationContext());
            ((StickerImageView) stickerView).setImageDrawable(getDrawable(R.drawable.circle));
            ((StickerImageView) stickerView).getIv_main().setTag("circle");
        }
        else if (getRadio04().isChecked()) {
            stickerView = new StickerImageView(getApplicationContext());
            ((StickerImageView) stickerView).setImageDrawable(getDrawable(R.drawable.square));
            ((StickerImageView) stickerView).getIv_main().setTag("square");
        }
        OrderSheetActivity.stickerPreviews[index] = stickerView;
        OrderSheetActivity.touchPanel.addView(OrderSheetActivity.stickerPreviews[index]);
    }
}