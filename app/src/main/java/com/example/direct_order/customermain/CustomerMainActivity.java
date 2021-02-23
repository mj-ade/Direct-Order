package com.example.direct_order.customermain;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.direct_order.R;

import java.util.Arrays;
import java.util.List;

public class CustomerMainActivity extends AppCompatActivity {

    private MyRecyclerViewAdapter myAdapter;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customermain);

        init();

        getCustomerMainData();
    }

    private void init(){
        RecyclerView myRecyclerView = (RecyclerView) findViewById(R.id.GridView);
        GridLayoutManager myLayoutManager = new GridLayoutManager(this, 2);
        myRecyclerView.setLayoutManager(myLayoutManager);

        myAdapter = new MyRecyclerViewAdapter();
        myRecyclerView.setAdapter(myAdapter);
    }

    private void getCustomerMainData(){
        //임의 데이터
        List<String> listMarketname = Arrays.asList("market1","market2","market3","market4","market5");
        List<Integer> listImgId = Arrays.asList(R.drawable.profile3, R.drawable.shop,
                R.drawable.profile3, R.drawable.shop, R.drawable.profile3);
        for (int i = 0; i < listImgId.size(); i++) {
            CustomerMainData mdata = new CustomerMainData();
            mdata.setMarket_name(listMarketname.get(i));
            mdata.setImgId(listImgId.get(i));

            myAdapter.addData(mdata);
        }
        myAdapter.notifyDataSetChanged();
    }

    //검색기능
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);

        SearchView searchView = (SearchView) menu.findItem(R.id.search).getActionView();
        //searchView.setSubmitButtonEnabled(true);
        searchView.setMaxWidth(Integer.MAX_VALUE);
        searchView.setQueryHint("가게명 검색"); //검색버튼 클릭시, searchview에 힌트 추가
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            //검색버튼 눌렀을 경우
            @Override
            public boolean onQueryTextSubmit(String query) {
                //TextView text = (TextView)findViewById(R.id.txtresult);
                //text.setText(query+"를 검색합니다");
                Toast.makeText(getApplicationContext(),"검색시작", Toast.LENGTH_LONG).show();
                return true;
            }
            //텍스트가 바뀔때마다 호출
            @Override
            public boolean onQueryTextChange(String newText) {
                //TextView text = (TextView)findViewById(R.id.txtsearch);
                //text.setText("검색식 : "+newText);
                Toast.makeText(getApplicationContext(),"검색변경", Toast.LENGTH_SHORT).show();
                return true;
            }
        });

        return super.onCreateOptionsMenu(menu);
    }

    //Map창 열기
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){
            case R.id.openmap:
                Intent intent = new Intent(getApplicationContext(), MapActivity.class);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}