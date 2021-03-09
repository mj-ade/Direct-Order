package com.example.direct_order.sellermain;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.example.direct_order.R;
import com.example.direct_order.orderlist.OrderListFragment;
import com.example.direct_order.reviewlist.ReviewListFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

public class SellerMainFragment extends Fragment {
    private final String TAG = "SellerMainFragment";
    private boolean isExist;
    private SellerMainViewModel mViewModel;
    TabLayout tabLayout;
    ViewPager2 viewPager;
    String[] tabItems = {"주문 보기", "주문서 보기", "리뷰 보기"};

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mViewModel = new ViewModelProvider(this).get(SellerMainViewModel.class);
        View root = inflater.inflate(R.layout.fragment_user_main, container, false);
        tabLayout = root.findViewById(R.id.tab1);
        viewPager = root.findViewById(R.id.pager);

        new OnOrderSheetCountListener() {
            @Override
            public void onOrderSheetCount() {
                checkOptionExist();
            }
        }.onOrderSheetCount();

        viewPager.setAdapter(new FragmentStateAdapter(this) {
            @NonNull
            @Override
            public Fragment createFragment(int position) {
                switch (position) {
                    case 0:
                        return new OrderListFragment();
                    case 1:
                        return new OrderSheetFragment(isExist);
                    case 2:
                        return new ReviewListFragment();
                    default:
                        return null;
                }
            }

            @Override
            public int getItemCount() {
                return tabItems.length;
            }
        });

        new TabLayoutMediator(tabLayout, viewPager, new TabLayoutMediator.TabConfigurationStrategy() {
            @Override
            public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
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

    private void checkOptionExist() {
        CollectionReference optionRef = FirebaseFirestore.getInstance()
                .collection("markets")
                .document(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .collection("OrderSheet")
                .document("sheet")
                .collection("Options");
        optionRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    if (task.getResult().size() > 0) {
                        isExist = true;
                        Log.d(TAG, "cardview");
                    }
                    else {
                        isExist = false;
                        Log.d(TAG, "button");
                    }
                }
                else
                    Log.d(TAG, "Error getting documents: ", task.getException());
            }
        });
    }
}