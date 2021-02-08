package com.example.direct_order.ui.orderhistory;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.example.direct_order.R;

import java.util.HashMap;
import java.util.List;

public class MyListAdapter extends RecyclerView.Adapter<MyListAdapter.ViewHolder> {


    private List<String> myList;

    public MyListAdapter(List<String> myList) {
        this.myList = myList;

    }


    @Override

    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext())

                .inflate(R.layout.myorderlist, parent, false);

        return new ViewHolder(v);

    }


    @Override

    public void onBindViewHolder(final ViewHolder holder, int position) {


        String my = myList.get(position);

        holder.mText.setText(my);

        holder.mContent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Context context = v.getContext();
                //Toast.makeText(context,position+": "+ my+" click!",Toast.LENGTH_LONG).show();
                if(position==0) {
                    /*((AppCompatActivity) context).getSupportFragmentManager().beginTransaction()
                            .replace(R.id.nav_host_fragment, new Cake_shop())
                            .commit();*/
                }
            }

        });
        holder.mContent2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Context context = v.getContext();
                //Toast.makeText(context,position+": "+ my+" click!",Toast.LENGTH_LONG).show();
                if(position==0) {
                    /*((AppCompatActivity) context).getSupportFragmentManager().beginTransaction()
                            .replace(R.id.nav_host_fragment, new Cake_shop())
                            .commit();*/
                }
            }

        });

        holder.mContent3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Context context = v.getContext();
                //Toast.makeText(context,position+": "+ my+" click!",Toast.LENGTH_LONG).show();
                if(position==0) {
                    /*((AppCompatActivity) context).getSupportFragmentManager().beginTransaction()
                            .replace(R.id.nav_host_fragment, new Cake_shop())
                            .commit();*/
                }
            }

        });
    }


    @Override

    public int getItemCount() {

        return myList.size();

    }


    public class ViewHolder extends RecyclerView.ViewHolder {


        public final View mView;
        public final TextView mText;
        public final Button mContent, mContent2, mContent3;

        public ViewHolder(View view) {

            super(view);

            mView = view;
            mText=view.findViewById(R.id.orders);
            mContent = view.findViewById(R.id.write);
            mContent2 = view.findViewById(R.id.send);
            mContent3 = view.findViewById(R.id.complete);
        }

    }
}
