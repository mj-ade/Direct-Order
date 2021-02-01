package com.example.test.ui.home;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.test.MainForSeller;
import com.example.test.R;
import com.example.test.Register_Shop;
import com.example.test.cake.Cake_shop;

import java.util.List;

public class MyListAdapter extends RecyclerView.Adapter<MyListAdapter.ViewHolder> {


    private List<String> myList;

    public MyListAdapter(List<String> myList) {
        this.myList = myList;

    }


    @Override

    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext())

                .inflate(R.layout.home_shop, parent, false);

        return new ViewHolder(v);

    }


    @Override

    public void onBindViewHolder(final ViewHolder holder, int position) {


        String my = myList.get(position);

        holder.mContent.setText(my);

        holder.mContent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Context context = v.getContext();
                Toast.makeText(context,position+": "+ my+" click!",Toast.LENGTH_LONG).show();
                if(position==0) {
                    ((AppCompatActivity) context).getSupportFragmentManager().beginTransaction()
                            .replace(R.id.nav_host_fragment, new Cake_shop())
                            .commit();
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

        public final Button mContent;

        public ViewHolder(View view) {

            super(view);

            mView = view;
            mContent = view.findViewById(R.id.shopnamelist);


        }

    }
}
