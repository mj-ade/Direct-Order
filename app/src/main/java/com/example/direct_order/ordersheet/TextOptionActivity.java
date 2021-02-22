package com.example.direct_order.ordersheet;

import android.os.Bundle;
import android.view.View;

public class TextOptionActivity extends NewOptionActivity {
    private StickerView stickerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void setOption() {
        super.setOption();

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