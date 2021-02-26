package com.example.direct_order.customermain;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.example.direct_order.R;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class CustomerMainFragment extends Fragment {
    private final String TAG = "CUSTOMER_MAIN_FRAGMENT";
    TabLayout tabLayout;
    ViewPager2 viewPager;
    String[] tabItems = {"CAKE", "CASE", "ACCESSORY", "ETC"};

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.cake_shop_fragment, container, false);
        tabLayout = root.findViewById(R.id.tab1);
        viewPager = root.findViewById(R.id.pager);
        viewPager.setAdapter(new FragmentStateAdapter(this) {
            @NonNull
            @Override
            public Fragment createFragment(int position) {
                return new MarketFragment(position);
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

        setHasOptionsMenu(true);
        return root;
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.customer_home_menu, menu);
        SearchView searchView = (SearchView) menu.findItem(R.id.search).getActionView();
        searchView.setMaxWidth(Integer.MAX_VALUE);
        searchView.setQueryHint("가게명 검색"); //검색버튼 클릭시, searchview에 힌트 추가
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            //검색버튼 눌렀을 경우
            @Override
            public boolean onQueryTextSubmit(String query) {
                Toast.makeText(getContext(), "검색 시작", Toast.LENGTH_LONG).show();
                return true;
            }
            //텍스트가 바뀔때마다 호출
            @Override
            public boolean onQueryTextChange(String newText) {
                Toast.makeText(getContext(), "검색 변경", Toast.LENGTH_SHORT).show();
                return true;
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.openmap:
                Intent intent = new Intent(getContext(), MapActivity.class);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}