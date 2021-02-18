package com.example.direct_order.cake;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.direct_order.R;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class Cake_shop extends Fragment {
    private CakeShopViewModel mViewModel;
    TabLayout tabLayout;
    ViewPager2 viewPager;
    String[] tabItems = {"주문 보기", "주문서 보기", "매출 보기", "리뷰 보기"};

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mViewModel = new ViewModelProvider(this).get(CakeShopViewModel.class);
        View root = inflater.inflate(R.layout.cake_shop_fragment, container, false);
        tabLayout = root.findViewById(R.id.tab1);
        viewPager = root.findViewById(R.id.pager);

        viewPager.setAdapter(new FragmentStateAdapter(this) {
            @NonNull
            @Override
            public Fragment createFragment(int position) {
                switch (position) {
                    case 0:
                        return new Fragment(R.layout.tab__orders_fragment);
                    case 1:
                        return new Tab_Order_Form();
                    case 2:
                        return new Fragment(R.layout.tab__sales_fragment);
                    default:
                        return new Fragment(R.layout.tab__reviews_fragment);
                }
            }

            @Override
            public int getItemCount() {
                return tabItems.length;
            }
        });

        new TabLayoutMediator(tabLayout, viewPager,
                new TabLayoutMediator.TabConfigurationStrategy() {
                    @Override public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
                        tab.setText(tabItems[position]);
                    }
                }).attach();

        mViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {

            }
        });

        return root;
    }
}
