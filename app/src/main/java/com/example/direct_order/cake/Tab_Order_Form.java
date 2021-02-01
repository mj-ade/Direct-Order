package com.example.direct_order.cake;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.direct_order.R;
import com.example.direct_order.cake.TabOrderFormViewModel;
import com.google.android.material.tabs.TabLayout;


public class Tab_Order_Form extends Fragment {

    private Button btn;
    private TabOrderFormViewModel tabOrderFormViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_tab__order__form, container, false);
        btn=root.findViewById(R.id.add_form);
        btn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                //order form 생성 activity로 이동
            }
        });
        return root;
    }

  /*
    private static final String ARG_COUNT = "param1";
    private Integer counter;


  {
        tabOrderFormViewModel =
                new ViewModelProvider(this).get(TabOrderFormViewModel.class);

        final TextView textView = root.findViewById(R.id.tv_counter);
        tabOrderFormViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });

        return root;
    }

    public static Tab_Order_Form newInstance(Integer counter){
        Tab_Order_Form fragment = new Tab_Order_Form();
        Bundle args = new Bundle();
        args.putInt(ARG_COUNT, counter);
        fragment.setArguments(args);
        return fragment;
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getArguments()!=null)
            counter=getArguments().getInt(ARG_COUNT);
    }

    @Override public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //view.setBackgroundColor(ContextCompat.getColor(getContext(), TabItem[counter]));
        //TextView textViewCounter = view.findViewById(R.id.tv_counter);
        //textViewCounter.setText("Fragment No " + (counter+1));
    }*/
}
