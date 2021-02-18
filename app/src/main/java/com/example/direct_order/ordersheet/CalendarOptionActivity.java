package com.example.direct_order.ordersheet;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Spinner;

public class CalendarOptionActivity extends NewOptionActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
    protected String setPreview() {
        return "";
    }

    @Override
    protected String setPreviewDescription() {
        return "";
    }

    @Override
    protected void setPreviewSticker(int index, String previewDesc) {

    }

    @Override
    protected void addStickerView(int index, String previewDesc) {

    }
}