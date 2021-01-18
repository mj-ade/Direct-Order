package com.example.direct_order.order_sheet;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;

import com.example.direct_order.R;

import java.util.ArrayList;

public class CheckBoxTool extends LinearLayout {
    private int index;
    private ArrayList<CheckBoxTool> checkBoxToolArrayList;

    public CheckBoxTool(Context context, ArrayList<CheckBoxTool> checkBoxToolAL) {
        super(context);

        this.checkBoxToolArrayList = checkBoxToolAL;
        this.index = checkBoxToolAL.size() + 1;

        init(context);
    }

    private void init(Context context) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View subView = (View) inflater.inflate(R.layout.tool_checkbox, this, true);

        CheckBox checkBox = findViewById(R.id.checkBox);
        checkBox.setText("type"+ index);

        Button deleteButton=findViewById(R.id.delete_option);
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ViewGroup parentViewGroup = (ViewGroup) getParent();
                if (parentViewGroup != null) {
                    parentViewGroup.removeView(subView);

                    for(CheckBoxTool checkBoxTool : checkBoxToolArrayList){
                        if (index < checkBoxTool.getIndex())   //선택된 옵션이 리스트에 있는 옵션보다 작은 값이면
                            checkBoxTool.setIndex(checkBoxTool.getIndex() - 1);
                        ((CheckBox) checkBoxTool.findViewById(R.id.checkBox)).setText(checkBoxTool.getIndex() + ".");

                    }

                    checkBoxToolArrayList.remove(index - 1);
                }
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
