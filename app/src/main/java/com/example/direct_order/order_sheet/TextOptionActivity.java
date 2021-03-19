package com.example.direct_order.order_sheet;

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

        getRadio02().setTag("text");
        getRadio02().setText("텍스트");
        getRadio03().setVisibility(View.GONE);
        getPreviewContentLayout().setVisibility(View.VISIBLE);
        getEditTextPreview().setVisibility(View.VISIBLE);
        getImageViewPreview().setVisibility(View.GONE);
        getContentsLayout().setVisibility(View.GONE);
    }

    @Override
    protected String setPreviewDescription() {
        String previewDesc = getEditTextPreview().getText().toString();
        if (getRadio01().isChecked()) {
            if (!previewDesc.trim().isEmpty()) {
                Toast.makeText(this, "미리보기 추가를 선택하지 않았습니다.\n 내용 입력 창을 비워주세요.", Toast.LENGTH_LONG).show();
                return null;
            }
        }
        else if (getRadio02().isChecked()) {
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