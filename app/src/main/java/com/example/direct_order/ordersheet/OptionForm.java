package com.example.direct_order.ordersheet;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
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

import com.bumptech.glide.request.target.Target;
import com.example.direct_order.R;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.Calendar;
import java.util.StringTokenizer;

public class OptionForm extends LinearLayout {
    private final Calendar mCalendar = Calendar.getInstance();
    private LayoutInflater inflater;
    private int optionType;
    private int numOfOption;
    private String content;
    private String previewDesc;

    public OptionForm(Context context, int optionType, int numOfOption, String content, String previewDesc) {
        super(context);
        this.optionType = optionType;
        this.numOfOption = numOfOption;
        this.content = content;
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
        }
        else if (optionType == OptionType.IMAGE) {
            view = (View) inflater.inflate(R.layout.image_container, this, true);
            ImageView imageView = view.findViewById(R.id.imageView);
            StorageReference ref = FirebaseStorage.getInstance().getReference(previewDesc);
            GlideApp.with(context)
                    .load(ref)
                    .override(Target.SIZE_ORIGINAL)
                    .into(imageView);
        }
        else if (optionType >= OptionType.CHECKBOX_TEXT && optionType <= OptionType.RADIOBUTTON_IMAGE) {
            view = (View) inflater.inflate(R.layout.compoundbutton_container, this, true);
            RadioGroup radioGroup = view.findViewById(R.id.radio_group);

            StringTokenizer st = new StringTokenizer(content, "#");

            CompoundButton compoundButton = null;
            // how에 따라 버튼 개수 생성
            for (int i = 0; i < numOfOption; i++) {
                if (optionType <= OptionType.CHECKBOX_IMAGE)
                    compoundButton = new CheckBox(context);
                else
                    compoundButton = new RadioButton(context);

                if (optionType % 2 == 0) {
                    compoundButton.setText(st.nextToken());
                    radioGroup.addView(compoundButton);
                }
                else {
                    ImageView imageView = new ImageView(context);
                    LayoutParams layoutParams = new LayoutParams(OrderSheetActivity.dpToPx(context, 180), LayoutParams.WRAP_CONTENT);
                    layoutParams.leftMargin = OrderSheetActivity.dpToPx(context, 20);
                    layoutParams.bottomMargin = OrderSheetActivity.dpToPx(context, 20);
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
            }
        }
        else if (optionType == OptionType.CALENDAR) {
            view = (View) inflater.inflate(R.layout.calendar_container, this, true);
            Button dateButton = view.findViewById(R.id.date);
            dateButton.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    displayDate();
                }
            });
            Button timeButton = view.findViewById(R.id.time);
            timeButton.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    displayTime();
                }
            });
        }
    }

    private void displayDate() {
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
                        TextView dateText = findViewById(R.id.date_textView);
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

    private void displayTime() {
        int myHour = mCalendar.get(Calendar.HOUR_OF_DAY);
        int myMinute = mCalendar.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new IntervalTimePickerDialog(getContext(), new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                TextView timeText = findViewById(R.id.time_textView);
                timeText.setText(String.format("%02d:%02d", hourOfDay, minute));
            }
        }, myHour, myMinute, true);
        timePickerDialog.setMessage("시간 선택");
        timePickerDialog.show();
    }
}

