package com.example.direct_order.order_sheet;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.direct_order.R;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

public class OptionAdapter extends FirestoreRecyclerAdapter<Option, OptionAdapter.OptionHolder> {
    private Context context;

    public OptionAdapter(@NonNull FirestoreRecyclerOptions<Option> options, Context context) {
        super(options);
        this.context = context;
    }

    @Override
    protected void onBindViewHolder(@NonNull OptionHolder holder, int position, @NonNull Option model) {
        holder.numTextView.setText(model.getNumber() + ". ");
        holder.optionTitle.setText(model.getTitle());
        holder.optionDescription.setText(model.getDesc());
        if (model.getDesc().equals(""))
            holder.optionDescription.setVisibility(View.GONE);
        else
            holder.optionDescription.setVisibility(View.VISIBLE);

        if (holder.container.getChildCount() != 0)
            holder.container.removeAllViews();
        holder.container.addView(new OptionForm(context, model.getParentNumber(), model.getType(), model.getNumOfOption(), model.getContent(), model.getFunc(), model.getPreviewDesc()));
    }

    @NonNull
    @Override
    public OptionHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View itemView = inflater.inflate(R.layout.option_form, parent, false);
        return new OptionHolder(itemView);
    }

    public void deleteItem(int position) {
        getSnapshots().getSnapshot(position).getReference().delete();
        //notify 부를 필요 없음
    }

    public Option updateItem(int position) {
        return getSnapshots().getSnapshot(position).toObject(Option.class);
    }

    class OptionHolder extends RecyclerView.ViewHolder{
        TextView numTextView;
        TextView optionTitle;
        TextView optionDescription;
        LinearLayout container;

        public OptionHolder(View itemView) {
            super(itemView);

            numTextView = itemView.findViewById(R.id.number);
            optionTitle = itemView.findViewById(R.id.option_title);
            optionDescription = itemView.findViewById(R.id.option_description);
            container = itemView.findViewById(R.id.container);
        }
    }
}
