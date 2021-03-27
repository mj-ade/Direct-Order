package com.example.direct_order.order_sheet;

import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.bumptech.glide.request.target.Target;
import com.example.direct_order.GlideApp;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class ImageOptionActivity extends NewOptionActivity {
    private StickerView stickerView;
    Uri imgUri;
    String test;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void setOption() {
        super.setOption();

        radio02.setTag("cus_image");
        radio02.setText("이미지");
        radio03.setVisibility(View.GONE);
        previewContentLayout.setVisibility(View.VISIBLE);
        editTextPreview.setVisibility(View.GONE);
        imageViewPreview.setVisibility(View.VISIBLE);
        contentsLayout.setVisibility(View.GONE);

        imageViewPreview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setImageView(imageViewPreview);
                pickFromGallery();
            }
        });
    }

    @Override
    protected void retrievePreviewDesc() {
        StorageReference ref = FirebaseStorage.getInstance().getReference(OrderSheetActivity.option.getPreviewDesc());
        if (ref != null) {
            GlideApp.with(getApplicationContext())
                    .load(ref)
                    .override(Target.SIZE_ORIGINAL)
                    .into(imageViewPreview);
        }
    }

    @Override
    protected String setPreviewDescription() {
        String previewDesc = "";
        imgUri = getResultUri();

        if (imgUri != null)
            previewDesc = uploadImage(imgUri, "option_photo/");  //storage의 이미지 위치
        else
            previewDesc = OrderSheetActivity.isUpdate ? OrderSheetActivity.option.getPreviewDesc() : "";

        if (previewDesc.trim().isEmpty()) {
            Toast.makeText(this, "기본 이미지를 넣어주세요", Toast.LENGTH_SHORT).show();
            return null;
        }
        return test = previewDesc;
    }

    @Override
    protected void addStickerView(int index, int parentIndex, String previewDesc) {
        stickerView = new StickerImageView(getApplicationContext());
        if (OrderSheetActivity.isUpdate) {
            StorageReference ref = FirebaseStorage.getInstance().getReference(previewDesc);
            GlideApp.with(getApplicationContext())
                    .load(ref)
                    .override(Target.SIZE_ORIGINAL)
                    .into(((StickerImageView) stickerView).getIv_main());
        }
        else
            ((StickerImageView) stickerView).setImageUri(imgUri);
        ((StickerImageView) stickerView).getIv_main().setTag(previewDesc);
        OrderSheetActivity.stickerPreviews[index] = stickerView;
        OrderSheetActivity.touchPanel.addView(OrderSheetActivity.stickerPreviews[index]);
    }

    @Override
    protected void changePreviewDesc(int index, String previewDesc) {
        StorageReference ref = FirebaseStorage.getInstance().getReference(previewDesc);
        GlideApp.with(getApplicationContext())
                .load(ref)
                .override(Target.SIZE_ORIGINAL)
                .into(((StickerImageView) OrderSheetActivity.stickerPreviews[index]).getIv_main());
        ((StickerImageView) OrderSheetActivity.stickerPreviews[index]).getIv_main().setTag(previewDesc);
    }
}