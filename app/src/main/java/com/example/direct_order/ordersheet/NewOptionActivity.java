package com.example.direct_order.ordersheet;

import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.direct_order.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;


public abstract class NewOptionActivity extends ImageCropActivity {
    private Spinner spinner;
    private EditText editTextTitle;
    private EditText editTextDesc;
    private LinearLayout functionLayout;
    private RadioGroup functionRadioGroup;
    private LinearLayout previewLayout;
    private RadioGroup radioGroup;
    private RadioButton radio01, radio02, radio03, radio04;
    private LinearLayout previewContentLayout;
    private EditText editTextPreview;
    private ImageView imageView;
    private LinearLayout contentsLayout;
    private LinearLayout contentsContainer;
    private ArrayAdapter<Integer> arrayAdapter;

    private int numOfOption, optionType;
    private boolean isUploaded;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_option);

        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_back);

        ArrayList<Integer> optionNums = new ArrayList<>();
        for (int i = 1; i <= 20; i++)
            optionNums.add(i);
        arrayAdapter = new ArrayAdapter<Integer>(this, android.R.layout.simple_spinner_dropdown_item, optionNums) {
            @NonNull
            @Override
            public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                TextView textView = (TextView) super.getView(position, convertView, parent);
                textView.setTextColor(Color.BLACK);
                return textView;
            }
        };
        spinner = findViewById(R.id.spinner);
        spinner.setAdapter(arrayAdapter);
        editTextTitle = findViewById(R.id.edit_text_title);
        editTextDesc = findViewById(R.id.edit_text_desc);
        functionLayout = findViewById(R.id.function_layout);
        functionRadioGroup = findViewById(R.id.radio_group_function);
        previewLayout = findViewById(R.id.preview_layout);
        radioGroup = findViewById(R.id.radio_group);
        radio01 = findViewById(R.id.radio01);
        radio02 = findViewById(R.id.radio02);
        radio03 = findViewById(R.id.radio03);
        radio04 = findViewById(R.id.radio04);
        previewContentLayout = findViewById(R.id.preview_content_layout);
        editTextPreview = findViewById(R.id.edit_text_preview);
        imageView = findViewById(R.id.input_imageView);
        contentsLayout = findViewById(R.id.contents_layout);
        contentsContainer = findViewById(R.id.contents_container);

        setOption();

        if (OrderSheetActivity.isUpdate) {
            setTitle("Update option");
            numOfOption = OrderSheetActivity.option.getNumOfOption();
            optionType = OrderSheetActivity.option.getType();
            spinner.setSelection(OrderSheetActivity.option.getNumber() - 1);
            editTextTitle.setText(OrderSheetActivity.option.getTitle());
            editTextDesc.setText(OrderSheetActivity.option.getDesc());
            functionLayout.setVisibility(View.GONE);
            setStoredPreviews();
        }
        else {
            setTitle("Add option");
            numOfOption = OrderSheetActivity.numOfOption;
            optionType = OrderSheetActivity.type;
        }

        if (optionType >= 4 && optionType <= 5)
            functionLayout.setVisibility(View.VISIBLE);
        else
            functionLayout.setVisibility(View.GONE);

        addContents(numOfOption);
    }

    protected void setOption() {
        spinner.setSelection(0);
        editTextTitle.setText("");
        editTextDesc.setText("");
        editTextPreview.setText("");
        imageView.setImageDrawable(getDrawable(R.drawable.c10));

        for (int i = 0; i < functionRadioGroup.getChildCount(); i++) {
            RadioButton rb = (RadioButton) functionRadioGroup.getChildAt(i);
            rb.setChecked(false);
        }

        for (int i = 0; i < radioGroup.getChildCount(); i++) {
            RadioButton rb = (RadioButton) radioGroup.getChildAt(i);
            rb.setChecked(false);
        }
        contentsContainer.removeAllViews();
    }

    protected void addContents(int numOfOption) {}

    protected void setStoredPreviews() {
        String previewType = OrderSheetActivity.option.getPreview();
        for (int i = 0; i < radioGroup.getChildCount(); i++) {
            RadioButton rb = (RadioButton) radioGroup.getChildAt(i);
            if (previewType.equals((String) rb.getTag())) {
                rb.setChecked(true);
                break;
            }
        }
        retrievePreviewDesc();
    }

    protected void retrievePreviewDesc() {
        editTextPreview.setText(OrderSheetActivity.option.getPreviewDesc());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.new_option_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.save_note:
                saveNote();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    protected void saveNote() {
        isUploaded = true;
        int number = (int) spinner.getSelectedItem();

        String title = editTextTitle.getText().toString();
        if (title.trim().isEmpty()) {
            Toast.makeText(this, "제목을 입력해주세요", Toast.LENGTH_SHORT).show();
            return;
        }

        String description = editTextDesc.getText().toString();

        String function = setOptionFunction();
        if (function == null)
            return;

        String preview = setPreview();
        if (preview == null)
            return;

        if (isNumberDuplicate(number))
            return;

        String previewDesc = setPreviewDescription();
        if (previewDesc == null) {
            resetNumber(number);
            return;
        }

        String contents = setContents();
        if (contents == null) {
            resetNumber(number);
            return;
        }

        CollectionReference optionRef = FirebaseFirestore.getInstance()
                .collection("markets")
                .document(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .collection("OrderSheet")
                .document("sheet")
                .collection("Options");
        setPreviewSticker(number - 1, previewDesc);

        if(optionType % 2 == 1) {   // 이미지 관련 옵션
            setUploadCompleteListener(new OnUploadCompleteListener() {  //upload image 횟수만큼 call
                @Override
                public void onUploadComplete() {
                    if (isUploaded) {
                        saveOptionToDB(optionRef, number, title, description, contents, function, preview, previewDesc);
                        isUploaded = false;
                    }
                }
            });
        }
        else
            saveOptionToDB(optionRef, number, title, description, contents, function, preview, previewDesc);
        //optionRef.document("question" + number).set(new Option(number, title, description, optionType, numOfOption, contents, function, preview, previewDesc, ""));

        finish();
    }

    private void saveOptionToDB(CollectionReference optionsheetRef, int number, String title, String description, String contents, String function, String preview, String previewDesc) {
        if (OrderSheetActivity.isUpdate) {
            if (radio02.isChecked()) {
                if (!previewDesc.equals(OrderSheetActivity.option.getPreviewDesc()))
                    changePreviewDesc(number - 1, previewDesc);
            }
            optionsheetRef.document(OrderSheetActivity.id).update("number", number, "title", title, "desc", description, "content", contents, "previewDesc", previewDesc);
            Toast.makeText(getApplicationContext(), "옵션이 수정되었습니다", Toast.LENGTH_SHORT).show();
        }
        else {
            optionsheetRef.add(new Option(number, title, description, optionType, numOfOption, contents, function, preview, previewDesc, ""));
            Toast.makeText(getApplicationContext(), "옵션이 추가되었습니다", Toast.LENGTH_SHORT).show();
        }
    }

    protected String setOptionFunction() {
        return "";
    }

    protected String setPreview() {
        String preview = "";
        for (int i = 0; i < radioGroup.getChildCount(); i++) {
            RadioButton rb = (RadioButton) radioGroup.getChildAt(i);
            if (rb.isChecked()) {
                preview = (String) rb.getTag();
                break;
            }
        }
        if (preview.trim().isEmpty()) {
            Toast.makeText(this, "미리보기 추가 여부를 선택하세요.", Toast.LENGTH_LONG).show();
            return null;
        }
        if (OrderSheetActivity.isUpdate && !preview.equals(OrderSheetActivity.option.getPreview())) {
            Toast.makeText(this, "미리보기 타입을 변경할 수 없습니다.\n 새로운 옵션을 추가해주세요.", Toast.LENGTH_LONG).show();
            return null;
        }
        return preview;
    }

    protected String setPreviewDescription() {
        String previewDesc = editTextPreview.getText().toString();
        if (radio02.isChecked()) {
            if (previewDesc.trim().isEmpty()) {
                Toast.makeText(this, "텍스트 미리보기 추가를 선택하였습니다.\n 내용을 입력해주세요.", Toast.LENGTH_LONG).show();
                return null;
            }
        }
        else {
            if (!previewDesc.trim().isEmpty()) {
                Toast.makeText(this, "텍스트 미리보기 추가를 선택하지 않았습니다.\n 내용 입력 창을 비워주세요.", Toast.LENGTH_LONG).show();
                return null;
            }
        }
        return previewDesc;
    }

    protected String setContents() {
        return "";
    }

    private boolean isNumberDuplicate(int number) {
        int index = number - 1;
        if (OrderSheetActivity.isUpdate) {
            if (number != OrderSheetActivity.option.getNumber()) { // 문항 번호가 바뀌었을 때만 중복 확인
                if (OrderSheetActivity.numberDup[index]) {
                    Toast.makeText(getApplicationContext(), "중복 번호", Toast.LENGTH_SHORT).show();
                    return true;
                }
                else {
                    OrderSheetActivity.numberDup[index] = true;
                    OrderSheetActivity.numberDup[OrderSheetActivity.option.getNumber()-1] = false;
                    OrderSheetActivity.stickerPreviews[index] = OrderSheetActivity.stickerPreviews[OrderSheetActivity.option.getNumber()-1];
                    OrderSheetActivity.stickerPreviews[OrderSheetActivity.option.getNumber()-1] = null;
                }
            }
        }
        else {
            if (OrderSheetActivity.numberDup[index]) {
                Toast.makeText(getApplicationContext(), "중복 번호+", Toast.LENGTH_SHORT).show();
                return true;
            }
            else
                OrderSheetActivity.numberDup[index] = true;
        }
        return false;
    }

    private void resetNumber(int number) {
        int index = number - 1;
        if(OrderSheetActivity.isUpdate) {
            OrderSheetActivity.numberDup[OrderSheetActivity.option.getNumber()-1] = true;
            OrderSheetActivity.stickerPreviews[OrderSheetActivity.option.getNumber()-1] = OrderSheetActivity.stickerPreviews[index];
            OrderSheetActivity.stickerPreviews[index] = null;
        }
        OrderSheetActivity.numberDup[index] = false;
    }

    protected void setPreviewSticker(int index, String previewDesc) {
        if (OrderSheetActivity.isUpdate) {
            if(!radio01.isChecked()) {
                if (OrderSheetActivity.stickerPreviews[index] == null) // 이전에 저장하고 종료하지 않아서 새로 생성함
                    addStickerView(index, previewDesc);
            }
        }
        else {
            if (radio02.isChecked() || radio03.isChecked() || radio04.isChecked())
                addStickerView(index, previewDesc);
        }
    }

    protected void changePreviewDesc(int index, String previewDesc) {
        ((StickerTextView) OrderSheetActivity.stickerPreviews[index]).setText(previewDesc);
    }

    protected abstract void addStickerView(int index, String previewDesc);

    public RadioButton getRadio02() {
        return radio02;
    }

    public RadioButton getRadio03() {
        return radio03;
    }

    public RadioButton getRadio04() {
        return radio04;
    }

    public EditText getEditTextPreview() {
        return editTextPreview;
    }

    public ImageView getImageViewPreview() {
        return imageView;
    }

    public RadioGroup getFunctionRadioGroup() {
        return functionRadioGroup;
    }

    public LinearLayout getPreviewLayout() {
        return previewLayout;
    }

    public LinearLayout getPreviewContentLayout() {
        return previewContentLayout;
    }

    public LinearLayout getContentsLayout() {
        return contentsLayout;
    }

    public LinearLayout getContentsContainer() {
        return contentsContainer;
    }

    public int getNumOfOption() {
        return numOfOption;
    }

    public int getOptionType() {
        return optionType;
    }
}
