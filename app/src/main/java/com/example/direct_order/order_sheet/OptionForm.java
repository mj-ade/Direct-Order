package com.example.direct_order.order_sheet;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.direct_order.R;

import java.util.ArrayList;

public class OptionForm extends LinearLayout {
    private int index;
    private ArrayList<OptionForm> optionFormArrayList = new ArrayList<>();

    private ArrayList<CheckBoxTool> checkBoxToolArrayList = new ArrayList<>();

    public OptionForm(Context context, ArrayList<OptionForm> optionFormAL) {
        super(context);

        this.optionFormArrayList = optionFormAL;
        this.index = optionFormAL.size() + 1;

        init(context);
    }

    private void init(Context context) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View optionFormView = (View) inflater.inflate(R.layout.option_form, this, true);

        LinearLayout sub_container = findViewById(R.id.sub_container);
        addItem(sub_container);

        TextView textView = findViewById(R.id.option_number);
        textView.setText(index + ".");

        Button addButton = findViewById(R.id.add_option);
        addButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                addItem(sub_container);
            }
        });

        Button deleteButton = findViewById(R.id.delete_all_option);
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

    public void addItem(LinearLayout container) {
        checkBoxToolArrayList.add(new CheckBoxTool(getContext(), checkBoxToolArrayList));
        container.addView(checkBoxToolArrayList.get(checkBoxToolArrayList.size()-1));
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }
}
