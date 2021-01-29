package com.example.direct_order.ordersheet;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.direct_order.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class OptionAdapter extends RecyclerView.Adapter<OptionAdapter.ViewHolder> {
    List<Option> items = new ArrayList<>();
    Context context;
    int optionType;

    static int test = 1;

    public OptionAdapter(Context context, List<Option> items) {
        this.context = context;
        this.items = items;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
        View itemView = inflater.inflate(R.layout.option_form, viewGroup, false);
        return new ViewHolder(itemView, context);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {
        SubOptionAdapter subOptionAdapter = new SubOptionAdapter(context, items.get(position).subOptionArrayList);
        RecyclerView subRecyclerView = viewHolder.recyclerView;
        subRecyclerView.setAdapter(subOptionAdapter);
        subRecyclerView.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));
        subRecyclerView.setHasFixedSize(true);
        subRecyclerView.setAdapter(subOptionAdapter);

        Option item = items.get(position);
        optionType = item.optionForm.getOptionType();
        viewHolder.setItem(item);

        if (optionType != OptionType.CALENDAR) {
            viewHolder.setSpinner();
        }
        else {
            viewHolder.spinner.setVisibility(View.GONE);
            viewHolder.addButton.setVisibility(View.GONE);
        }

        viewHolder.delButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                test--;
                OrderSheetActivity.touchPanel.removeView(item.getOptionForm().linearLayout);
                for (int i = position; i < items.size(); i++) { //삭제된 애들부터 번호 하나씩 줄이기
                    items.get(i).setNumber(items.get(i).getNumber() - 1);
                    items.get(i).optionForm.textView.setText("@option" + items.get(i).getNumber());
                }
                viewHolder.container.removeAllViews(); //container에 그린 옵션 삭제
                items.remove(position);
                notifyItemRemoved(position);
                notifyItemRangeChanged(position, items.size());
            }
        });

        if(items.get(position).subOptionArrayList == null) {
            items.get(position).subOptionArrayList = new ArrayList<>();
        }

        viewHolder.addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (viewHolder.subOptionType == 6) {
                    Toast.makeText(context, "추가할 옵션을 선택해주세요", Toast.LENGTH_SHORT).show();
                    return;
                }
                items.get(position).subOptionArrayList.
                        add(new SubOption(
                                items.get(position).subOptionArrayList.size()+1,
                                "sub",
                                new SubOptionForm(context, viewHolder.subOptionType)));

                viewHolder.container.setVisibility(View.GONE);  //남겨둘까?
                //subOptionAdapter.notifyDataSetChanged();
                notifyDataSetChanged();
            }
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        Context mContext;
        TextView numTextView;
        EditText optionTitle;
        LinearLayout container;
        RecyclerView recyclerView;
        Button addButton, delButton;
        Spinner spinner;
        int subOptionType;

        public ViewHolder(View itemView, Context context) {
            super(itemView);
            mContext = context;

            numTextView = itemView.findViewById(R.id.number);
            optionTitle = itemView.findViewById(R.id.option_title);

            container = itemView.findViewById(R.id.container);
            recyclerView = itemView.findViewById(R.id.sub_recyclerView);

            delButton = itemView.findViewById(R.id.delButton);
            spinner = itemView.findViewById(R.id.option_spinner);
            addButton = itemView.findViewById(R.id.addButton);
        }

        public void setItem(Option item) {
            numTextView.setText(item.getNumber() + ". ");
            optionTitle.setText(item.getTitle());

            if(item.getLayout().getParent()!=null)
                ((ViewGroup)item.getLayout().getParent()).removeView(item.getLayout());
            container.addView(item.getLayout());
        }

        public void setSpinner() {
            spinner.setPrompt("옵션 추가");
            List<String> optionList = new ArrayList<>();
            optionList.addAll(Arrays.asList(mContext.getResources().getStringArray(R.array.option_array)));
            optionList.add("옵션 추가");
            OptionSpinnerAdapter adapter = new OptionSpinnerAdapter(mContext,R.layout.support_simple_spinner_dropdown_item, optionList);
            adapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
            spinner.setAdapter(adapter);
            spinner.setSelection(adapter.getCount());
            spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    subOptionType = position;
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) { }
            });
        }
    }
}
