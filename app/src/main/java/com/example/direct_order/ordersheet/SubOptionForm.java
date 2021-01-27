package com.example.direct_order.ordersheet;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.example.direct_order.R;

public class SubOptionForm extends LinearLayout {
    public static ViewGroup.LayoutParams optionImageSize = new ViewGroup.LayoutParams(450, 450);
    public static ViewGroup.LayoutParams optionTextButtonSize = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, 160);
    public static ViewGroup.LayoutParams optionImageButtonSize = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, 450);

    InputMethodManager inputMethodManager;
    LinearLayout sub_con, inputPanel;
    LayoutInflater inflater;
    EditText editText_num;  // 문항명 추가
    Button inputButton;
    int count;
    private int optionType;

    public SubOptionForm(Context context, int opt){
        super(context);
        this.optionType = opt;
        init(context);
    }

    public int getOptionType() {
        return optionType;
    }

    public void init(Context context) {
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = (View) inflater.inflate(R.layout.option_container, this, true);

        inputMethodManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        sub_con = view.findViewById(R.id.sub_con);

        if (optionType >= 2 && optionType <= 5) {
            displayOptionNumberInputPanel();
            inputButton.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (verifyNumberOfOptionInput())
                        displayOption(optionType);
                }
            });
        }
        else {
            displayOption(optionType);
        }
    }

    public void displayOption(int type){
        switch (type) {
            case 0:
                EditText editText = new EditText(getContext());
                editText.setText("hi");
                sub_con.addView(editText);
                Toast.makeText(getContext(),"testetwerew",Toast.LENGTH_SHORT).show();
                break;
            case 1:
                ImageView imageView = new ImageView(getContext());
                imageView.setImageResource(R.drawable.ic_launcher_background);
                imageView.setId(imageView.generateViewId());
                imageView.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ((OrderSheetActivity) getContext()).openGallery(imageView.getId());
                        OrderSheetActivity.position =imageView.getId();
                    }
                });

                Toast.makeText(getContext(),""+optionImageSize, Toast.LENGTH_SHORT).show();
                imageView.setLayoutParams(optionImageSize);    //ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                sub_con.addView(imageView);
                break;
            case 2: case 3: case 4: case 5:
                displayCompoundButton(type);
                break;
        }
    }

    public void displayCompoundButton(int type) {
        LinearLayout buttonContainer;
        if (type <= OptionType.CHECKBOX_IMAGE) {
            buttonContainer = new LinearLayout(getContext());
            buttonContainer.setOrientation(VERTICAL);
        }
        else {
            buttonContainer = new RadioGroup(getContext());
        }
        LinearLayout descriptionContainer = new LinearLayout(getContext()); // desc 담을 layout
        descriptionContainer.setOrientation(VERTICAL);

        for (int i = 0; i < count; i++) {
            CompoundButton button;
            int buttonID;
            if (type <= OptionType.CHECKBOX_IMAGE) {
                button = new CheckBox(getContext());
                buttonID = button.generateViewId();
                button.setId(buttonID);
            }
            else {
                button = new RadioButton(getContext());
                buttonID = button.generateViewId();
                // radioButton.setText("new" + i);
                button.setId(buttonID);
            }

            if (type % 2 == 0) {
                button.setLayoutParams(optionTextButtonSize);
                EditText editText = new EditText(getContext());
                editText.setId(buttonID + 1000);
                descriptionContainer.addView(editText);
            }
            else {
                button.setLayoutParams(optionImageButtonSize);
                ImageView imageView = new ImageView(getContext());
                imageView.setImageResource(R.drawable.ic_launcher_background);
                imageView.setId(buttonID + 100000);
                imageView.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ((OrderSheetActivity) getContext()).openGallery(imageView.getId());
                        OrderSheetActivity.position = imageView.getId();
                    }
                });
                imageView.setLayoutParams(optionImageSize);    //ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                descriptionContainer.addView(imageView);
            }
            buttonContainer.addView(button);
        }
        sub_con.addView(buttonContainer);
        sub_con.addView(descriptionContainer);
    }

    public void displayOptionNumberInputPanel() {
        inputPanel = findViewById(R.id.input_panel);
        inputPanel.setVisibility(VISIBLE);
        editText_num = findViewById(R.id.editText);
        inputButton = findViewById(R.id.input_button);
    }

    public boolean verifyNumberOfOptionInput() {
        String editTextValue= editText_num.getText().toString();
        try {
            count = Integer.parseInt(editTextValue);
            inputPanel.setVisibility(GONE);
        } catch (NumberFormatException e){
            if(editTextValue.equals(""))
                Toast.makeText(getContext(), "옵션 개수를 입력해주세요", Toast.LENGTH_SHORT).show();
            else
                Toast.makeText(getContext(), "유효한 값을 입력해주세요", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void hideKeyboard() {
        inputMethodManager.hideSoftInputFromWindow(editText_num.getWindowToken(), 0);
    }
}
