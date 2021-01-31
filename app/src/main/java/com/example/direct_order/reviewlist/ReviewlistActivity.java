package com.example.direct_order.reviewlist;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.direct_order.R;

import java.util.Arrays;
import java.util.List;

public class ReviewlistActivity extends AppCompatActivity {

    private ReviewAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reviewlist);

        init();

        getData();
    }
    private void init() {
        RecyclerView recyclerView = findViewById(R.id.recyclerView);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);

        adapter = new ReviewAdapter();
        recyclerView.setAdapter(adapter);
    }

    private void getData() {
        // 임의의 데이터입니다.
        List<Integer> listTitle = Arrays.asList(5, 4, 3, 3, 4, 4, 5, 5, 5, 5, 5, 5, 3, 2, 5, 1);//별점
        List<Integer> listResId = Arrays.asList(
                R.drawable.ic_launcher_background,
                R.drawable.ic_launcher_foreground,
                R.drawable.ic_launcher_background,
                R.drawable.ic_launcher_foreground,
                R.drawable.ic_launcher_background,
                R.drawable.ic_launcher_foreground,
                R.drawable.ic_launcher_foreground,
                R.drawable.ic_launcher_background,
                R.drawable.ic_launcher_background,
                R.drawable.ic_launcher_foreground,
                R.drawable.ic_launcher_background,
                R.drawable.ic_launcher_foreground,
                R.drawable.ic_launcher_background,
                R.drawable.ic_launcher_foreground,
                R.drawable.ic_launcher_background,
                R.drawable.ic_launcher_foreground);
        List<String> listContent = Arrays.asList(
                "review content1",
                "review content2",
                "review content3.",
                "review content4.",
                "review content5",
                "review content6",
                "review content7",
                "review content8",
                "review content9",
                "review content10",
                "review content11",
                "review content12",
                "review content13",
                "review content14",
                "review content15",
                "review content16"
        );
        for (int i = 0; i < listTitle.size(); i++) {
            // 각 List의 값들을 data 객체에 set 해줍니다.
            ReviewData data = new ReviewData();
            data.setTitle(listTitle.get(i));
            data.setContent(listContent.get(i));
            data.setResId(listResId.get(i));

            // 각 값이 들어간 data를 adapter에 추가합니다.
            adapter.addItem(data);
        }

        // adapter의 값이 변경되었다는 것을 알려줍니다.
        adapter.notifyDataSetChanged();
    }
}