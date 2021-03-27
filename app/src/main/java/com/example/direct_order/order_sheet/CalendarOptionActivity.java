package com.example.direct_order.order_sheet;

import android.os.Bundle;
import android.view.View;

public class CalendarOptionActivity extends NewOptionActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        editTextTitle.setBackground(null);
        editTextTitle.setText("픽업일 선택");
        editTextTitle.setTextSize(20);
        editTextTitle.setKeyListener(null);
    }

    @Override
    protected void setOption() {
        super.setOption();
        previewLayout.setVisibility(View.GONE);
        previewContentLayout.setVisibility(View.GONE);
        contentsLayout.setVisibility(View.GONE);
    }

    @Override
    protected void setStoredPreviews() {
        return;
    }

    @Override
    protected String setPreview(int number) {
        return "";
    }

    @Override
    protected String setPreviewDescription() {
        return "";
    }

    @Override
    protected void setPreviewSticker(int index, int parentIndex, String previewDesc) {

    }

    @Override
    protected void addStickerView(int index, int parentIndex, String previewDesc) {

    }
}