package com.example.direct_order.ordersheet;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

public class OptionSpinnerAdapter extends ArrayAdapter<String> {
    public OptionSpinnerAdapter(@NonNull Context context, int resource, @NonNull List<String> objects) {
        super(context, resource, objects);
    }

    @Override
    public int getCount() {
        return super.getCount() - 1;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = super.getView(position, convertView, parent);
        if (position == getCount()){
            ((TextView) view.findViewById(android.R.id.text1)).setText("");
            ((TextView) view.findViewById(android.R.id.text1)).setHint(getItem(getCount()));
        }
        return view;
    }


}
