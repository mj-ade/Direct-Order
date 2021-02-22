package com.example.direct_order.ordersheet;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.bumptech.glide.request.target.Target;
import com.example.direct_order.R;
import com.example.direct_order.productorder.ProductOrderActivity;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.Calendar;
import java.util.StringTokenizer;

import static com.example.direct_order.ordersheet.OrderSheetActivity.dpToPx;
import static com.example.direct_order.productorder.ProductOrderActivity.clicked;
import static com.example.direct_order.productorder.ProductOrderActivity.imageViews;
import static com.example.direct_order.productorder.ProductOrderActivity.isCustomer;
import static com.example.direct_order.productorder.ProductOrderActivity.stickerViews;

public class OptionForm extends LinearLayout {
    private final Calendar mCalendar = Calendar.getInstance();
    private LayoutInflater inflater;
    private int number;
    private int optionType;
    private int numOfOption;
    private String content;
    private String function;
    private String previewDesc;

    public OptionForm(Context context, int number, int optionType, int numOfOption, String content, String function, String previewDesc) {
        super(context);
        this.number = number;
        this.optionType = optionType;
        this.numOfOption = numOfOption;
        this.content = content;
        this.function = function;
        this.previewDesc = previewDesc;
        init(context);
    }

    public void init(Context context) {
        inflater= (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = null;
        if (optionType == OptionType.TEXT) {
            view = (View) inflater.inflate(R.layout.text_container, this, true);
            EditText editText = view.findViewById(R.id.editText);
            editText.setText("");
            if (isCustomer && stickerViews[number-1] != null) {
                editText.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {

                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                        ((StickerTextView) stickerViews[number-1]).getTv_main().setText(editText.getText().toString());
                    }
                });
            }
        }
        else if (optionType == OptionType.IMAGE) {
            view = (View) inflater.inflate(R.layout.image_container, this, true);
            ImageView imageView = view.findViewById(R.id.imageView);
            StorageReference ref = FirebaseStorage.getInstance().getReference(previewDesc);

            if (!isCustomer || (isCustomer && !clicked[number-1])) {
                imageViews[number-1] = imageView;
                GlideApp.with(context)
                        .load(ref)
                        .override(Target.SIZE_ORIGINAL)
                        .into(imageView);
            }

            if (isCustomer) {
                imageView.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        ((ProductOrderActivity) getContext()).openGallery(number);
                    }
                });

                if (clicked[number-1])
                    imageView.setImageDrawable(imageViews[number-1].getDrawable());
            }
        }
        else if (optionType == OptionType.CHECKBOX_TEXT || optionType == OptionType.CHECKBOX_IMAGE) {
            view = (View) inflater.inflate(R.layout.compoundbutton_container, this, true);
            RadioGroup radioGroup = view.findViewById(R.id.radio_group);

            StringTokenizer st = new StringTokenizer(content, "&");

            for (int i = 0; i < numOfOption; i++) {
                CheckBox checkBox = new CheckBox(context);
                if (optionType % 2 == 0) {
                    checkBox.setText(st.nextToken());
                    radioGroup.addView(checkBox);
                }
                else {
                    setCompoundButtonImage(context, st, radioGroup, checkBox);
                }
            }
        }
        else if (optionType == OptionType.RADIOBUTTON_TEXT || optionType == OptionType.RADIOBUTTON_IMAGE) {
            view = (View) inflater.inflate(R.layout.compoundbutton_container, this, true);
            RadioGroup radioGroup = view.findViewById(R.id.radio_group);

            StringTokenizer st = new StringTokenizer(content, "&");

            for (int i = 0; i < numOfOption; i++) {
                RadioButton radioButton = new RadioButton(context);

                if (optionType % 2 == 0) {
                    String s = st.nextToken();
                    if (!function.equals("") && !function.equals("func1"))  // 기능 추가 시
                        radioButton.setText(s.substring(0, s.indexOf(':')));
                    else
                        radioButton.setText(s);
                    radioGroup.addView(radioButton);
                }
                else {
                    setCompoundButtonImage(context, st, radioGroup, radioButton);
                }

                if (isCustomer) {
                    int finalI = i;
                    radioButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton button, boolean b) {
                            if (button.isChecked())
                                ProductOrderActivity.myVar.setValue(number, finalI);
                        }
                    });
                }
            }
        }
        else if (optionType == OptionType.CALENDAR) {
            view = (View) inflater.inflate(R.layout.calendar_container, this, true);
            Button dateButton = view.findViewById(R.id.date);
            TextView dateText = findViewById(R.id.date_textView);
            Button timeButton = view.findViewById(R.id.time);
            TextView timeText = findViewById(R.id.time_textView);

            dateButton.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    displayDate(dateText);
                    timeText.setText("Time");
                }
            });

            timeButton.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!dateText.getText().toString().substring(0, 1).equals("2"))
                        Toast.makeText(context, "날짜를 먼저 선택해주세요", Toast.LENGTH_SHORT).show();
                    else
                        displayTime(timeText);
                }
            });
        }
    }

    private void setCompoundButtonImage(Context context, StringTokenizer st, RadioGroup radioGroup, CompoundButton compoundButton) {
        ImageView imageView = new ImageView(context);
        LayoutParams layoutParams = new LayoutParams(dpToPx(context, 180), LayoutParams.WRAP_CONTENT);
        layoutParams.leftMargin = dpToPx(context, 20);
        layoutParams.bottomMargin = dpToPx(context, 20);
        imageView.setLayoutParams(layoutParams);
        imageView.setScaleType(ImageView.ScaleType.FIT_START);
        StorageReference ref = FirebaseStorage.getInstance().getReference(st.nextToken());
        GlideApp.with(context)
                .load(ref)
                .override(Target.SIZE_ORIGINAL)
                .into(imageView);
        radioGroup.addView(compoundButton);
        radioGroup.addView(imageView);
    }

    private void displayDate(TextView dateText) {
        int todayYear = mCalendar.get(Calendar.YEAR);
        int todayMonth = mCalendar.get(Calendar.MONTH);
        int todayDate = mCalendar.get(Calendar.DATE);

        Calendar minDate = Calendar.getInstance();
        Calendar maxDate = Calendar.getInstance();

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                getContext(),
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        dateText.setText(String.format("%02d/%02d/%02d", year, month+1, dayOfMonth));
                    }
                },
                todayYear, todayMonth, todayDate);

        minDate.set(todayYear, todayMonth, todayDate);
        datePickerDialog.getDatePicker().setMinDate(minDate.getTimeInMillis());

        maxDate.set(todayYear, todayMonth + 1,todayDate);
        datePickerDialog.getDatePicker().setMaxDate(maxDate.getTimeInMillis());

        datePickerDialog.setMessage("날짜 선택");
        datePickerDialog.show();
    }

    private void displayTime(TextView timeText) {
        int myHour = mCalendar.get(Calendar.HOUR_OF_DAY);
        int myMinute = mCalendar.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new IntervalTimePickerDialog(getContext(), new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                timeText.setText(String.format("%02d:%02d", hourOfDay, minute));
            }
        }, myHour, myMinute, true);
        timePickerDialog.setMessage("시간 선택");
        timePickerDialog.show();
    }
}

