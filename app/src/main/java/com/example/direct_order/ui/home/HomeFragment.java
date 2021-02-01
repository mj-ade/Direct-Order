package com.example.direct_order.ui.home;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.direct_order.R;
import com.example.direct_order.Register_Shop;
import com.example.direct_order.cake.Cake_shop;

import java.util.ArrayList;
import java.util.List;


public class HomeFragment extends Fragment {
    private HomeViewModel homeViewModel;
    String myValue;
    List<String>  myList = new ArrayList<>();
    private MyListAdapter mAdapter;
    RecyclerView mRecyclerView;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);
        View root = inflater.inflate(R.layout.fragment_home, container, false);
        //final TextView textView = root.findViewById(R.id.text_home);
        Button button = root.findViewById(R.id.btn_add);
        //final TextView textView = root.findViewById(R.id.text_home);
        homeViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                //textView.setText(s);
            }
        });

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // do something
                Intent intent = new Intent(getActivity(), Register_Shop.class);
                startActivity(intent);

            }
        });

            mRecyclerView = root.findViewById(R.id.rv2);


            myList.add("Cake Shop");
           // myList.add("Macron Shop");
           // myList.add("Cake Shop2");
           // myList.add("Phone Case Shop");
            if (getArguments() != null) {
                myValue = getArguments().getString("마켓");
                Toast.makeText(getContext(),myValue,Toast.LENGTH_LONG).show();
                myList.add(myValue);
                //mAdapter.notifyDataSetChanged();
            }

            mRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(),2));

            mAdapter = new MyListAdapter(myList);

            mRecyclerView.setAdapter(mAdapter);



        return root;

    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

            //mAdapter.notifyDataSetChanged();
            //mAdapter.notifyItemChanged(myList.size()-1);
            //Toast.makeText(getActivity(), "신규 마켓 \'"+myValue+"\'이 등록되었습니다.", Toast.LENGTH_LONG).show();


    }

}
