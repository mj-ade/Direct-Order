package com.example.direct_order.ordersheet;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.graphics.Color;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;

import com.example.direct_order.R;

import java.util.Calendar;

public class OptionForm extends SubOptionForm {
    final Calendar mCalendar = Calendar.getInstance();
    TextView textView;
    //LayoutInflater inflater;
    LinearLayout linearLayout;

    public OptionForm(Context context, int opt) {
        super(context, opt);
    }

    @Override
    public void init(Context context) {
        super.init(context);

        if (super.getOptionType() != OptionType.CALENDAR) {
            addOptionDescription();
            OptionAdapter.test++;
        }
    }

    @Override
    public void displayOption(int type) {
        super.displayOption(type);

        if (type == OptionType.CALENDAR) {
            LinearLayout calendarLayout = (LinearLayout) inflater.inflate(R.layout.calendar_layout, null);
            sub_con.addView(calendarLayout);

            Button dateButton = calendarLayout.findViewById(R.id.date_button);
            dateButton.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    displayDate();
                }
            });

            Button timeButton = calendarLayout.findViewById(R.id.time_button);
            timeButton.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    displayTime();
                }
            });
        }
    }

    void displayDate() {
        int myYear = mCalendar.get(Calendar.YEAR);
        int myMonth = mCalendar.get(Calendar.MONTH);
        int myDate = mCalendar.get(Calendar.DATE);

        Calendar minDate = Calendar.getInstance();
        Calendar maxDate = Calendar.getInstance();

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                getContext(),
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        TextView dateText = findViewById(R.id.date_textView);
                        dateText.setText(String.format("%02d-%02d-%02d", year, month+1, dayOfMonth));
                    }
                },
                myYear, myMonth, myDate);

        minDate.set(myYear, myMonth, myDate);
        datePickerDialog.getDatePicker().setMinDate(minDate.getTimeInMillis());

        maxDate.set(myYear, myMonth + 1,myDate);
        datePickerDialog.getDatePicker().setMaxDate(maxDate.getTimeInMillis());

        datePickerDialog.setMessage("날짜 선택");
        datePickerDialog.show();
    }

    void displayTime() {
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

    int dpToPx(Context context, int dp) {
        DisplayMetrics dm = context.getResources().getDisplayMetrics();
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, dm);
    }

    private void addOptionDescription() {
        linearLayout = new LinearLayout(getContext());
        LinearLayout.LayoutParams ll = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        linearLayout.setLayoutParams(ll);

        Button button = new Button(getContext());
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(dpToPx(getContext(), 28), dpToPx(getContext(), 28));
        button.setLayoutParams(lp);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OrderSheetActivity.touchPanel.removeView(linearLayout);
            }
        });
        textView = new TextView(getContext());
        textView.setTextColor(Color.BLACK);
        textView.setTextSize(18);
        textView.setText("option"+OptionAdapter.test);
        textView.setLayoutParams(ll);
        int paddingVal = dpToPx(getContext(), 4);
        textView.setPadding(paddingVal, paddingVal, paddingVal, paddingVal);

        linearLayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int parentWidth = ((ViewGroup)v.getParent()).getWidth();
                int parentHeight = ((ViewGroup)v.getParent()).getHeight();

                if(event.getAction() == MotionEvent.ACTION_DOWN){
                    //oldX = event.getX();// View 내부에서 터치한 지점의 상대 좌표값
                    //oldY = event.getY();
                }
                else if(event.getAction() == MotionEvent.ACTION_MOVE){
                    v.setX(v.getX() + event.getX() - v.getWidth()/2);
                    v.setY(v.getY() + event.getY() - v.getHeight()/2);
                }
                else if(event.getAction() == MotionEvent.ACTION_UP){
                    if (v.getX() < 0){
                        v.setX(0);
                    }
                    else if ((v.getX() + v.getWidth()) > parentWidth){
                        v.setX(parentWidth - v.getWidth());
                    }

                    if (v.getY() < 0){
                        v.setY(0);
                    }
                    else if ((v.getY() + v.getHeight()) > parentHeight){
                        v.setY(parentHeight - v.getHeight());
                    }
                }
                return true;
            }
        });

        linearLayout.addView(button);
        linearLayout.addView(textView);

        OrderSheetActivity.touchPanel.addView(linearLayout);
    }
}

