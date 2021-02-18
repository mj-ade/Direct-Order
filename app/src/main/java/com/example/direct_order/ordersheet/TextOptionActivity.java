package com.example.direct_order.ordersheet;

import android.os.Bundle;
import android.view.View;

import com.example.direct_order.MainActivity;

public class TextOptionActivity extends NewOptionActivity {
    private StickerView stickerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void setOption() {
        super.setOption();
        getImageViewPreview().setVisibility(View.GONE);
        getEditTextPreview().setVisibility(View.VISIBLE);
        getRadio02().setTag("text");
        getRadio02().setText("텍스트");
        getRadio03().setVisibility(View.GONE);
        getRadio04().setVisibility(View.GONE);
        getContentsLayout().setVisibility(View.GONE);
    }

    @Override
    protected void addStickerView(int index, String previewDesc) {
        stickerView = new StickerTextView(getApplicationContext());
        ((StickerTextView) stickerView).setText(previewDesc);
        OrderSheetActivity.stickerPreviews[index] = stickerView;
        OrderSheetActivity.touchPanel.addView(OrderSheetActivity.stickerPreviews[index]);
    }
}