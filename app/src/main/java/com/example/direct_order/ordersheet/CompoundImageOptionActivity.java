package com.example.direct_order.ordersheet;

import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.Toast;

import com.bumptech.glide.request.target.Target;
import com.example.direct_order.R;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.StringTokenizer;

public class CompoundImageOptionActivity extends NewOptionActivity {
    static Uri[] uris = new Uri[10];
    static int clickedPosition;
    private ArrayList<ImageView> contentsList = new ArrayList<>();
    private StickerView stickerView;
    private StringTokenizer st;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void setOption() {
        super.setOption();
        getImageViewPreview().setVisibility(View.GONE);
        getEditTextPreview().setVisibility(View.VISIBLE);
        getRadio02().setTag("text");
        getRadio02().setText("텍스트");
        getRadio03().setVisibility(View.VISIBLE);
        getRadio04().setVisibility(View.VISIBLE);
        getContentsLayout().setVisibility(View.VISIBLE);
    }

    @Override
    protected void retrievePreviewDesc() {
        super.retrievePreviewDesc();
        st = new StringTokenizer(OrderSheetActivity.option.getContent(), "#");
    }

    @Override
    protected void addContents(int numOfOption) {
        for (int i = 0; i < numOfOption; i++) {
            ImageView imageView = new ImageView(this);
            imageView.setTag("optionImg"+"_"+i);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    OrderSheetActivity.dpToPx(getApplicationContext(), 160));
            layoutParams.bottomMargin = OrderSheetActivity.dpToPx(getApplicationContext(), 20);
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
                imageView.setImageDrawable(getDrawable(R.drawable.c10));  // 개발자 기본 제공 이미지

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
            getContentsContainer().addView(imageView);
            contentsList.add(imageView);
        }
    }

    @Override
    protected String setOptionFunction() {
        String function = "";
        if (getOptionType() >= OptionType.RADIOBUTTON_TEXT && !OrderSheetActivity.isUpdate) {
            for (int i = 0; i < getFunctionRadioGroup().getChildCount(); i++) {
                RadioButton r = (RadioButton) getFunctionRadioGroup().getChildAt(i);
                if (r.isChecked()) {
                    function = (String) r.getTag();
                    break;
                }
            }
            if (function.trim().isEmpty()) {
                Toast.makeText(this, "옵션 기능을 선택하세요.", Toast.LENGTH_LONG).show();
                return null;
            }
        }
        return function;
    }

    @Override
    protected String setContents() {
        if (OrderSheetActivity.isUpdate)
            st = new StringTokenizer(OrderSheetActivity.option.getContent(), "#");

        String contents = "";
        for (int i = 0; i < getNumOfOption(); i++) {
            String content = "";
            if (uris[i] == null)
                content = (OrderSheetActivity.isUpdate) ? st.nextToken() : "";
            else
                content = uploadImage(uris[i], "option_photo/");

            if (content.trim().isEmpty()) {
                Toast.makeText(this, "입력하지 않은 항목이 있습니다"+i, Toast.LENGTH_SHORT).show();
                return null;
            }
            contents += content + "#";
        }
        return contents;
    }

    @Override
    protected void addStickerView(int index, String previewDesc) {
        if (getRadio02().isChecked()) {
            stickerView = new StickerTextView(getApplicationContext());
            ((StickerTextView) stickerView).setText(previewDesc);
        }
        else if (getRadio03().isChecked()) {
            stickerView = new StickerImageView(getApplicationContext());
            ((StickerImageView) stickerView).setImageDrawable(getDrawable(R.drawable.circle));
            ((StickerImageView) stickerView).getIv_main().setTag("circle");
        }
        else if (getRadio04().isChecked()) {
            stickerView = new StickerImageView(getApplicationContext());
            ((StickerImageView) stickerView).setImageDrawable(getDrawable(R.drawable.square));
            ((StickerImageView) stickerView).getIv_main().setTag("square");
        }
        OrderSheetActivity.stickerPreviews[index] = stickerView;
        OrderSheetActivity.touchPanel.addView(OrderSheetActivity.stickerPreviews[index]);
    }
}