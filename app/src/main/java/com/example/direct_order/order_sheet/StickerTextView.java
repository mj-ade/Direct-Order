package com.example.direct_order.order_sheet;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;

public class StickerTextView extends StickerView {
    private AutoResizeTextView tv_main;

    public StickerTextView(Context context) {
        super(context);
    }

    public StickerTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public StickerTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public AutoResizeTextView getTv_main() {
        return tv_main;
    }

    @Override
    public View getMainView() {
        if(tv_main != null)
            return tv_main;

        tv_main = new AutoResizeTextView(getContext());
        tv_main.setTextColor(Color.WHITE);
        tv_main.setGravity(Gravity.CENTER);
        tv_main.setTextSize(100);
        tv_main.setShadowLayer(4, 0, 0, Color.BLACK);
        tv_main.setMaxLines(1);
        LayoutParams params = new LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
        );
        params.gravity = Gravity.CENTER;
        tv_main.setLayoutParams(params);
        if (getIv_width() != null && getIv_height() != null) {
            getIv_width().setVisibility(GONE);
            getIv_height().setVisibility(GONE);
        }
        return tv_main;
    }

    public void setText(String text) {
        if(tv_main != null)
            tv_main.setText(text);
    }

    public String getText() {
        if(tv_main != null)
            return tv_main.getText().toString();
        return null;
    }

    @Override
    protected void onScaling(boolean scaleUp) {
        super.onScaling(scaleUp);
    }

    @Override
    public void setControlItemsHidden(boolean isHidden) {
        super.setControlItemsHidden(isHidden);
        if (getIv_width() != null && getIv_height() != null) {
            getIv_width().setVisibility(GONE);
            getIv_height().setVisibility(GONE);
        }
    }
}
