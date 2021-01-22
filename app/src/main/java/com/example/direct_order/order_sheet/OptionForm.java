package com.example.direct_order.order_sheet;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Path;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.direct_order.MainActivity;
import com.example.direct_order.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class OptionForm extends LinearLayout {
    public static ViewGroup.LayoutParams optionImageSize = new ViewGroup.LayoutParams(450, 450);
    public static ViewGroup.LayoutParams optionTextButtonSize = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, 160);
    public static ViewGroup.LayoutParams optionImageButtonSize = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, 450);

    InputMethodManager inputMethodManager;
    LinearLayout sub_container, sub_con, inputPanel;
    TextView textView;
    EditText editText_numOfOptions;
    Button inputButton, addButton;
    Spinner spinner;

    private int count;
    private int index;
    private int optionType, subOptionType;
    private ArrayList<OptionForm> optionFormArrayList = new ArrayList<>();
    private ArrayList<OptionForm> SubOptionFormArrayList = new ArrayList<>();

    public OptionForm(Context context, int optionType) {
        super(context);
        this.optionType = optionType;
    }

    public void setOptionFormArrayList(ArrayList<OptionForm> optionFormArrayList) {
        this.optionFormArrayList = optionFormArrayList;
        this.index = optionFormArrayList.size();
        init(getContext());
    }

    private void init(Context context) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View optionFormView = (View) inflater.inflate(R.layout.option_form, this, true);

        inputMethodManager = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        sub_container = findViewById(R.id.sub_container);
        sub_con = findViewById(R.id.sub_con);

        textView = findViewById(R.id.option_number);
        textView.setText(index + ".");

        if (optionType >= OptionType.CHECKBOX_TEXT && optionType <= OptionType.RADIOBUTTON_IMAGE) {
            displayNumberOfOptionInputPanel();
            inputButton.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    hideKeyboard();
                    if (verifyNumberOfOptionInput())
                        displayOption(optionType);
                }
            });
        }
        else {
            displayOption(optionType);
        }

        spinner = findViewById(R.id.spinner);
        addButton = findViewById(R.id.add_button);
        if (optionType != OptionType.CALENDAR) {
            setSpinner();
        }
        else {
            spinner.setVisibility(GONE);
            addButton.setVisibility(GONE);
        }

        addButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                OptionForm subOptionForm = new OptionForm(getContext(), subOptionType);
                SubOptionFormArrayList.add(subOptionForm);
                subOptionForm.setOptionFormArrayList(SubOptionFormArrayList);
                sub_container.setVisibility(GONE);
                subOptionForm.spinner.setVisibility(GONE);
                subOptionForm.addButton.setVisibility(GONE);
                sub_con.addView(subOptionForm);
            }
        });

        Button deleteButton = findViewById(R.id.delete_button);
        deleteButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                ViewGroup parentViewGroup = (ViewGroup) getParent();
                if (parentViewGroup != null) {
                    parentViewGroup.removeView(optionFormView);
                    for (OptionForm optionForm : optionFormArrayList) {
                        if (index < optionForm.getIndex()) { // 선택된 옵션이 리스트에 있는 옵션보다 작은 값이면
                            optionForm.setIndex(optionForm.getIndex() - 1);
                        }
                        ((TextView) optionForm.findViewById(R.id.option_number)).setText(optionForm.getIndex() + ".");
                    }
                    optionFormArrayList.remove(index - 1);
                }
            }
        });
    }

    public void displayOption(int type){
        switch (type) {
            case 0:
                EditText editText = new EditText(getContext());
                sub_container.addView(editText);
                break;
            case 1:
                ImageView imageView = new ImageView(getContext());
                imageView.setImageResource(R.drawable.ic_launcher_background);
                imageView.setId(10000 + getIndex());
                imageView.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        OrderSheetFragment orderSheetFragment = new OrderSheetFragment();
                        orderSheetFragment.openGallery(imageView.getId());
                        OrderSheetFragment.position = imageView.getId();
                    }
                });
                imageView.setLayoutParams(optionImageSize);
                sub_container.addView(imageView);
                break;
            case 2: case 3:
                displayCheckBox(type);
                setOptionDescription(type, 10);
                break;
            case 4: case 5:
                displayRadioButton(type);
                setOptionDescription(type, 1000);
                break;
            case 6:
                CalendarView calendarView = new CalendarView(getContext());
                sub_container.addView(calendarView);
        }
    }

    public void displayCheckBox(int type) {
        LinearLayout linearLayout = new LinearLayout(getContext());
        linearLayout.setOrientation(VERTICAL);
        linearLayout.setBackgroundColor(Color.GREEN);
        for (int i = 0; i < count; i++) {
            CheckBox checkBox = new CheckBox(getContext());
            checkBox.setId(10 + i);
            if (type % 2 == 0)
                checkBox.setLayoutParams(optionTextButtonSize);
            else
                checkBox.setLayoutParams(optionImageButtonSize);
            linearLayout.addView(checkBox);
        }
        sub_container.addView(linearLayout);
    }

    public void displayRadioButton(int type) {
        RadioGroup radioGroup = new RadioGroup(getContext());
        for (int i = 0; i < count; i++) {
            RadioButton radioButton = new RadioButton(getContext());
            radioButton.setText("new" + i);
            radioButton.setId(1000 + i);
            if (type % 2 == 0)
                radioButton.setLayoutParams(optionTextButtonSize);
            else
                radioButton.setLayoutParams(optionImageButtonSize);
            //RadioGroup.LayoutParams layoutParams = new RadioGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            radioGroup.addView(radioButton);    //radioGroup.addView(radioButton, layoutParams);
        }
        sub_container.addView(radioGroup);
    }

    public void setOptionDescription(int type, int typeId) {
        LinearLayout descriptionContainer = new LinearLayout(getContext());
        descriptionContainer.setOrientation(VERTICAL);
        if (type % 2 == 0)
            descriptionContainer = displayOptionDescriptionEditText(descriptionContainer, typeId);
        else
            descriptionContainer = displayOptionDescriptionImageView(descriptionContainer, typeId);
        sub_container.addView(descriptionContainer);
    }

    public LinearLayout displayOptionDescriptionEditText(LinearLayout descriptionContainer, int typeId) {
        for (int i = 0; i < count; i++) {
            EditText editText = new EditText(getContext());
            editText.setId(typeId+i + 100);   // ID = buttonID + 100, buttonID = typeId + i
            descriptionContainer.addView(editText);
        }
        return descriptionContainer;

    }

    public LinearLayout displayOptionDescriptionImageView(LinearLayout descriptionContainer, int typeId) {
        for (int i = 0; i < count; i++) {
            ImageView imageView = new ImageView(getContext());
            imageView.setImageResource(R.drawable.ic_launcher_background);
            imageView.setId(typeId + i + 100000); // ID = buttonID + 100000, buttonID = typeId + i
            imageView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    OrderSheetFragment orderSheetFragment = new OrderSheetFragment();
                    orderSheetFragment.openGallery(imageView.getId());
                    OrderSheetFragment.position = imageView.getId();
                }
            });
            imageView.setLayoutParams(optionImageSize);
            descriptionContainer.addView(imageView);
        }
        return descriptionContainer;
    }

    private void displayNumberOfOptionInputPanel() {
        inputPanel = findViewById(R.id.input_panel);
        inputPanel.setVisibility(VISIBLE);
        editText_numOfOptions = findViewById(R.id.numberOfOptions);
        inputButton = findViewById(R.id.input_button);
    }

    private boolean verifyNumberOfOptionInput() {
        String value = editText_numOfOptions.getText().toString();
        try {
            count = Integer.parseInt(value);
            inputPanel.setVisibility(GONE);
        } catch (NumberFormatException e) {
            if (value.equals(""))
                Toast.makeText(getContext(), "옵션 개수를 입력해주세요", Toast.LENGTH_SHORT).show();
            else
                Toast.makeText(getContext(), "유효한 값을 입력해주세요", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void hideKeyboard() {
        inputMethodManager.hideSoftInputFromWindow(editText_numOfOptions.getWindowToken(), 0);
    }

    public void setSpinner() {
        spinner.setPrompt("옵션 추가");
        List<String> optionList = new ArrayList<>();
        optionList.addAll(Arrays.asList(getResources().getStringArray(R.array.option_array)));
        optionList.add("옵션 추가"); // spinner 생성 시 보이는 항목
        SubOptionSpinnerAdapter adapter = new SubOptionSpinnerAdapter(getContext(), R.layout.support_simple_spinner_dropdown_item, optionList);
        adapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setSelection(adapter.getCount());
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                subOptionType = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }
}
