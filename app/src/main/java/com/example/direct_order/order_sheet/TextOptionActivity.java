package com.example.direct_order.order_sheet;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

public class TextOptionActivity extends NewOptionActivity {
    private StickerView stickerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void setOption() {
        super.setOption();

        radio02.setTag("text");
        radio02.setText("텍스트");
        radio03.setVisibility(View.GONE);
        previewContentLayout.setVisibility(View.VISIBLE);
        editTextPreview.setVisibility(View.VISIBLE);
        imageViewPreview.setVisibility(View.GONE);
        contentsLayout.setVisibility(View.GONE);
    }

    @Override
    protected String setPreviewDescription() {
        String previewDesc = editTextPreview.getText().toString();
        if (radio01.isChecked()) {
            if (!previewDesc.trim().isEmpty()) {
                Toast.makeText(this, "미리보기 추가를 선택하지 않았습니다.\n 내용 입력 창을 비워주세요.", Toast.LENGTH_LONG).show();
                return null;
            }
        }
        else if (radio02.isChecked()) {
            if (previewDesc.trim().isEmpty()) {
                Toast.makeText(this, "텍스트 미리보기 추가를 선택하였습니다.\n 내용을 입력해주세요.", Toast.LENGTH_LONG).show();
                return null;
            }
        }
        return previewDesc;
    }

    @Override
    protected void addStickerView(int index, int parentIndex, String previewDesc) {
        stickerView = new StickerTextView(getApplicationContext());
        ((StickerTextView) stickerView).setText(previewDesc);
        OrderSheetActivity.stickerPreviews[index] = stickerView;
        OrderSheetActivity.touchPanel.addView(OrderSheetActivity.stickerPreviews[index]);
    }
}