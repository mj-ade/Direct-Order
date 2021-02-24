package com.example.direct_order.ordersheet;

import android.os.Bundle;
import android.view.View;

public class CalendarOptionActivity extends NewOptionActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getEditTextTitle().setBackground(null);
        getEditTextTitle().setText("픽업일");
        getEditTextTitle().setTextSize(20);
        getEditTextTitle().setKeyListener(null);
    }

    @Override
    protected void setOption() {
        super.setOption();
        getPreviewLayout().setVisibility(View.GONE);
        getPreviewContentLayout().setVisibility(View.GONE);
        getContentsLayout().setVisibility(View.GONE);
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