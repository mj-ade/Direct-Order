package com.example.direct_order.order_sheet;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;

public class StickerImageView extends StickerView {
    private ImageView iv_main;

    public StickerImageView(Context context) {
        super(context);
    }

    public StickerImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public StickerImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public View getMainView() {
        if (iv_main == null) {
            iv_main = new ImageView(getContext());
            iv_main.setScaleType(ImageView.ScaleType.FIT_XY);
        }
        return iv_main;
    }

    public ImageView getIv_main() {
        return iv_main;
    }

    public void setIv_main(ImageView iv_main) {
        this.iv_main = iv_main;
    }

    public void setImageUri(Uri uri) {
        iv_main.setImageURI(uri);
    }

    public void setImageResource(int res_id) {
        iv_main.setImageResource(res_id);
    }

    public void setImageDrawable(Drawable drawable) {
        iv_main.setImageDrawable(drawable);
    }
}
