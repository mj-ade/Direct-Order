package com.example.direct_order.order_sheet;

import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.bumptech.glide.request.target.Target;
import com.example.direct_order.GlideApp;
import com.example.direct_order.R;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.StringTokenizer;

import static com.example.direct_order.order_sheet.OrderSheetActivity.dpToPx;

public class CompoundImageOptionActivity extends NewOptionActivity {
    static Uri[] uris = new Uri[10];
    static int clickedPosition;
    private ArrayList<ImageView> contentsList = new ArrayList<>();
    private StickerView stickerView;
    private StringTokenizer st;
    private String contents = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void setOption() {
        super.setOption();

        radio02.setTag("circle");
        radio02.setText("원형");
        radio03.setVisibility(View.VISIBLE);

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                dpToPx(this, 24));
        previewContentLayout.setLayoutParams(layoutParams);
        previewContentLayout.setVisibility(View.INVISIBLE);
        contentsLayout.setVisibility(View.VISIBLE);
    }

    @Override
    protected void setPreviewRadioButton() {
        if (optionType == OptionType.RADIOBUTTON_IMAGE) {
            radio02.setTag("cus_image");
            radio02.setText("이미지");
            radio03.setVisibility(View.GONE);
        }
    }

    @Override
    protected void retrievePreviewDesc() {
        super.retrievePreviewDesc();
        st = new StringTokenizer(OrderSheetActivity.option.getContent(), "&");
    }

    @Override
    protected void addContents() {
        for (int i = 0; i < numOfOption; i++) {
            ImageView imageView = new ImageView(this);
            imageView.setTag("optionImg"+"_"+i);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    dpToPx(getApplicationContext(), 160));
            layoutParams.bottomMargin = dpToPx(getApplicationContext(), 20);
            imageView.setLayoutParams(layoutParams);
            imageView.setAdjustViewBounds(true);
            imageView.setScaleType(ImageView.ScaleType.FIT_START);

            int currentImg = i;
            if (OrderSheetActivity.isUpdate) {
                StorageReference ref = FirebaseStorage.getInstance().getReference(st.nextToken());
                GlideApp.with(getApplicationContext())
                        .load(ref)
                        .override(Target.SIZE_ORIGINAL)
                        .into(imageView);
            }
            else
                imageView.setImageDrawable(getDrawable(R.drawable.ic_add_photo));  // 개발자 기본 제공 이미지

            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    clickedPosition = currentImg;
                    setImageView(imageView);
                    pickFromGallery();
                    setOptionUriSetupListener(new OnOptionUriSetupListener() {
                        @Override
                        public void onOptionUriSetup(Uri[] uris) {
                            uris[clickedPosition] = getResultUri();
                        }
                    });
                }
            });
            contentsContainer.addView(imageView);
            contentsList.add(imageView);
        }
    }

    @Override
    protected String setContents() {
        if (OrderSheetActivity.isUpdate)
            st = new StringTokenizer(OrderSheetActivity.option.getContent(), "&");

        for (int i = 0; i < numOfOption; i++) {
            String content = "";
            if (uris[i] == null)
                content = (OrderSheetActivity.isUpdate) ? st.nextToken() : "";
            else
                content = uploadImage(uris[i], "option_photo/");

            if (content.trim().isEmpty()) {
                Toast.makeText(this, "입력하지 않은 항목이 있습니다"+i, Toast.LENGTH_SHORT).show();
                return null;
            }
            contents += content + "&";
        }
        return contents;
    }

    @Override
    protected String setPreviewDescription() {
        if (optionType == OptionType.RADIOBUTTON_IMAGE)
            return new StringTokenizer(contents, "&").nextToken();
        return super.setPreviewDescription();
    }

    @Override
    protected void addStickerView(int index, int parentIndex, String previewDesc) {
        if (optionType == OptionType.CHECKBOX_IMAGE) {
            if (radio02.isChecked()) {
                stickerView = new StickerImageView(getApplicationContext());
                ((StickerImageView) stickerView).setImageDrawable(getDrawable(R.drawable.sticker_preview_circle));
                ((StickerImageView) stickerView).getIv_main().setTag("circle");
            }
            else if (radio03.isChecked()) {
                stickerView = new StickerImageView(getApplicationContext());
                ((StickerImageView) stickerView).setImageDrawable(getDrawable(R.drawable.sticker_preview_square));
                ((StickerImageView) stickerView).getIv_main().setTag("square");
            }
        }
        else {
            if (radio02.isChecked()) {
                stickerView = new StickerImageView(getApplicationContext());
                if (OrderSheetActivity.isUpdate) {
                    StorageReference ref = FirebaseStorage.getInstance().getReference(previewDesc);
                    GlideApp.with(getApplicationContext())
                            .load(ref)
                            .override(Target.SIZE_ORIGINAL)
                            .into(((StickerImageView) stickerView).getIv_main());
                }
                else
                    ((StickerImageView) stickerView).setImageUri(uris[0]);

                ((StickerImageView) stickerView).getIv_main().setTag(previewDesc);
            }
        }
        OrderSheetActivity.stickerPreviews[index] = stickerView;
        OrderSheetActivity.touchPanel.addView(OrderSheetActivity.stickerPreviews[index]);
    }
}