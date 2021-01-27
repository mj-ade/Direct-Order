package com.example.direct_order.ordersheet;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.direct_order.R;

import java.util.ArrayList;
import java.util.List;


public class SubOptionAdapter extends RecyclerView.Adapter<SubOptionAdapter.ViewHolder2> {
    Context context;
    List<SubOption> subOptionList = new ArrayList<>();
    int subOptionType;

    public SubOptionAdapter(Context context, List<SubOption> subOptionList) {
        this.context = context;
        this.subOptionList = subOptionList;
    }

    @NonNull
    @Override
    public SubOptionAdapter.ViewHolder2 onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.sub_option_form, parent, false);
        return new ViewHolder2(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SubOptionAdapter.ViewHolder2 holder, int position) {
        SubOption subOption = subOptionList.get(position);
        holder.subNumberText.setText(subOption.getNumber()+". ");

        subOptionType = subOption.subOptionForm.getOptionType();
        holder.setItem(subOption);

        holder.subDelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, "삭제", Toast.LENGTH_SHORT).show();
                //삭제
                for (int i = position; i < subOptionList.size(); i++) { //삭제된 애들부터 번호 하나씩 줄이기
                    subOptionList.get(i).setNumber(subOptionList.get(i).getNumber() - 1);
                }
                holder.subcontainer.removeAllViews(); //container에 그린 옵션 삭제
                subOptionList.remove(position);
                notifyItemRemoved(position);
                notifyItemRangeChanged(position, subOptionList.size());
            }
        });
    }

    @Override
    public int getItemCount() {
        return subOptionList == null ? 0 : subOptionList.size();//return person2List.size();
    }

    public class ViewHolder2 extends RecyclerView.ViewHolder {
        TextView subNumberText;
        EditText subOptionTitle;
        Button subDelButton;
        LinearLayout subcontainer;

        public ViewHolder2(@NonNull View itemView) {
            super(itemView);

            subNumberText = itemView.findViewById(R.id.sub_number);
            subOptionTitle = itemView.findViewById(R.id.sub_option_title);
            subDelButton = itemView.findViewById(R.id.sub_delButton);
            subcontainer = itemView.findViewById(R.id.sub_container);
        }

        public void setItem(SubOption item) {
            subNumberText.setText(item.getNumber()+". ");
            subOptionTitle.setText(item.getTitle());

            if(item.getLayout().getParent()!=null)
                ((ViewGroup)item.getLayout().getParent()).removeView(item.getLayout());
            subcontainer.addView(item.getLayout());
        }
    }
}
